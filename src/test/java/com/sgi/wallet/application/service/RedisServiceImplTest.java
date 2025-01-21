package com.sgi.wallet.application.service;

import com.sgi.wallet.application.service.impl.RedisServiceImpl;
import com.sgi.wallet.domain.model.redis.Transaction;
import com.sgi.wallet.helper.FactoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.assertions.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisServiceImplTest {


    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, String, Object> hashOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    private static final String REDIS_HASH = "Transaction-redis";

    @BeforeEach
    void setUp() {
        doReturn(hashOperations).when(redisTemplate).opsForHash();
        redisService.init();
    }

    @Test
    void saveTransactions_shouldSaveTransaction() {
        String walletId = UUID.randomUUID().toString();
        Transaction transaction = FactoryTest.toFactoryRedisTransaction();
        transaction.setWalletId(walletId);

        List<Transaction> existingTransactions = new ArrayList<>();
        when(hashOperations.get(REDIS_HASH, walletId)).thenReturn(existingTransactions);

        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String hashKey = invocation.getArgument(1);
            List<Transaction> value = invocation.getArgument(2);
            assertEquals(REDIS_HASH, key);
            assertEquals(walletId, hashKey);
            assertTrue(value.contains(transaction));
            return null;
        }).when(hashOperations).put(eq(REDIS_HASH), eq(walletId), any());

        Mono<Transaction> result = redisService.saveTransaction(walletId, Mono.just(transaction));

        StepVerifier.create(result)
                .expectNext(transaction)
                .verifyComplete();

        verify(hashOperations, times(1)).put(eq(REDIS_HASH), eq(walletId), any());
    }


    @Test
    void saveTransactions_shouldHandleErrorGracefully() {
        String walletId = UUID.randomUUID().toString();
        Transaction transaction = FactoryTest.toFactoryRedisTransaction();
        transaction.setWalletId(walletId);

        Mono<Transaction> transactionMono = Mono.just(transaction);

        doThrow(new RuntimeException("Redis error")).when(hashOperations).put(eq(REDIS_HASH), eq(walletId), any(List.class));

        Mono<Transaction> result = redisService.saveTransaction(walletId, transactionMono);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(hashOperations, times(1)).put(eq(REDIS_HASH), eq(walletId), any(List.class));
    }


    @Test
    void findAllTransactionsByWalletId_shouldReturnBootCoinOrder() {
        String walletId = UUID.randomUUID().toString();
        Transaction transaction = FactoryTest.toFactoryRedisTransaction();
        transaction.setWalletId(walletId);

        List<Transaction> mockTransactions = List.of(transaction);

        when(hashOperations.get(REDIS_HASH, walletId)).thenReturn(mockTransactions);

        List<Transaction> result = redisService.findAllTransactionsByWalletId(walletId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));

        verify(hashOperations, times(1)).get(REDIS_HASH, walletId);
    }


    @Test
    void findAllTransactionsByWalletId_shouldReturnEmptyListIfNotFound() {
        String walletId = UUID.randomUUID().toString();

        when(hashOperations.get(REDIS_HASH, walletId)).thenReturn(null);

        List<Transaction> result = redisService.findAllTransactionsByWalletId(walletId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hashOperations, times(1)).get(REDIS_HASH, walletId);
    }

}