package com.pool.domine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "TBL_ERROR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorId;
    private String jobName;
    private String actionType;
    @Column(columnDefinition = "text")
    private String input;
    @Column(columnDefinition = "text")
    private String message;
    private Integer lineNumber;
}

