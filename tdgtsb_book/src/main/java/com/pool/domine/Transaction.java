package com.pool.domine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction implements Serializable {
    private Integer transactionId;
    private Integer accountId;
    private Float credit;
    private Float debit;
    private Timestamp timestamp;
}
