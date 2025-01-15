package com.sgi.wallet.infrastructure.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorEvent {
    private String cardId;
    private String accountId;
    private String clientId;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;

}
