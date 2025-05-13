package com.amaranthh.autocompleteservice.controller;

import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutoCompleteController.class) // ✅ (1) Controller만 테스트하는 슬라이스 테스트
public class AutoCompleteControllerTest {

    @Autowired
    private MockMvc mockMvc; // ✅ (2) 실제 HTTP 요청 없이 MVC 흐름을 테스트할 수 있는 도구

    // ✅ (3) 컨트롤러에서 의존하는 빈을 가짜(mock)로 주입함
    // 실제 서비스 클래스가 없어도 테스트할 수 있게 해줌
    @MockBean
    private IAutoCompleteService iAutoCompleteService;

    @Test
    @DisplayName("자동완성 키워드가 주어지면 결과를 반환한다") // ✅ (4) 테스트 이름을 한글로 보기 쉽게 지정
    void shouldReturnSuggestionsForKeyword() throws Exception {
        // given
        String keyword = "pen";
        List<String> mockResult = List.of("pen", "pencil", "penguin");

        // ✅ (5) autoCompleteService.getSuggestions("pen") 호출 시 mockResult 반환되도록 설정
        Mockito.when(iAutoCompleteService.getSuggestions(keyword)).thenReturn(mockResult);

        // when & then
        mockMvc.perform(get("/api/autocomplete")  // ✅ (6) GET 요청 시뮬레이션
                        .param("keyword", keyword)) // 쿼리 파라미터 추가
                .andExpect(status().isOk())         // ✅ (7) HTTP 200 OK 응답 확인
                .andExpect(jsonPath("$.length()").value(mockResult.size())) // JSON 배열 길이 확인
                .andExpect(jsonPath("$[0]").value("pen"))     // 1번째 결과 확인
                .andExpect(jsonPath("$[1]").value("pencil"))  // 2번째 결과 확인
                .andExpect(jsonPath("$[2]").value("penguin")); // 3번째 결과 확인
    }
}
