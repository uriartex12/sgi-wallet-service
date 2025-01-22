package com.sgi.wallet.domain.model.redis;

import com.sgi.wallet.infrastructure.dto.UserDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "transaction-wallet", timeToLive = 86400L)
public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = 7924654226538059017L;

    @Id
    @Indexed
    private String id;
    private String walletId;
    private String accountId;
    private String clientId;
    private String type;
    private BigDecimal amount;
    private String source;
    private BigDecimal balance;
    private String description;
    private UserDTO sender;
    private UserDTO receiver;
}
