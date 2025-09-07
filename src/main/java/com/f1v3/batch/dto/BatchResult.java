package com.f1v3.batch.dto;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BatchResult {

    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private final List<BatchError> errors;

    public BatchResult() {
        this.totalProcessed = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.errors = new ArrayList<>();
    }

    public void addSuccess(int count) {
        this.successCount += count;
        this.totalProcessed += count;
    }

    public void addFailure(int startIndex, int endIndex, String errorMessage) {
        this.failureCount += (endIndex - startIndex);
        this.totalProcessed += (endIndex - startIndex);
        this.errors.add(new BatchError(startIndex, endIndex, errorMessage));
    }

    @Getter
    public static class BatchError {
        private final int startIndex;
        private final int endIndex;
        private final String errorMessage;

        public BatchError(int startIndex, int endIndex, String errorMessage) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.errorMessage = errorMessage;
        }
    }
}
