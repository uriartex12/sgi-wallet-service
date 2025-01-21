package com.sgi.wallet.application.service;

import com.sgi.wallet.infrastructure.subscriber.events.SyncBankAccountBalance;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEventResponse;

import java.math.BigDecimal;

public interface WalletEventService {
    void syncBankAccountBalance(SyncBankAccountBalance balanceEventResponse);
    void validateExistWalletId(String bootcoinId, String walletId);
    void invalidateWalletProcess(OrchestratorWalletEventResponse event);
    void updatedCurrentAccountBalance(String walletId, String type, BigDecimal amount);

}
