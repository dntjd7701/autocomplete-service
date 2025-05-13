package com.amaranthh.autocompleteservice.service.impl;

import com.amaranthh.autocompleteservice.service.IAutoCompleteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoCompleteService implements IAutoCompleteService {

    @Override
    public List<String> getSuggestions(String keyword) throws Exception {
        return null;
    }
}
