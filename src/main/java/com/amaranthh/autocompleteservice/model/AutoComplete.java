package com.amaranthh.autocompleteservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;

//@Document(indexName = "autocomplete-index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoComplete {
    @Id
    private String id;  // ES 문서의 _id
    private String coCd;
    private String divCd;
    private String code;
    private String name;
    private String category;
}