package com.sgi.wallet.infrastructure.subscriber.events;

import java.math.BigDecimal;


public record CurrentAccountBalance(String accountId, String type, BigDecimal amount) {
    public static final String TOPIC = "CurrentAccountBalanceEventResponse";
}