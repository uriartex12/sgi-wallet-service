package com.sgi.wallet.infrastructure.controller;

import com.sgi.wallet.application.service.impl.WalletServiceImpl;
import com.sgi.wallet.helper.FactoryTest;
import com.sgi.wallet.infrastructure.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@WebFluxTest(controllers = WalletController.class)
public class WalletControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WalletServiceImpl walletService;


    @Test
    void createWallet_shouldReturnCreatedResponse() {
        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);
        Mockito.when(walletService.createWallet(any(Mono.class)))
                .thenReturn(Mono.just(walletResponse));

        webTestClient.post()
                .uri("/v1/wallets")
                .bodyValue(FactoryTest.toFactoryWallet(WalletRequest.class))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(WalletResponse.class)
                .consumeWith(accountResponseEntityExchangeResult -> {
                    WalletResponse actual = accountResponseEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(Objects.requireNonNull(actual).getId());
                    Assertions.assertNotNull(actual.getName());
                })
                .returnResult();
        Mockito.verify(walletService, times(1)).createWallet(any(Mono.class));
    }


    @Test
    void deleteWallet_shouldReturnOkResponse() {
        String walletId = randomUUID().toString();
        Mockito.when(walletService.deleteWallet(walletId)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/v1/wallets/{walletId}", walletId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getWalletById_shouldReturnWalletResponse() {
        String walletId = randomUUID().toString();
        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);
        walletResponse.setId(walletId);
        Mockito.when(walletService.getWalletById(walletId))
                .thenReturn(Mono.just(walletResponse));

        webTestClient.get()
                .uri("/v1/wallets/{walletId}", walletId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(WalletResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(walletResponse.getId(), actual.getId());
                });
    }

    @Test
    void getAllWallet_shouldReturnFluxOfWalletResponse() {
        List<WalletResponse> walletResponse = FactoryTest.toFactoryListWalletResponse();
        Flux<WalletResponse> walletResponseFlux = Flux.fromIterable(walletResponse);

        Mockito.when(walletService.getAllWallets()).thenReturn(walletResponseFlux);

        webTestClient.get()
                .uri("/v1/wallets")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WalletResponse.class)
                .value(list -> assertThat(list).hasSize(1));
    }

    @Test
    void updateWallet_shouldReturnWalletResponse() {
        String walletId = randomUUID().toString();
        WalletRequest walletRequest = FactoryTest.toFactoryWallet(WalletRequest.class);
        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);
        walletResponse.id(walletId);

        Mockito.when(walletService.updateWallet(eq(walletId), any(Mono.class)))
                .thenReturn(Mono.just(walletResponse));

        webTestClient.put()
                .uri("/v1/wallets/{walletId}", walletId)
                .bodyValue(walletRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(WalletResponse.class);
        Mockito.verify(walletService, times(1)).updateWallet(eq(walletId), any(Mono.class));
    }

    @Test
    void getWalletTransactions_shouldReturnWalletResponse() {
        String walletId = randomUUID().toString();
        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);
        walletResponse.setId(walletId);
        Mockito.when(walletService.getWalletTransactions(walletId))
                .thenReturn(Mono.just(walletResponse));

        webTestClient.get()
                .uri("/v1/wallets/{walletId}/transactions", walletId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(WalletResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(walletResponse.getId(), actual.getId());
                });
    }

    @Test
    void getWalletBalance_shouldReturnBalanceResponse() {
        String phone = "910677465";
        BalanceResponse balanceResponse = FactoryTest.toFactoryBalance(phone);
        Mockito.when(walletService.getPrimaryWalletBalance(phone))
                .thenReturn(Mono.just(balanceResponse));
        webTestClient.get()
                .uri("/v1/wallets/{phone}/balance", phone)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BalanceResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(balanceResponse.getPhone(), actual.getPhone());
                });
    }


    @Test
    void associateDebitCard_shouldReturnWalletResponse() {
        String walletId = UUID.randomUUID().toString();
        AssociateRequest associateRequest = FactoryTest.toFactoryAssociateRequest();
        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);
        walletResponse.setDebitCardId(associateRequest.getDebitCardId());
        Mockito.when(walletService.associateDebitCard(anyString(), any(Mono.class)))
                .thenReturn(Mono.just(walletResponse));

        webTestClient.post()
                .uri("/v1/wallets/{walletId}/associate", walletId)
                .bodyValue(associateRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(WalletResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(walletResponse.getDebitCardId(), actual.getDebitCardId());
                });
    }

    @Test
    void yankear_shouldReturnYankearResponse() {
        YankearRequest yankearRequest = FactoryTest.toFactoryYankearRequest();
        YankearResponse yankearResponse = FactoryTest.toFactoryYankearResponse();

        Mockito.when(walletService.yankearWallet(any(Mono.class)))
                .thenReturn(Mono.just(yankearResponse));

        webTestClient.post()
                .uri("/v1/wallets/yankear")
                .bodyValue(yankearRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(YankearResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(yankearResponse.getPhoneNumber(), actual.getPhoneNumber());
                });
    }



}
