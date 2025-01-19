package com.sgi.wallet.infrastructure.subscriber.listener;

import com.sgi.wallet.application.service.RedisService;
import com.sgi.wallet.application.service.impl.WalletEventServiceImpl;
import com.sgi.wallet.infrastructure.annotations.KafkaController;
import com.sgi.wallet.infrastructure.mapper.RedisTransactionMapper;
import com.sgi.wallet.infrastructure.subscriber.events.BalanceEventResponse;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import reactor.core.publisher.Mono;

import static com.sgi.wallet.domain.shared.Constants.KAFKA_MESSAGE_RECEIVING;


@KafkaController
@Slf4j
@RequiredArgsConstructor
public class TopicListenerCard {

    private final WalletEventServiceImpl walletEventService;

    private final RedisService redisService;

    @KafkaListener(
            groupId = "${app.name}",
            topics = BalanceEventResponse.TOPIC
    )
    private void balanceHandle(BalanceEventResponse balanceEventResponse) {
        log.info(KAFKA_MESSAGE_RECEIVING, balanceEventResponse);
        walletEventService.updatedBalanceEvent(balanceEventResponse);
    }

    @KafkaListener(
            groupId = "${app.name}",
            topics = OrchestratorWalletEventResponse.TOPIC
    )
    private void orchestratorHandle(OrchestratorWalletEventResponse orchestratorWalletEventResponse) {
        redisService.saveTransaction(orchestratorWalletEventResponse.getWalletId(), Mono.just(RedisTransactionMapper.
                INSTANCE.map(orchestratorWalletEventResponse)))
                .subscribe();
    }
}
