package com.sgi.wallet.domain.shared;

import com.sgi.wallet.infrastructure.exception.ApiError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum representing custom errors for the Card-service application.
 * Each constant includes an error code, message, and HTTP status for specific errors.
 */
@Getter
@AllArgsConstructor
public enum CustomError {

    E_OPERATION_FAILED(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "WALLET-000", "Operation failed")),
    E_INSUFFICIENT_BALANCE(new ApiError(HttpStatus.PAYMENT_REQUIRED, "WALLET-002", "Insufficient balance")),
    E_WALLET_NOT_FOUND(new ApiError(HttpStatus.NOT_FOUND, "WALLET-001", "Wallet not found"));
    private final ApiError error;
}
