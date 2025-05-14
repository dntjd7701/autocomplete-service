package com.amaranthh.autocompleteservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="suga")
@Getter
@Setter
public class Suga {
    @Id
    private String sugaCd;
    private String prscNm;
}
