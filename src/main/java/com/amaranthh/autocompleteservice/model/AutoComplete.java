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

    @Field(name = "code", type = FieldType.Text)
    private String code;  // 예: 수가 코드

    @Field(name = "name", type = FieldType.Text)
    private String name;  // 예: 수가명
}