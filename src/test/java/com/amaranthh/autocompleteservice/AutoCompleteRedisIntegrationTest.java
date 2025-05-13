//package com.amaranthh.autocompleteservice;
//
//import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
//import com.amaranthh.autocompleteservice.service.impl.AutoCompleteService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//public class AutoCompleteRedisIntegrationTest {
////    @Test
////    void 레디스_등록조회() throws Exception {
////        // given
////        String keyword = "pen";
////        String redisKey = "auto::" + keyword;
////        List<String> redisValue = List.of("pen", "pencil", "penguin");
////
////        redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofMinutes(5));
////
////        // when & then
////        mockMvc.perform(get("/getSuggestions").param("keyword", keyword))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.length()").value(3))
////                .andExpect(jsonPath("$[0]").value("pen"))
////                .andExpect(jsonPath("$[1]").value("pencil"))
////                .andExpect(jsonPath("$[2]").value("penguin"));
////    }
//
//    @Test
//    void 레디스_인기검색어조회() throws Exception {
//        // given
//        Map<String, Integer> range = new HashMap<>();
//        range.put("start", 0);
//        range.put("end", 0);
//
//
//        String keyword = "pen";
//        String redisKey = "auto::" + keyword;
//        List<String> redisValue = List.of("pen", "pencil", "penguin");
//
//        // when & then
//        mockMvc.perform(get("/getSuggestions").param("keyword", keyword))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(3))
//                .andExpect(jsonPath("$[0]").value("pen"))
//                .andExpect(jsonPath("$[1]").value("pencil"))
//                .andExpect(jsonPath("$[2]").value("penguin"));
//    }
//}
