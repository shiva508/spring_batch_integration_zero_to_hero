package com.pool.domine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name="TBL_CREDIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long creditId;
    private String personId;
    private String movieId;
    private String name;
    private String character;
    private String role;



}
