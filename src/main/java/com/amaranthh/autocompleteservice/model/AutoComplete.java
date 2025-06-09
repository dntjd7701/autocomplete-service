package com.amaranthh.autocompleteservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoComplete {
    @Id
    @JsonIgnore
    private String id;  // ES 문서의 _id
    private String coCd;
    private String divCd;
    private String code;
    private String name;
    private String category;
}