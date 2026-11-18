package com.jindalsaw.it.backend_gateway.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequest {

    @NotBlank(message = "Transaction ID is required for idempotency verification")
    private String transactionId;

    @NotBlank(message = "Source system identifier is required")
    private String sourceSystem; // e.g., "MOTINAGAR_PLANT_ERP", "INVENTORY_SERVICE"

    @NotBlank(message = "Payload data cannot be empty")
    private String payload;

    @NotNull(message = "Timestamp is required")
    private Long timestamp;
}
