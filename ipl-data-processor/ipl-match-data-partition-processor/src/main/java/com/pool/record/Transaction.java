package com.pool.record;

import java.math.BigDecimal;
import java.util.Date;
public record Transaction(String account,Date timestamp,BigDecimal amount) {
}

