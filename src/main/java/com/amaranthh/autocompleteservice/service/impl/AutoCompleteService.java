package com.amaranthh.autocompleteservice.service.impl;

import com.amaranthh.autocompleteservice.model.AutoComplete;
import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoCompleteService implements IAutoCompleteService {
    private final StringRedisTemplate redisTemplate;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<AutoComplete> getSuggestions(Map<String, String> param) throws Exception {
        String _keyword = param.get("keyword");
        /* 인기검색어 처리 count 집계 처리하기 */
        if(_keyword == null || _keyword.isEmpty()){
            return new ArrayList<>();
        }

        /* 2글자 이상 검색 */
        if(_keyword.length() < 2) {
            return new ArrayList<>();
        }

        // ZREVRANGE popular:keywords 0 9 WITHSCORES
//        redisTemplate.opsForZSet().incrementScore("popular-keyword", _keyword, 1);

        BoolQueryBuilder bool = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("companyId", "H001"))     // ✅ 필터
                .filter(QueryBuilders.termQuery("category", "환자"))      // ✅ 필터
                .should(QueryBuilders.matchPhrasePrefixQuery("code", _keyword))     // ✅ 검색
                .should(QueryBuilders.matchPhrasePrefixQuery("name.ko", _keyword).boost(2.0f))
                .should(QueryBuilders.matchPhrasePrefixQuery("name.en", _keyword));


        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(bool)
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<AutoComplete> hits = elasticsearchRestTemplate.search(query, AutoComplete.class);

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Double> getPopularKeyword(Map<String, Integer> range) throws Exception {
        int start = range.get("start");
        int end = range.get("end");

        // ZREVRANGE WITHSCORES
        Set<ZSetOperations.TypedTuple<String>> result =
                redisTemplate.opsForZSet().reverseRangeWithScores("popular-keyword", start, end);

        // 결과를 Map<String, Double> 형태로 변환
        if (result == null) return Collections.emptyMap();

        Map<String, Double> keywordScoreMap = new LinkedHashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : result) {
            keywordScoreMap.put(tuple.getValue(), tuple.getScore());
        }

        return keywordScoreMap;
    }
}
