package io.github.jairandersonsouza.authorizer.services;

import io.github.jairandersonsouza.authorizer.entities.AccountBalance;
import io.github.jairandersonsouza.authorizer.entities.MccEnum;
import io.github.jairandersonsouza.authorizer.exceptions.TransactionRejectedException;
import io.github.jairandersonsouza.authorizer.controllers.requests.TransactionInput;
import io.github.jairandersonsouza.authorizer.util.AccountBalanceUtil;
import io.github.jairandersonsouza.authorizer.util.TransactionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BalanceAuthorizationServiceTest {

    @InjectMocks
    private BalanceAuthorizationService balanceAuthorizationService;

    @Mock
    private AccountBalanceService accountBalanceService;

    private TransactionInput transaction;

    private String id;

    @BeforeEach
    public void init() {
        this.id = UUID.randomUUID().toString();
        this.transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), MccEnum.CASH.name(), "Google");
    }

    @Test
    void testShouldAuthorizeAndReturnsAnAccount() {
        AccountBalance accountBalanceResponse = AccountBalanceUtil.makeAccountBalance(UUID.randomUUID().toString(), transaction.getAccount(), transaction.getTotalAmount(), MccEnum.valueOf(transaction.getMcc()), transaction.getMerchant());
        when(this.accountBalanceService.getAccount(any(TransactionInput.class))).thenReturn(accountBalanceResponse);
        final var accountResponse = this.balanceAuthorizationService.authorizeTransaction(transaction);
        ArgumentCaptor<TransactionInput> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionInput.class);

        verify(accountBalanceService, times(1))
                .getAccount(argumentCaptorTransaction.capture());

        assertEquals(accountBalanceResponse, accountResponse);
        assertEquals(transaction, argumentCaptorTransaction.getValue());
    }

    @Test
    void testShouldUnauthorizedATransaction() {
        AccountBalance accountBalanceResponse = AccountBalanceUtil.makeAccountBalance(id, transaction.getAccount(), transaction.getTotalAmount().subtract(new BigDecimal(10)), MccEnum.valueOf(transaction.getMcc()), transaction.getMerchant());
        when(this.accountBalanceService.getAccount(any(TransactionInput.class))).thenReturn(accountBalanceResponse);

        final var exception = assertThrows(TransactionRejectedException.class, () -> this.balanceAuthorizationService.authorizeTransaction(transaction));
        ArgumentCaptor<TransactionInput> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionInput.class);

        verify(accountBalanceService, times(1))
                .getAccount(argumentCaptorTransaction.capture());

        assertEquals("51", exception.getMessage());
        assertEquals(transaction, argumentCaptorTransaction.getValue());

    }

}