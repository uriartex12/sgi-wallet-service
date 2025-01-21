package com.sgi.wallet.infrastructure.subscriber.events;

import com.sgi.wallet.infrastructure.dto.WalletDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WalletExistEventResponse {
    private String bootcoinId;
    private String yankiId;
    private Boolean exist;
    private WalletDetailDTO walletDetail;

    public static final String TOPIC = "validation-wallet-response";
}
