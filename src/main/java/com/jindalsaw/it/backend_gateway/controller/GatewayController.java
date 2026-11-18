package com.jindalsaw.it.backend_gateway.controller;



import com.jindalsaw.it.backend_gateway.model.TransactionRequest;
import com.jindalsaw.it.backend_gateway.security.HmacSecurityUtil;
import com.jindalsaw.it.backend_gateway.service.GatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gateway")
@CrossOrigin(origins = "*") // Allows Next.js frontend to communicate seamlessly
public class GatewayController {

    private final HmacSecurityUtil hmacSecurityUtil;
    private final GatewayService gatewayService;

    public GatewayController(HmacSecurityUtil hmacSecurityUtil, GatewayService gatewayService) {
        this.hmacSecurityUtil = hmacSecurityUtil;
        this.gatewayService = gatewayService;
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processTransaction(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @Valid @RequestBody TransactionRequest request) {

        Map<String, Object> response = new HashMap<>();

        // 1. HMAC SHA-256 Security Verification
        if (!hmacSecurityUtil.verifySignature(request.getPayload(), signature)) {
            response.put("status", "REJECTED");
            response.put("error", "Invalid or missing HMAC SHA-256 signature header [X-Signature]");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 2. Rate Limiting Verification
        if (gatewayService.isRateLimited(request.getSourceSystem())) {
            response.put("status", "TOO_MANY_REQUESTS");
            response.put("error", "Rate limit exceeded for system: " + request.getSourceSystem());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }

        // 3. Idempotency Deduplication Check
        if (gatewayService.isDuplicateTransaction(request.getTransactionId())) {
            response.put("status", "DUPLICATE_IGNORED");
            response.put("message", "Transaction " + request.getTransactionId() + " was already processed.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // 4. Successful Processing
        response.put("status", "SUCCESS");
        response.put("transactionId", request.getTransactionId());
        response.put("message", "Transaction routed successfully to Jindal Saw IT internal pipeline.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
