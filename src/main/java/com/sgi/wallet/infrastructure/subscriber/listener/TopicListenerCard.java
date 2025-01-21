package com.sgi.wallet.infrastructure.subscriber.listener;

import com.sgi.wallet.application.service.RedisService;
import com.sgi.wallet.application.service.WalletEventService;
import com.sgi.wallet.infrastructure.annotations.KafkaController;
import com.sgi.wallet.infrastructure.mapper.RedisTransactionMapper;
import com.sgi.wallet.infrastructure.subscriber.events.CurrentAccountBalance;
import com.sgi.wallet.infrastructure.subscriber.events.SyncBankAccountBalance;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEventResponse;
import com.sgi.wallet.infrastructure.subscriber.events.WallerValidatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import reactor.core.publisher.Mono;

import static com.sgi.wallet.domain.shared.Constants.KAFKA_MESSAGE_RECEIVING;
import static com.sgi.wallet.infrastructure.enums.PaymentProcess.PAYMENT_CANCELLED;
import static com.sgi.wallet.infrastructure.enums.PaymentProcess.PAYMENT_COMPLETED;


@KafkaController
@Slf4j
@RequiredArgsConstructor
public class TopicListenerCard {

    private final WalletEventService walletEventService;

    private final RedisService redisService;

    @KafkaListener(groupId = "${app.name}", topics = SyncBankAccountBalance.TOPIC)
    private void balanceHandle(SyncBankAccountBalance balanceEventResponse) {
        log.info(KAFKA_MESSAGE_RECEIVING, balanceEventResponse);
        walletEventService.syncBankAccountBalance(balanceEventResponse);
    }

    @KafkaListener(groupId = "${app.name}", topics = OrchestratorWalletEventResponse.TOPIC)
    private void handleOrchestratorHandle(OrchestratorWalletEventResponse event) {
        log.info(" state {}, result {}", event.getStatus(), event);
        if (event.getStatus().equals(PAYMENT_CANCELLED.name())){
            walletEventService.invalidateWalletProcess(event);

        }else if (event.getStatus().equals(PAYMENT_COMPLETED.name())){
            redisService.saveTransaction(event.getWalletId(),
                            Mono.just(RedisTransactionMapper.INSTANCE.map(event)))
                            .subscribe();
        }
    }

    @KafkaListener(groupId = "${app.name}", topics = WallerValidatedEvent.TOPIC)
    public void handleValidateAccount(WallerValidatedEvent event) {
        walletEventService.validateExistWalletId(event.getBootcoinId(), event.getYankiId());
    }

    @KafkaListener(groupId = "${app.name}", topics = CurrentAccountBalance.TOPIC)
    public void handleUpdatedBalance(CurrentAccountBalance event) {
        walletEventService.updatedCurrentAccountBalance(event.accountId(), event.type(), event.amount());
    }

}
