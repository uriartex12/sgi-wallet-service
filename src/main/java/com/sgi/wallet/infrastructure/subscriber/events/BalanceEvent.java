package com.sgi.wallet.infrastructure.subscriber.events;

public record BalanceEvent(String cardId) implements EventHandle{
    public static final String TOPIC = "BalanceEvent";
}
