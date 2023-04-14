package com.pool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name="TBL_CREDIT_BACKUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditBackupEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long creditId;
    private String personId;
    private String movieId;
    private String name;
    private String character;
    private String role;
}
