package com.sgi.wallet.domain.port.in;

import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.infrastructure.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface WalletService {
    Mono<WalletResponse> createWallet(Mono<WalletRequest> walletRequestMono);
    Mono<Void> deleteWallet(String walletId);
    Flux<WalletResponse> getAllWallets();
    Mono<WalletResponse> getWalletById(String walletId);
    Mono<WalletResponse> associateDebitCard(String walletId, Mono<AssociateRequest> associateRequest);
    Mono<WalletResponse> updateWallet(String walletId, Mono<WalletRequest> walletRequestMono);
    Mono<WalletResponse> updateBalanceAndCardDetailsByCardId(String cardId, CardDetails cardDetails, BigDecimal balance);
    Mono<YankearResponse> yankearWallet(Mono<YankearRequest> yankearRequestMono);
    Mono<WalletResponse> getWalletTransactions(String walletId);
    Mono<BalanceResponse> getPrimaryWalletBalance(String phone);
}
