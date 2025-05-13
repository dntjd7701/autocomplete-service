package com.amaranthh.autocompleteservice.service;

import java.util.List;

public interface IAutoCompleteService {
    List<String> getSuggestions(String keyword) throws Exception;
}
