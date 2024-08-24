package io.github.jairandersonsouza.authorizer.services;

import io.github.jairandersonsouza.authorizer.controllers.requests.TransactionInput;
import io.github.jairandersonsouza.authorizer.entities.AccountBalance;
import io.github.jairandersonsouza.authorizer.entities.MccEnum;
import io.github.jairandersonsouza.authorizer.entities.Transaction;
import io.github.jairandersonsouza.authorizer.exceptions.AccountNotExistsException;
import io.github.jairandersonsouza.authorizer.exceptions.TransactionRejectedException;
import io.github.jairandersonsouza.authorizer.repositories.TransactionRepository;
import io.github.jairandersonsouza.authorizer.util.AccountBalanceUtil;
import io.github.jairandersonsouza.authorizer.util.TransactionUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static io.github.jairandersonsouza.authorizer.entities.MccEnum.FOOD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FoodTransactionServiceTest {

    @InjectMocks
    private FoodTransactionService foodTransactionService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private AccountBalanceService accountBalanceService;

    @Mock
    private TransactionRepository transactionRepository;


    @Test
    void testShouldProcessFoodPayment() {
        var transactionInput = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), FOOD.name(), "Google");
        var transaction = TransactionUtil.makeTransaction(transactionInput);
        AccountBalance accountBalanceResponse = AccountBalanceUtil.makeAccountBalance(UUID.randomUUID().toString(), transactionInput.getAccount(), transactionInput.getTotalAmount(), MccEnum.valueOf(transaction.getMcc()), transactionInput.getMerchant());

        when(authorizationService.authorizeTransaction(any(TransactionInput.class))).thenReturn(accountBalanceResponse);
        doNothing().when(accountBalanceService).save(any(AccountBalance.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        this.foodTransactionService.startTransaction(transactionInput);
        final ArgumentCaptor<TransactionInput> argumentCaptorTransactionInput = ArgumentCaptor.forClass(TransactionInput.class);
        final ArgumentCaptor<AccountBalance> argumentCaptorSaveAccountBalance = ArgumentCaptor.forClass(AccountBalance.class);
        final ArgumentCaptor<Transaction> argumentCaptorTransaction = ArgumentCaptor.forClass(Transaction.class);

        verify(authorizationService)
                .authorizeTransaction(argumentCaptorTransactionInput.capture());
        verify(accountBalanceService)
                .save(argumentCaptorSaveAccountBalance.capture());
        verify(transactionRepository)
                .save(argumentCaptorTransaction.capture());

        assertEquals(transactionInput, argumentCaptorTransactionInput.getValue());

        assertEquals(accountBalanceResponse, argumentCaptorSaveAccountBalance.getValue());
        assertEquals(transaction.getAmount(), argumentCaptorTransaction.getValue().getAmount());
    }

    @Test
    void testShouldThrownAnTransactionRejected() {
        var transactionInput = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), FOOD.name(), "Google");

        when(authorizationService.authorizeTransaction(any(TransactionInput.class))).thenThrow(TransactionRejectedException.class);

        var exception = assertThrows(TransactionRejectedException.class, () -> {
            this.foodTransactionService.startTransaction(transactionInput);
        });

        final ArgumentCaptor<TransactionInput> argumentCaptorTransactionInput = ArgumentCaptor.forClass(TransactionInput.class);
        final ArgumentCaptor<AccountBalance> argumentCaptorSaveAccountBalance = ArgumentCaptor.forClass(AccountBalance.class);
        final ArgumentCaptor<Transaction> argumentCaptorTransaction = ArgumentCaptor.forClass(Transaction.class);

        verify(authorizationService, times(1))
                .authorizeTransaction(argumentCaptorTransactionInput.capture());

        verify(accountBalanceService, times(0))
                .save(argumentCaptorSaveAccountBalance.capture());
        verify(transactionRepository, times(0))
                .save(argumentCaptorTransaction.capture());

        assertEquals(transactionInput, argumentCaptorTransactionInput.getValue());
        assertEquals("51", exception.getMessage());
        assertInstanceOf(TransactionRejectedException.class, exception);
    }

    @Test
    void testShouldThrownAnExceptionAccountNotExists() {
        var transactionInput = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), FOOD.name(), "Google");

        when(authorizationService.authorizeTransaction(any(TransactionInput.class))).thenThrow(AccountNotExistsException.class);

        var exception = assertThrows(AccountNotExistsException.class, () -> {
            this.foodTransactionService.startTransaction(transactionInput);
        });

        final ArgumentCaptor<TransactionInput> argumentCaptorTransactionInput = ArgumentCaptor.forClass(TransactionInput.class);
        final ArgumentCaptor<AccountBalance> argumentCaptorSaveAccountBalance = ArgumentCaptor.forClass(AccountBalance.class);
        final ArgumentCaptor<Transaction> argumentCaptorTransaction = ArgumentCaptor.forClass(Transaction.class);

        verify(authorizationService, times(1))
                .authorizeTransaction(argumentCaptorTransactionInput.capture());
        verify(accountBalanceService, times(0))
                .save(argumentCaptorSaveAccountBalance.capture());
        verify(transactionRepository, times(0))
                .save(argumentCaptorTransaction.capture());
        assertEquals(transactionInput, argumentCaptorTransactionInput.getValue());
        assertInstanceOf(AccountNotExistsException.class, exception);
    }

    @Test
    void testShouldSaveTransaction() {
        var transaction = TransactionUtil.makeTransaction(TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), FOOD.name(), "Google"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        this.foodTransactionService.save(transaction);
        final ArgumentCaptor<Transaction> argumentCaptorTransaction = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository, times(1))
                .save(argumentCaptorTransaction.capture());
        assertEquals(transaction, argumentCaptorTransaction.getValue());
    }

    @Test
    void testShouldThrownAnExceptionWhenSaveTransaction() {
        var transaction = TransactionUtil.makeTransaction(TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), FOOD.name(), "Google"));
        when(transactionRepository.save(any(Transaction.class))).thenThrow(RuntimeException.class);

        final var exception = assertThrows(TransactionRejectedException.class, () -> {
            this.foodTransactionService.save(transaction);
        });

        final ArgumentCaptor<Transaction> argumentCaptorTransaction = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository, times(1))
                .save(argumentCaptorTransaction.capture());

        assertEquals("51", exception.getMessage());
        assertEquals(transaction, argumentCaptorTransaction.getValue());
    }

    @Test
    void testShouldGetMcc() {
        String mcc = this.foodTransactionService.getMcc();
        assertEquals(FOOD.name(), mcc);

    }
}