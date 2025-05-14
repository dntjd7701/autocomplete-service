package com.amaranthh.autocompleteservice.service.impl;

import com.amaranthh.autocompleteservice.model.AutoComplete;
import com.amaranthh.autocompleteservice.model.Suga;
import com.amaranthh.autocompleteservice.repository.SugaRepository;
import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AutoCompleteService implements IAutoCompleteService {

    private final StringRedisTemplate redisTemplate;
    private final ElasticsearchService elasticsearchService;
    private final SugaRepository sugaRepository;

    @Override
    public void sync(AutoComplete param) {
        List<Suga> result = sugaRepository.findAvailableSugaByDate(param.getCoCd(), param.getDivCd(), "20250514");
        List<AutoComplete> docs = result.stream().map(suga ->
                AutoComplete.builder()
                        .id(param.getCoCd() + ":" + suga.getSugaCd())
                        .coCd(param.getCoCd())
                        .divCd(param.getDivCd())
                        .category(param.getCategory())
                        .code(suga.getSugaCd())
                        .name(suga.getPrscNm())
                        .build()).collect(Collectors.toList());

        elasticsearchService.buildIndex(docs);
    }

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
        return elasticsearchService.search(param);
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
