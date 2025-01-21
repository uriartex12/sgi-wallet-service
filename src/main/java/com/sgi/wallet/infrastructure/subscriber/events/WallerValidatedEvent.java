package com.sgi.wallet.infrastructure.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WallerValidatedEvent {
    private String yankiId;
    private String bootcoinId;

    public static final String TOPIC = "validation-exists-yanki";
}
