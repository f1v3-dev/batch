package com.f1v3.batch.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BulkInsertResult {
    private final String method;
    private final int totalCount;
    private final int batchSize;
    private final long totalTimeMs;
    private final double totalTimeSec;
    private final long insertedRecords;
    private final long throughputPerSec;

    @Builder.Default
    private final String status = "completed";
}
