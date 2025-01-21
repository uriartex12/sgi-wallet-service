package com.sgi.wallet.application.service.impl;

import com.sgi.wallet.application.service.WalletEventService;
import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.domain.port.in.WalletService;
import com.sgi.wallet.infrastructure.dto.WalletDetailDTO;
import com.sgi.wallet.infrastructure.enums.MovementType;
import com.sgi.wallet.infrastructure.subscriber.events.SyncBankAccountBalance;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEventResponse;
import com.sgi.wallet.infrastructure.subscriber.events.WalletExistEventResponse;
import com.sgi.wallet.infrastructure.subscriber.message.EventSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Set;

import static com.sgi.wallet.domain.shared.Constants.OPERATION_FAILED;
import static com.sgi.wallet.domain.shared.Constants.OPERATION_SUCCESS;
import static com.sgi.wallet.infrastructure.enums.MovementType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletEventServiceImpl implements WalletEventService {

    private final WalletService walletService;
    private final EventSender kafkaTemplate;

    Set<MovementType> creditTypes = Set.of(DEPOSIT, CREDIT, SALE);
    Set<MovementType> debitTypes = Set.of(WITHDRAWAL, PAYMENT, CHARGE, DEBIT, PURCHASE);

    @Override
    @SneakyThrows
    public void syncBankAccountBalance(SyncBankAccountBalance event) {
        CardDetails cardDetails = new CardDetails(event.cardId(), event.accountId(), "DEBIT",event.clientId());
        walletService.currentBalanceAndCardDetailsByCardId(event.cardId(), cardDetails, event.accountBalance())
                .doOnSuccess(wallet -> log.info(OPERATION_SUCCESS, wallet))
                .doOnError(error -> log.error(OPERATION_FAILED, error.getMessage()))
                .subscribe();
    }

    @Override
    @SneakyThrows
    public void validateExistWalletId(String bootcoinId, String walletId) {
        walletService.getWalletById(walletId)
                .map(wallet -> WalletExistEventResponse.builder()
                        .yankiId(walletId)
                        .bootcoinId(bootcoinId)
                        .exist(true)
                        .walletDetail(WalletDetailDTO.builder()
                                .accountId(wallet.getCardDetails().getAccountId())
                                .type(wallet.getCardDetails().getType())
                                .clientId(wallet.getCardDetails().getClientId())
                                .build())
                        .build())
                .switchIfEmpty(Mono.just(WalletExistEventResponse.builder()
                        .yankiId(walletId)
                        .bootcoinId(bootcoinId)
                        .exist(false)
                        .build()))
                .flatMap(eventResponse -> {
                    kafkaTemplate.sendEvent(WalletExistEventResponse.TOPIC, eventResponse);
                    return Mono.empty();
                })
                .subscribe();
    }



    @Override
    @SneakyThrows
    public void invalidateWalletProcess(OrchestratorWalletEventResponse event) {
        walletService.rollbackBalanceDueToServerError(event.getWalletId(),
                        calculateBalance(event.getType(), event.getAmount(),true))
                        .subscribe();
    }

    @Override
    public void updatedCurrentAccountBalance(String accountId, String type, BigDecimal amount) {
        walletService.updatedBalanceDueToOperation(accountId,
                        calculateBalance(type, amount, false))
                .subscribe();
    }

    private BigDecimal calculateBalance(String type, BigDecimal amount, boolean isRollback) {
        if (creditTypes.contains(MovementType.valueOf(type))) {
            return isRollback ? amount.negate() : amount;
        }
        if (debitTypes.contains(MovementType.valueOf(type))) {
            return isRollback ? amount : amount.negate();
        }
        return amount;
    }



}
