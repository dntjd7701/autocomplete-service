package com.amaranthh.autocompleteservice.controller;

import com.amaranthh.autocompleteservice.model.AutoComplete;
import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/autocomplete")
public class AutoCompleteController {

    private final IAutoCompleteService iAutoCompleteService;

    @PostMapping("/getPopularKeyword")
    public ResponseEntity<Map<String, Double>> asyncSuga(@RequestBody Map<String, Integer> range) throws Exception {
        Map<String, Double> suggestions = iAutoCompleteService.getPopularKeyword(range);
        return ResponseEntity.ok(suggestions);
    }


    @PostMapping("/getSuggestions")
    public ResponseEntity<List<AutoComplete>> getSuggestions(@RequestBody Map<String, String> param) throws Exception {
        List<AutoComplete> suggestions = iAutoCompleteService.getSuggestions(param);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/getPopularKeyword")
    public ResponseEntity<Map<String, Double>> getPopularKeyword(@RequestBody Map<String, Integer> range) throws Exception {
        Map<String, Double> suggestions = iAutoCompleteService.getPopularKeyword(range);
        return ResponseEntity.ok(suggestions);
    }

}
