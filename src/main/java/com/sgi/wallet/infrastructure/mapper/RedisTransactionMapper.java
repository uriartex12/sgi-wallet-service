package com.sgi.wallet.infrastructure.mapper;

import com.sgi.wallet.domain.model.redis.Transaction;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RedisTransactionMapper {

    RedisTransactionMapper INSTANCE = Mappers.getMapper(RedisTransactionMapper.class);

    @Mapping(target = "id", source = "orchestratorWallet.transactionId")
    Transaction map(OrchestratorWalletEventResponse orchestratorWallet);
}
