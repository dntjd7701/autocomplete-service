package com.amaranthh.autocompleteservice.controller;

import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/autocomplete")
public class AutoCompleteController {

    private final IAutoCompleteService iAutoCompleteService;

    @GetMapping
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String keyword) throws Exception {
        List<String> suggestions = iAutoCompleteService.getSuggestions(keyword);
        return ResponseEntity.ok(suggestions);
    }
}
