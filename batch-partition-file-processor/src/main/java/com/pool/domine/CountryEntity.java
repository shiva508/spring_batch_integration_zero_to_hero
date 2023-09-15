package com.pool.domine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Entity
@Table(name = "country")
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity implements Serializable {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "continent")
    private String continent;

    @Column(name = "region")
    private String region ;

    @Column(name = "surfacearea")
    private Long surfaceArea;

    @Column(name = "indepyear")
    private Long indepYear;

    @Column(name = "population")
    private Long population;

    @Column(name = "lifeexpectancy")
    private Long lifeExpectancy;

    @Column(name = "gnp")
    private Double gnp;

    @Column(name = "gnpold")
    private Double gnpOld;

    @Column(name = "localname")
    private String localName;

    @Column(name = "governmentform")
    private String governmentForm;

    @Column(name = "headofstate")
    private String  headOfState;

    @Column(name = "capital")
    private Long capital;

    @Column(name = "code2")
    private String code2;
}
