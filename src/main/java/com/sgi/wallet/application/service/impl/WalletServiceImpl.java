package com.sgi.wallet.application.service.impl;

import com.sgi.wallet.application.service.RedisService;
import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.domain.model.Wallet;
import com.sgi.wallet.domain.model.redis.Transaction;
import com.sgi.wallet.domain.port.in.WalletService;
import com.sgi.wallet.domain.shared.CustomError;
import com.sgi.wallet.infrastructure.dto.*;
import com.sgi.wallet.infrastructure.enums.MovementType;
import com.sgi.wallet.infrastructure.exception.CustomException;
import com.sgi.wallet.infrastructure.mapper.ExternalOrchestratorDataMapper;
import com.sgi.wallet.infrastructure.mapper.WalletMapper;
import com.sgi.wallet.infrastructure.repository.WalletRepositoryJpa;
import com.sgi.wallet.infrastructure.subscriber.events.BalanceEvent;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEvent;
import com.sgi.wallet.infrastructure.subscriber.message.EventSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.sgi.wallet.domain.shared.Constants.YANKEAR;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepositoryJpa walletRepository;
    private final RedisService redisService;
    private final EventSender kafkaTemplate;

    @Override
    public Mono<WalletResponse> createWallet(Mono<WalletRequest> walletRequestMono) {
        return walletRequestMono
                .map(WalletMapper.INSTANCE::map)
                .flatMap(walletRepository::save)
                .map(WalletMapper.INSTANCE::map);
    }

    @Override
    public Mono<Void> deleteWallet(String walletId) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .flatMap(walletRepository::delete);
    }

    @Override
    public Flux<WalletResponse> getAllWallets() {
        return walletRepository.findAll()
               .map(WalletMapper.INSTANCE::map);
    }

    @Override
    public Mono<WalletResponse> getWalletById(String walletId) {
        return walletRepository.findById(walletId)
                .map(WalletMapper.INSTANCE::map);
    }

    @Override
    public Mono<WalletResponse> associateDebitCard(String walletId, Mono<AssociateRequest> associateRequestMono) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .zipWith(associateRequestMono, (wallet, request) -> {
                    wallet.setDebitCardId(request.getDebitCardId());
                    kafkaTemplate.sendEvent(BalanceEvent.TOPIC, new BalanceEvent(request.getDebitCardId()));
                    return wallet;
                })
                .flatMap(walletRepository::save)
                .map(WalletMapper.INSTANCE::map);
    }

    @Override
    public Mono<WalletResponse> updateWallet(String walletId, Mono<WalletRequest> walletRequestMono) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .zipWith(walletRequestMono, (wallet, walletRequest) -> {
                    Wallet updated = WalletMapper.INSTANCE.map(walletRequest);
                    updated.setId(wallet.getId());
                    return updated;
                })
                .flatMap(walletRepository::save)
                .map(WalletMapper.INSTANCE::map);
    }

    @Override
    public Mono<WalletResponse> currentBalanceAndCardDetailsByCardId(String cardId, CardDetails cardDetails, BigDecimal balance) {
        return walletRepository.findByDebitCardId(cardId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .flatMap(wallet -> {
                        wallet.setBalance(balance);
                    wallet.setCardDetails(cardDetails);
                    return walletRepository.save(wallet)
                            .map(WalletMapper.INSTANCE::map);
                });
    }
    @Override
    public Mono<WalletResponse> rollbackBalanceDueToServerError(String walletId, BigDecimal balance) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .flatMap(wallet -> {
                    wallet.setBalance(wallet.getBalance().add(balance));
                    return walletRepository.save(wallet)
                            .map(WalletMapper.INSTANCE::map);
                });
    }

    @Override
    public Mono<WalletResponse> updatedBalanceDueToOperation(String accountId, BigDecimal balance) {
        return walletRepository.findByCardDetailsAccountId(accountId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .flatMap(wallet -> {
                    wallet.setBalance(wallet.getBalance().add(balance));
                    return walletRepository.save(wallet)
                            .map(WalletMapper.INSTANCE::map);
                });
    }

    @Override
    public Mono<YankearResponse> yankearWallet(Mono<YankearRequest> yankearRequestMono) {
        return yankearRequestMono
                .flatMap(yankearRequest ->
                        walletRepository.findByPhone(yankearRequest.getFromPhoneNumber())
                                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                                .doOnNext(fromWallet -> validateWalletBalance(fromWallet, yankearRequest.getAmount()))
                                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_INSUFFICIENT_BALANCE)))
                                .zipWith(walletRepository.findByPhone(yankearRequest.getToPhoneNumber())
                                                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND))),
                                        (fromWallet, toWallet) -> {
                                            fromWallet.setBalance(fromWallet.getBalance().subtract(yankearRequest.getAmount()));
                                            toWallet.setBalance(toWallet.getBalance().add(yankearRequest.getAmount()));
                                            List<UserDTO> yankearUsers = createWalletUser(fromWallet, toWallet);
                                            OrchestratorWalletEvent fromWalletEvent = ExternalOrchestratorDataMapper.INSTANCE
                                                    .map(fromWallet, YANKEAR, yankearRequest, MovementType.DEBIT, yankearUsers.get(0), yankearUsers.get(1));
                                            OrchestratorWalletEvent toWalletEvent = ExternalOrchestratorDataMapper.INSTANCE
                                                    .map(toWallet,  YANKEAR, yankearRequest, MovementType.CREDIT, yankearUsers.get(1), yankearUsers.get(0));

                                            Stream.of(fromWalletEvent, toWalletEvent)
                                                    .forEach(orchestratorWalletEvent ->
                                                            kafkaTemplate.sendEvent(OrchestratorWalletEvent.TOPIC,
                                                                    orchestratorWalletEvent));
                                            return Flux.fromIterable(List.of(fromWallet, toWallet));
                                        }
                                )
                                .flatMap(walletList ->
                                        walletRepository.saveAll(walletList)
                                                .then(Mono.just(new YankearResponse(
                                                        Objects.requireNonNull(walletList.blockLast()).getName(),
                                                        yankearRequest.getToPhoneNumber(), yankearRequest.getAmount(),
                                                        yankearRequest.getDescription(),
                                                        OffsetDateTime.now()))
                                                )
                                )
                );
    }

    @Override
    public Mono<WalletResponse> getWalletTransactions(String walletId) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .flatMap(wallet -> {
                    WalletResponse walletDto = WalletMapper.INSTANCE.map(wallet);
                    List<Transaction> transactions = redisService.findAllTransactionsByWalletId(wallet.getId());
                    walletDto.setTransactions(transactions);
                    return Mono.just(walletDto);
                });
    }

    @Override
    public Mono<BalanceResponse> getPrimaryWalletBalance(String phone) {
        return walletRepository.findByPhone(phone)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_WALLET_NOT_FOUND)))
                .flatMap(wallet -> Mono.just(WalletMapper.INSTANCE.toBalance(wallet)));
    }

    private List<UserDTO> createWalletUser(Wallet fromWallet, Wallet toWallet) {
        return List.of(
                UserDTO.builder()
                        .name(fromWallet.getName())
                        .phone(fromWallet.getPhone())
                        .build(),
                UserDTO.builder()
                        .name(toWallet.getName())
                        .phone(toWallet.getPhone())
                        .build()
        );
    }

    private void validateWalletBalance(Wallet fromWallet, BigDecimal amount) {
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new CustomException(CustomError.E_INSUFFICIENT_BALANCE);
        }
    }
}
