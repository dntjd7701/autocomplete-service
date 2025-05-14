package com.amaranthh.autocompleteservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "autocomplete-index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoComplete {
    @Id
    private String id;  // ES 문서의 _id
    private String companyId;
    private String code;
    private String name;
    private String category;
}