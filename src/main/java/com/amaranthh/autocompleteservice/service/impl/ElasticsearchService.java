package com.amaranthh.autocompleteservice.service.impl;

import com.amaranthh.autocompleteservice.model.AutoComplete;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public List<AutoComplete> search(Map<String, String> param) {
        String _keyword = param.get("keyword");
        BoolQueryBuilder bool = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("coCd", param.get("coCd")))     // ✅ 필터
                .filter(QueryBuilders.termQuery("divCd", param.get("divCd")))      // ✅ 필터
                .filter(QueryBuilders.termQuery("category", param.get("category")))      // ✅ 필터
                .should(QueryBuilders.matchPhrasePrefixQuery("code", _keyword))     // ✅ 검색
                .should(QueryBuilders.matchPhrasePrefixQuery("name.ko", _keyword).boost(2.0f))
                .should(QueryBuilders.matchPhrasePrefixQuery("name.en", _keyword));

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(bool)
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<AutoComplete> hits = elasticsearchRestTemplate.search(query, AutoComplete.class);
        return  hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public void buildIndex(List<AutoComplete> docs) {
        int batchSize = 1000;
        IndexCoordinates index = IndexCoordinates.of("autocomplete-index");

        int totalSize = docs.size();
        int totalBatches = (int) Math.ceil((double) totalSize / batchSize);
        long start = System.currentTimeMillis();

        log.info("🚀 인덱싱 시작 - 총 {}건, 배치당 {}건 (총 {}배치)", totalSize, batchSize, totalBatches);

        for (int i = 0; i < totalSize; i += batchSize) {
            int currentBatch = (i / batchSize) + 1;

            List<AutoComplete> batch = docs.subList(i, Math.min(i + batchSize, totalSize));
            List<IndexQuery> queries = batch.stream().map(doc -> {
                IndexQuery query = new IndexQuery();
                query.setId(doc.getId());
                query.setObject(doc);
                return query;
            }).collect(Collectors.toList());

            elasticsearchRestTemplate.bulkIndex(queries, index);

            log.info("✅ [{} / {}] 배치 완료 (진행률: {}%) - 누적 처리 문서: {}건",
                    currentBatch,
                    totalBatches,
                    (currentBatch * 100) / totalBatches,
                    i + batch.size()
            );
        }

        long duration = System.currentTimeMillis() - start;
        log.info("🎉 인덱싱 완료! 총 소요 시간: {}ms", duration);
    }

    public void delete(Map<String, String> param) {
        elasticsearchRestTemplate.delete("abc123", IndexCoordinates.of("autocomplete-index"));
    }

    public void update(Map<String, String> param) {
//        IndexQuery query = new IndexQuery();
//        query.setId("abc123");
//        query.setObject(autoCompleteDoc); // 도큐먼트 객체
//
//        elasticsearchRestTemplate.index(query, IndexCoordinates.of("autocomplete-suga"));
    }
}
