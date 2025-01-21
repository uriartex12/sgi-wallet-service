package com.sgi.wallet.infrastructure.subscriber.events;

import com.sgi.wallet.infrastructure.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorWalletEvent implements EventHandle {
    private String walletId;
    private String accountId;
    private String clientId;
    private String type;
    private BigDecimal amount;
    private String paymentMethod;
    private BigDecimal balance;
    private String description;
    private UserDTO sender;
    private UserDTO receiver;

    public static final String TOPIC = "OrchestratorWalletEvent";

}
