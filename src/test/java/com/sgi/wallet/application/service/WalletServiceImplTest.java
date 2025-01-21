package com.sgi.wallet.application.service;

import com.sgi.wallet.application.service.impl.WalletServiceImpl;
import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.domain.model.Wallet;
import com.sgi.wallet.helper.FactoryTest;
import com.sgi.wallet.infrastructure.dto.*;
import com.sgi.wallet.infrastructure.exception.CustomException;
import com.sgi.wallet.infrastructure.mapper.WalletMapper;
import com.sgi.wallet.infrastructure.repository.WalletRepositoryJpa;
import com.sgi.wallet.infrastructure.subscriber.message.EventSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    @Mock
    private WalletRepositoryJpa walletRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private EventSender kafkaTemplate;


    @InjectMocks
    private WalletServiceImpl walletService;


    @Test
    void createWallet_shouldReturnCreatedResponse() {
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);

        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.createWallet(Mono.just(walletRequest));

        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();

        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void deleteWallet_shouldReturnVoid() {
        String walletId = UUID.randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        wallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.delete(wallet)).thenReturn(Mono.empty());

        Mono<Void> result = walletService.deleteWallet(walletId);
        StepVerifier.create(result)
                .verifyComplete();

        verify(walletRepository).findById(walletId);
        verify(walletRepository).delete(wallet);
    }

    @Test
    void deleteBootcoin_shouldReturnNotFound() {
        String walletId = UUID.randomUUID().toString();

        when(walletRepository.findById(walletId)).thenReturn(Mono.empty());

        Mono<Void> result = walletService.deleteWallet(walletId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CustomException
                        &&
                        throwable.getMessage().equals("Wallet not found"))
                .verify();
        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).delete(any());
    }

    @Test
    void getAllWallet_shouldReturnListWalletResponse() {
        List<Wallet> wallets = FactoryTest.toFactoryListWallet();

        when(walletRepository.findAll()).thenReturn(Flux.fromIterable(wallets));

        Flux<WalletResponse> result = walletService.getAllWallets();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        verify(walletRepository, times(1)).findAll();
    }

    @Test
    void getWalletById_shouldReturnWalletResponse() {
        String walletId =  UUID.randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.getWalletById(walletId);
        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();
        verify(walletRepository, times(1)).findById(walletId);
    }


    @Test
    void associateDebitCard_shouldReturnWalletResponse() {
        String walletId =  UUID.randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);
        AssociateRequest associateRequest = FactoryTest.toFactoryAssociateRequest();
        walletResponse.setDebitCardId(associateRequest.getDebitCardId());

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.associateDebitCard(walletId, Mono.just(associateRequest));
        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void updatedWallet_shouldReturnWalletResponse() {
        String walletId =  UUID.randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.updateWallet(walletId, Mono.just(walletRequest));
        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void currentBalanceAndCardDetailsByCardId_shouldReturnWalletResponse() {
        String cardId =  UUID.randomUUID().toString();
        BigDecimal currentBalance = BigDecimal.valueOf(10);
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        CardDetails cardDetails = FactoryTest.toFactoryCardDetailsResponse(cardId);
        wallet.setCardDetails(cardDetails);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);
        walletResponse.setBalance(currentBalance);

        when(walletRepository.findByDebitCardId(cardId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.currentBalanceAndCardDetailsByCardId(cardId, cardDetails, currentBalance);
        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();

        verify(walletRepository, times(1)).findByDebitCardId(cardId);
    }

    @Test
    void rollbackBalanceDueToServerError_shouldReturnWalletResponse() {
        String walletId =  UUID.randomUUID().toString();
        BigDecimal currentBalance = BigDecimal.valueOf(10);
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);
        walletResponse.setBalance(currentBalance.add(wallet.getBalance()));

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.rollbackBalanceDueToServerError(walletId, currentBalance);
        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();

        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void updatedBalanceDueToOperation_shouldReturnWalletResponse() {
        String accountId =  UUID.randomUUID().toString();
        BigDecimal currentBalance = BigDecimal.valueOf(10);
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        WalletResponse walletResponse = WalletMapper.INSTANCE.map(wallet);
        walletResponse.setBalance(currentBalance.add(wallet.getBalance()));

        when(walletRepository.findByCardDetailsAccountId(accountId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<WalletResponse> result = walletService.updatedBalanceDueToOperation(accountId, currentBalance);
        StepVerifier.create(result)
                .expectNext(walletResponse)
                .verifyComplete();

        verify(walletRepository, times(1)).findByCardDetailsAccountId(accountId);
    }



    @Test
    void yankearWallet_shouldReturnYankearResponse() {
        YankearRequest yankearRequest = FactoryTest.toFactoryYankearRequest();

        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet fromWallet = FactoryTest.toFactoryWallet(walletRequest);
        Wallet toWallet = FactoryTest.toFactoryWallet(walletRequest);

        YankearResponse yankearResponse = new YankearResponse();
        yankearResponse.setName(toWallet.getName());
        yankearResponse.setPhoneNumber(yankearRequest.getToPhoneNumber());
        yankearResponse.setAmount(yankearRequest.getAmount());
        yankearResponse.setDescription(yankearRequest.getDescription());
        yankearResponse.setTimestamp(OffsetDateTime.now());

        when(walletRepository.findByPhone(yankearRequest.getFromPhoneNumber())).thenReturn(Mono.just(fromWallet));
        when(walletRepository.findByPhone(yankearRequest.getToPhoneNumber())).thenReturn(Mono.just(toWallet));
        when(walletRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(fromWallet, toWallet));

        Mono<YankearResponse> result = walletService.yankearWallet(Mono.just(yankearRequest));
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(yankearResponse.getPhoneNumber(), response.getPhoneNumber());
                })
                .verifyComplete();

        verify(walletRepository, times(1)).findByPhone(yankearRequest.getFromPhoneNumber());
        verify(walletRepository, times(1)).findByPhone(yankearRequest.getToPhoneNumber());
    }

    @Test
    void getWalletTransactions_shouldReturnListWalletResponse() {
        String walletId =  UUID.randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);
        wallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        Mono<WalletResponse> result = walletService.getWalletTransactions(walletId);
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void getWalletBalance_shouldReturnBalanceResponse(){
        String phone =  UUID.randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        Wallet wallet = FactoryTest.toFactoryWallet(walletRequest);

        when(walletRepository.findByPhone(phone)).thenReturn(Mono.just(wallet));

        Mono<BalanceResponse> result = walletService.getPrimaryWalletBalance(phone);

        StepVerifier.create(result)
                .expectNext(WalletMapper.INSTANCE.toBalance(wallet))
                .verifyComplete();

        verify(walletRepository, times(1)).findByPhone(phone);
    }

}
