package com.sgi.wallet.infrastructure.repository;

import com.sgi.wallet.domain.model.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WalletRepositoryJpa extends ReactiveMongoRepository<Wallet, String> {

    Mono<Wallet> findByDebitCardId(String cardId);

    Mono<Wallet> findByPhone(String phone);

}
