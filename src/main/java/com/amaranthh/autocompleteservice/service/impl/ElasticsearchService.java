package com.amaranthh.autocompleteservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.amaranthh.autocompleteservice.model.AutoComplete;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    private static final String INDEX_NAME = "autocomplete-index";

    // 인덱스 생성
    public void createAutocompleteIndex() throws IOException {
        boolean exists = elasticsearchClient.indices().exists(e -> e.index(INDEX_NAME)).value();
        if (exists) {
            System.out.println("Index already exists.");
            return;
        }

        // JSON 파일 읽어오기
        ClassPathResource resource = new ClassPathResource(INDEX_NAME + ".json");
        String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Index 생성
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index("autocomplete-index")
                .withJson(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .build();

        CreateIndexResponse response = elasticsearchClient.indices().create(createIndexRequest);

        System.out.println("Index created: " + response.acknowledged());
    }

    // 문서 등록 (Indexing) / 수정(delete + insert)
    public void indexDocument(String id, AutoComplete doc) throws IOException {
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(INDEX_NAME)
                .id(id)
                .document(doc)
        );

        log.info("Document indexed with version: {}", response.version());
    }


    public List<AutoComplete> search(Map<String, String> param) throws IOException {
        String _keyword = param.get("keyword");

        SearchResponse<AutoComplete> response = elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .size(10)
                        .query(q -> q
                                .bool(b -> b
                                        // 🔶 filter → 반드시 일치해야 하는 필터 조건 (score 에 영향 X, 빠름)
                                        .filter(f -> f.term(t -> t.field("coCd").value(param.get("coCd"))))   // coCd 가 정확히 param.get("coCd") 와 일치
                                        .filter(f -> f.term(t -> t.field("divCd").value(param.get("divCd")))) // divCd 가 정확히 param.get("divCd") 와 일치
                                        .filter(f -> f.term(t -> t.field("category").value(param.get("category")))) // category 가 정확히 param.get("category") 와 일치

                                        // 🔶 should → OR 조건, relevance score 에 영향 줌 (match 또는 matchPhrasePrefix 로 사용)
                                        .should(s1 -> s1.matchPhrasePrefix(mp -> mp
                                                .field("code")           // code 필드에서
                                                .query(_keyword)))       // _keyword로 시작하는 문장(prefix) 매칭

                                        .should(s2 -> s2.matchPhrasePrefix(mp -> mp
                                                .field("name.ko")        // name.ko 필드에서
                                                .query(_keyword)         // _keyword로 시작하는 문장(prefix) 매칭
                                                .boost(2.0f)))           // 이 조건에 가중치(중요도) 2배 부여 → 우선순위 높음

                                        .should(s3 -> s3.matchPhrasePrefix(mp -> mp
                                                .field("name.en")        // name.en 필드에서
                                                .query(_keyword)))       // _keyword로 시작하는 문장(prefix) 매칭
                                )
                        ),
                AutoComplete.class
        );

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
    };

    public void buildIndex(List<AutoComplete> docs) throws IOException {
        int batchSize = 1000;
        int totalSize = docs.size();
        int totalBatches = (int) Math.ceil((double) totalSize / batchSize);
        long start = System.currentTimeMillis();

        log.info("🚀 인덱싱 시작 - 총 {}건, 배치당 {}건 (총 {}배치)", totalSize, batchSize, totalBatches);

        for (int i = 0; i < totalSize; i += batchSize) {
            int currentBatch = (i / batchSize) + 1;

            List<BulkOperation> operations = docs.subList(i, Math.min(i + batchSize, totalSize))
                    .stream()
                    .map(doc -> BulkOperation.of(b -> b
                            .index(idx -> idx
                                    .index(INDEX_NAME)
                                    .id(doc.getId())
                                    .document(doc)
                            )
                    ))
                    .collect(Collectors.toList());

            BulkRequest bulkRequest = new BulkRequest.Builder()
                    .index(INDEX_NAME)
                    .operations(operations)
                    .build();

            BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest);

            log.info("✅ [{} / {}] 배치 완료 (진행률: {}%) - 누적 처리 문서: {}건 (errors={})",
                    currentBatch,
                    totalBatches,
                    (currentBatch * 100) / totalBatches,
                    i + operations.size(),
                    bulkResponse.errors()
            );
        }

        long duration = System.currentTimeMillis() - start;
        log.info("🎉 인덱싱 완료! 총 소요 시간: {}ms", duration);
    }

    public void delete(String id) throws IOException {
        DeleteResponse response = elasticsearchClient.delete(d -> d
                .index(INDEX_NAME)
                .id(id)
        );

        log.info("Deleted document id={}, result={}", id, response.result().name());
      }

}
