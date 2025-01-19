package com.sgi.wallet.infrastructure.mapper;

import com.sgi.wallet.domain.model.Wallet;
import com.sgi.wallet.infrastructure.dto.BalanceResponse;
import com.sgi.wallet.infrastructure.dto.WalletRequest;
import com.sgi.wallet.infrastructure.dto.WalletResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import reactor.core.publisher.Mono;

@Mapper
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(target = "id", ignore = true)
    Wallet map(WalletRequest walletRequest);

    WalletResponse map(Wallet wallet);

    BalanceResponse toBalance(Wallet wallet);

}
