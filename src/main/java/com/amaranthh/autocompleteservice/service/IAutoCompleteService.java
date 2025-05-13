package com.amaranthh.autocompleteservice.service;

import com.amaranthh.autocompleteservice.model.AutoComplete;

import java.util.List;
import java.util.Map;

public interface IAutoCompleteService {
    List<AutoComplete> getSuggestions(Map<String, String>  param) throws Exception;
    Map<String, Double> getPopularKeyword(Map<String, Integer> range) throws Exception;
}
