package com.sgi.wallet.application.service.impl;

import com.sgi.wallet.application.service.WalletEventService;
import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.domain.port.in.WalletService;
import com.sgi.wallet.infrastructure.subscriber.events.BalanceEventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.sgi.wallet.domain.shared.Constants.OPERATION_FAILED;
import static com.sgi.wallet.domain.shared.Constants.OPERATION_SUCCESS;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletEventServiceImpl implements WalletEventService {

    private final WalletService walletService;

    @Override
    public void updatedBalanceEvent(BalanceEventResponse event) {
        CardDetails cardDetails = new CardDetails(event.cardId(), event.accountId(), "DEBIT",event.clientId());
        walletService.updateBalanceAndCardDetailsByCardId(event.cardId(), cardDetails, event.accountBalance())
                .doOnSuccess(wallet -> log.info(OPERATION_SUCCESS, wallet))
                .doOnError(error -> log.error(OPERATION_FAILED, error.getMessage()))
                .subscribe();
    }

}
