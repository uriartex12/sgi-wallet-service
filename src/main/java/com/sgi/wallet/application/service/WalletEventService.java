package com.sgi.wallet.application.service;

import com.sgi.wallet.infrastructure.subscriber.events.BalanceEventResponse;

public interface WalletEventService {
    void updatedBalanceEvent(BalanceEventResponse balanceEventResponse);
}
