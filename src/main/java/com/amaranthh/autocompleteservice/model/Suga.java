package com.amaranthh.autocompleteservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="suga")
@Getter
@Setter
public class Suga {
    @Id
    private String sugaCd;
    private String prscNm;
}
