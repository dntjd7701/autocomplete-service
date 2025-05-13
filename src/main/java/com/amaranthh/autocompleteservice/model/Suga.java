package com.amaranthh.autocompleteservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "suga_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suga {
    @Id
    private String sugaCd;
    @Column(name = "prscNm")
    private String nameKo;
    @Column(name = "sugaEnm")
    private String nameEn;
    private String category;

}
