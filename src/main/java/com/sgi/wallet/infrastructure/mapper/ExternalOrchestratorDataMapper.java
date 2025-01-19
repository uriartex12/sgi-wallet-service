package com.sgi.wallet.infrastructure.mapper;

import com.sgi.wallet.domain.model.Wallet;
import com.sgi.wallet.infrastructure.dto.UserDTO;
import com.sgi.wallet.infrastructure.dto.YankearRequest;
import com.sgi.wallet.infrastructure.enums.MovementType;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExternalOrchestratorDataMapper {

    ExternalOrchestratorDataMapper INSTANCE = Mappers.getMapper(ExternalOrchestratorDataMapper.class);

    default OrchestratorWalletEvent map(Wallet froYankiWallet, String source,
                                        YankearRequest yankearRequest, MovementType movement,
                                        UserDTO userSend, UserDTO userReceiver){
        return OrchestratorWalletEvent.builder()
                .walletId(froYankiWallet.getId())
                .source(source)
                .type(movement.name())
                .description(yankearRequest.getDescription())
                .accountId(froYankiWallet.getCardDetails().accountId())
                .amount(yankearRequest.getAmount())
                .balance(froYankiWallet.getBalance())
                .clientId(froYankiWallet.getCardDetails().clientId())
                .sender(userSend)
                .receiver(userReceiver)
                .build();
    };
}