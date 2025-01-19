package com.sgi.wallet.infrastructure.controller;

import com.sgi.wallet.domain.port.in.WalletService;
import com.sgi.wallet.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class WalletController implements V1Api {

    private final WalletService walletService;

    @Override
    public Mono<ResponseEntity<WalletResponse>> associateDebitCard(String walletId, Mono<AssociateRequest> associateRequest, ServerWebExchange exchange) {
        return walletService.associateDebitCard(walletId, associateRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WalletResponse>> createWallet(Mono<WalletRequest> walletRequest, ServerWebExchange exchange) {
        return walletService.createWallet(walletRequest)
                .map(walletResponse
                        -> ResponseEntity.status(HttpStatus.CREATED).body(walletResponse));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteWallet(String walletId, ServerWebExchange exchange) {
        return walletService.deleteWallet(walletId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<WalletResponse>>> getAllWallets(ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(walletService.getAllWallets()));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getPrimaryWalletBalance(String phone, ServerWebExchange exchange) {
        return walletService.getPrimaryWalletBalance(phone)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WalletResponse>> getWalletById(String walletId, ServerWebExchange exchange) {
        return walletService.getWalletById(walletId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WalletResponse>> getWalletTransactions(String walletId, ServerWebExchange exchange) {
        return walletService.getWalletTransactions(walletId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WalletResponse>> updateWallet(String walletId, Mono<WalletRequest> walletRequest, ServerWebExchange exchange) {
        return walletService.updateWallet(walletId, walletRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<YankearResponse>> yankearWallet(Mono<YankearRequest> yankearRequest, ServerWebExchange exchange) {
        return walletService.yankearWallet(yankearRequest)
                .map(ResponseEntity::ok);
    }
}
