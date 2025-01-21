package com.sgi.wallet.helper;

import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.domain.model.Wallet;
import com.sgi.wallet.domain.model.redis.Transaction;
import com.sgi.wallet.infrastructure.dto.*;
import com.sgi.wallet.infrastructure.subscriber.events.SyncBankAccountBalance;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.sgi.wallet.infrastructure.enums.MovementType.DEBIT;

public class FactoryTest {

    @SneakyThrows
    public static <R> R toFactoryWallet(Class<R> response) {
        R account = response.getDeclaredConstructor().newInstance();
        if (account instanceof WalletRequest walletRequest) {
            return (R) initializeWallet(walletRequest);
        } else if (account instanceof WalletResponse walletResponse) {
            return (R) initializeWallet(walletResponse);
        }
        return account;
    }

    private static WalletRequest initializeWallet(WalletRequest walletRequest) {
        walletRequest.setDocumentNumber("712338232");
        walletRequest.setEmail("test@gmail.com");
        walletRequest.setName("test-bootcoin");
        walletRequest.setPhone("910672362");
        walletRequest.setDocumentType("DNI");
        return walletRequest;
    }

    private static WalletResponse initializeWallet(WalletResponse walletResponse) {
        walletResponse.id(UUID.randomUUID().toString());
        walletResponse.setDocumentNumber("712338232");
        walletResponse.setEmail("test@gmail.com");
        walletResponse.setName("test-bootcoin");
        walletResponse.setPhone("910672362");
        walletResponse.setDocumentType("DNI");
        walletResponse.setCardDetails(new com.sgi.wallet.infrastructure.dto.CardDetails(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()
        ));
        walletResponse.setTransactions(List.of(
                Transaction.builder()
                        .walletId(UUID.randomUUID().toString())
                        .description("Yanki deposit")
                        .source("YANKI")
                        .amount(BigDecimal.TEN)
                        .clientId(UUID.randomUUID().toString())
                        .type(DEBIT.name())
                        .build()
        ));
        return walletResponse;
    }

    public static List<WalletResponse> toFactoryListWalletResponse() {
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.id(UUID.randomUUID().toString());
        walletResponse.setDocumentNumber("712338232");
        walletResponse.setEmail("test@gmail.com");
        walletResponse.setName("test-bootcoin");
        walletResponse.setPhone("910672362");
        walletResponse.setDocumentType("DNI");
        return List.of(walletResponse);
    }

    public static Wallet toFactoryWallet(WalletRequest walletRequest) {
        return Wallet.builder()
                .id(UUID.randomUUID().toString())
                .email(walletRequest.getEmail())
                .phone(walletRequest.getPhone())
                .name(walletRequest.getName())
                .documentNumber(walletRequest.getDocumentNumber())
                .documentType(walletRequest.getDocumentType())
                .balance(BigDecimal.ONE)
                .debitCardId(UUID.randomUUID().toString())
                .cardDetails(new CardDetails(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        "DEBIT",
                        UUID.randomUUID().toString()
                ))
                .build();
    }

    public static List<Wallet> toFactoryListWallet() {
        Wallet bootCoin = Wallet.builder()
                .id(UUID.randomUUID().toString())
                .email("test@gmail.com")
                .phone("910672362")
                .name("test-bootcoin")
                .documentNumber("712338232")
                .documentType("DNI")
                .build();
        return List.of(bootCoin);
    }

    public static BalanceResponse toFactoryBalance(String phone) {
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(BigDecimal.TEN);
        balanceResponse.setPhone(phone);
        balanceResponse.setWalletId(UUID.randomUUID().toString());
        return  balanceResponse;
    }


    public static AssociateRequest toFactoryAssociateRequest() {
        AssociateRequest associateRequest = new AssociateRequest();
        associateRequest.setDebitCardId(UUID.randomUUID().toString());
        return associateRequest;
    }

    public static YankearRequest toFactoryYankearRequest() {
        YankearRequest yankearRequest = new YankearRequest();
        yankearRequest.setAmount(BigDecimal.ONE);
        yankearRequest.setFromPhoneNumber("910672392");
        yankearRequest.setToPhoneNumber("910672839");
        return yankearRequest;
    }

    public static YankearResponse toFactoryYankearResponse() {
        YankearResponse yankearResponse = new YankearResponse();
        yankearResponse.setAmount(BigDecimal.TEN);
        yankearResponse.setName("TEST");
        yankearResponse.setPhoneNumber("910672392");
        yankearResponse.setTimestamp(OffsetDateTime.now());
        return yankearResponse;
    }



    public static CardDetails toFactoryCardDetailsResponse(String cardId) {
        return new CardDetails(
                cardId,
                UUID.randomUUID().toString(),
                "DEBIT",
                UUID.randomUUID().toString()
        );
    }

    public static Transaction toFactoryRedisTransaction() {
        return Transaction.builder()
                .type("DEBIT")
                .clientId(UUID.randomUUID().toString())
                .amount(BigDecimal.TEN)
                .id(UUID.randomUUID().toString())
                .source("yanki")
                .walletId(UUID.randomUUID().toString())
                .receiver(UserDTO.builder().build())
                .sender(UserDTO.builder().build())
                .build();
    }

    public static SyncBankAccountBalance toFactorySyncBankAccountBalance() {
        return new SyncBankAccountBalance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                BigDecimal.ONE
        );
    }

}
