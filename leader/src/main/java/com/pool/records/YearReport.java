package com.pool.records;

import java.util.Collection;

public record YearReport(Integer year , Collection<YearPlatformSales> breakout) {
}
