package com.jindalsaw.it.backend_gateway.service;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GatewayService {

    // In-memory thread-safe store for transaction deduplication (Idempotency)
    private final Set<String> processedTransactions = ConcurrentHashMap.newKeySet();

    // Simple Token Bucket Rate Limiter tracking requests per source system
    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_WINDOW = 10;

    public synchronized boolean isRateLimited(String sourceSystem) {
        int currentCount = requestCounts.getOrDefault(sourceSystem, 0);
        if (currentCount >= MAX_REQUESTS_PER_WINDOW) {
            return true; // Rate limit exceeded
        }
        requestCounts.put(sourceSystem, currentCount + 1);
        return false;
    }

    public boolean isDuplicateTransaction(String transactionId) {
        if (processedTransactions.contains(transactionId)) {
            return true; // Duplicate request detected
        }
        processedTransactions.add(transactionId);
        return false;
    }

    public void resetRateLimits() {
        requestCounts.clear();
    }
}

