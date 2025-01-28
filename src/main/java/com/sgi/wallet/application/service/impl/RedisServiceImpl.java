package com.sgi.wallet.application.service.impl;

import com.sgi.wallet.application.service.RedisService;
import com.sgi.wallet.domain.model.redis.Transaction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private static final String TRANSACTION_HASH = "Transaction-redis";

    private final RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Mono<Transaction> saveTransaction(String walletId ,Mono<Transaction> transactionMono) {
        return transactionMono.flatMap(this::saveTransactionToRedis)
                .onErrorResume(error -> {
                    log.error("Error saving transaction:", error);
                    return Mono.error(error);
                });
    }

    private Mono<Transaction> saveTransactionToRedis(Transaction transaction) {
        return Mono.defer(() ->
                Mono.fromCallable(() -> {
                    List<Transaction> existingTransactions = Optional.ofNullable(
                            (List<Transaction>) hashOperations.get(TRANSACTION_HASH, transaction.getWalletId()))
                            .orElseGet(ArrayList::new);
                    existingTransactions.add(transaction);
                    hashOperations.put(TRANSACTION_HASH, transaction.getWalletId(), existingTransactions);
                    return transaction;
                })
        );
    }

    @Override
    public List<Transaction> findAllTransactionsByWalletId(String walletId) {
        try {
            return Optional.ofNullable((List<Transaction>) hashOperations.get(TRANSACTION_HASH, walletId))
                    .orElseGet(Collections::emptyList);
        } catch (SerializationException e) {
            log.error("Error deserializing transactions from Redis for walletId: {}", walletId, e);
            return Collections.emptyList();
        }
    }

}
