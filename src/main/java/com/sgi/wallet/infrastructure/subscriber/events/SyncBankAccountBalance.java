package com.sgi.wallet.infrastructure.subscriber.events;

import java.math.BigDecimal;

public record SyncBankAccountBalance(String cardId, String accountId, String clientId, BigDecimal accountBalance) {
    public static final String TOPIC = "BalanceEventResponse";
}