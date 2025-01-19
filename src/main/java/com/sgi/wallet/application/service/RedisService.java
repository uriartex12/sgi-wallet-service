package com.sgi.wallet.application.service;

import com.sgi.wallet.domain.model.redis.Transaction;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RedisService {
    Mono<Transaction> saveTransaction (String walletId ,Mono<Transaction> transactionMono);
    List<Transaction> findAllTransactionsByWalletId(String walletId);
}
