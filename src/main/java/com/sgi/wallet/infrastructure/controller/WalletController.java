package com.sgi.wallet.infrastructure.controller;

import com.sgi.wallet.infrastructure.dto.BalanceResponse;
import com.sgi.wallet.infrastructure.dto.WalletRequest;
import com.sgi.wallet.infrastructure.dto.WalletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WalletController implements V1Api {

    @Override
    public Mono<ResponseEntity<WalletResponse>> createWallet(Mono<WalletRequest> walletRequest, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteWallet(String walletId, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<Flux<WalletResponse>>> getAllWallets(ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getPrimaryAccountBalance(String phone, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<WalletResponse>> getWalletById(String walletId, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<WalletResponse>> updateWallet(String walletId, Mono<WalletRequest> walletRequest, ServerWebExchange exchange) {
        return null;
    }
}
