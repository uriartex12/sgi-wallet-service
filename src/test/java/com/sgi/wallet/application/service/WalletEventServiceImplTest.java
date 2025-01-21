package com.sgi.wallet.application.service;

import com.sgi.wallet.application.service.impl.WalletEventServiceImpl;
import com.sgi.wallet.domain.model.CardDetails;
import com.sgi.wallet.domain.model.Wallet;
import com.sgi.wallet.domain.port.in.WalletService;
import com.sgi.wallet.helper.FactoryTest;
import com.sgi.wallet.infrastructure.dto.BalanceResponse;
import com.sgi.wallet.infrastructure.dto.WalletDetailDTO;
import com.sgi.wallet.infrastructure.dto.WalletRequest;
import com.sgi.wallet.infrastructure.dto.WalletResponse;
import com.sgi.wallet.infrastructure.enums.MovementType;
import com.sgi.wallet.infrastructure.mapper.WalletMapper;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorWalletEventResponse;
import com.sgi.wallet.infrastructure.subscriber.events.SyncBankAccountBalance;
import com.sgi.wallet.infrastructure.subscriber.events.WalletExistEventResponse;
import com.sgi.wallet.infrastructure.subscriber.message.EventSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletEventServiceImplTest {

    @Mock
    private WalletService walletService;

    @Mock
    private  EventSender kafkaTemplate;

    @InjectMocks
    WalletEventServiceImpl walletEventService;


    @Test
    void syncBankAccountBalance_shouldReturnBalanceResponse(){
        String phone =  UUID.randomUUID().toString();
        SyncBankAccountBalance syncBankAccountBalance = FactoryTest.toFactorySyncBankAccountBalance();
        CardDetails cardDetails = new CardDetails(syncBankAccountBalance.cardId(), syncBankAccountBalance.accountId(),
                "DEBIT", syncBankAccountBalance.clientId());

        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);

        when(walletService.currentBalanceAndCardDetailsByCardId(syncBankAccountBalance.cardId(), cardDetails, syncBankAccountBalance.accountBalance()))
                .thenReturn(Mono.just(walletResponse));

       walletEventService.syncBankAccountBalance(syncBankAccountBalance);

        verify(walletService, times(1))
                .currentBalanceAndCardDetailsByCardId(syncBankAccountBalance.cardId(), cardDetails, syncBankAccountBalance.accountBalance());

        verifyNoMoreInteractions(walletService);
    }

    @Test
    void validateExistWalletId_shouldPublishEventWhenWalletExists() {
        String walletId = UUID.randomUUID().toString();
        String bootcoinId = UUID.randomUUID().toString();
        WalletResponse walletResponse = FactoryTest.toFactoryWallet(WalletResponse.class);


        when(walletService.getWalletById(walletId)).thenReturn(Mono.just(walletResponse));
        doNothing().when(kafkaTemplate).sendEvent(anyString(), any(WalletExistEventResponse.class));

        walletEventService.validateExistWalletId(bootcoinId, walletId);

        verify(walletService, times(1)).getWalletById(walletId);
    }

    @Test
    void invalidateWalletProcess_shouldRollbackBalance() {
        String walletId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(100);
        String type = MovementType.CREDIT.name();

        OrchestratorWalletEventResponse event = OrchestratorWalletEventResponse.builder()
                .balance(BigDecimal.TEN)
                .amount(amount)
                .walletId(walletId)
                .type(type)
                .build();

        BigDecimal expectedBalance = amount.negate();

        when(walletService.rollbackBalanceDueToServerError(walletId, expectedBalance))
                .thenReturn(Mono.empty());

        walletEventService.invalidateWalletProcess(event);

        verify(walletService, times(1))
                .rollbackBalanceDueToServerError(walletId, expectedBalance);
    }

    @Test
    void updatedCurrentAccountBalance_shouldUpdateBalance() {
        String accountId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(100);
        String type = MovementType.CREDIT.name();

        when(walletService.updatedBalanceDueToOperation(accountId, amount))
                .thenReturn(Mono.empty());

        walletEventService.updatedCurrentAccountBalance(accountId, type, amount);

        verify(walletService, times(1))
                .updatedBalanceDueToOperation(accountId, amount);
    }

}
