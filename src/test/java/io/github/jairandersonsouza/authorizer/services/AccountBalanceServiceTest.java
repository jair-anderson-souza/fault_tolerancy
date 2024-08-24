package io.github.jairandersonsouza.authorizer.services;

import io.github.jairandersonsouza.authorizer.entities.AccountBalance;
import io.github.jairandersonsouza.authorizer.entities.MccEnum;
import io.github.jairandersonsouza.authorizer.exceptions.AccountNotExistsException;
import io.github.jairandersonsouza.authorizer.exceptions.TransactionRejectedException;
import io.github.jairandersonsouza.authorizer.repositories.AccountBalanceRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountBalanceServiceTest {

    @InjectMocks
    private AccountBalanceService accountBalanceService;

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    private TransactionInput transactionInput;

    @BeforeEach
    public void init() {
        this.transactionInput = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), MccEnum.FOOD.name(), "Google");
    }

    @Test
    void testShouldReturnsAValidAccount() {
        AccountBalance accountBalanceResponse = AccountBalanceUtil.makeAccountBalance(UUID.randomUUID().toString(), transactionInput.getAccount(), transactionInput.getTotalAmount(), MccEnum.FOOD, transactionInput.getMerchant());
        when(this.accountBalanceRepository.findByAccountIdAndMcc(any(String.class), any(MccEnum.class))).thenReturn(Optional.of(accountBalanceResponse));
        final var accountResponse = this.accountBalanceService.getAccount(transactionInput);
        ArgumentCaptor<String> argumentCaptorAccountId = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MccEnum> argumentCaptorMcc = ArgumentCaptor.forClass(MccEnum.class);
        verify(accountBalanceRepository, times(1))
                .findByAccountIdAndMcc(argumentCaptorAccountId.capture(), argumentCaptorMcc.capture());

        assertEquals(accountBalanceResponse, accountResponse);
        assertEquals(accountResponse.getAccountId(), argumentCaptorAccountId.getValue());
        assertEquals(accountResponse.getMcc(), argumentCaptorMcc.getValue());
    }

    @Test
    void testShouldThrownAccountNotExistsException() {

        when(this.accountBalanceRepository.findByAccountIdAndMcc(any(String.class), any(MccEnum.class))).thenReturn(Optional.empty());
        final var exception = assertThrows(AccountNotExistsException.class, () -> this.accountBalanceService.getAccount(transactionInput));

        ArgumentCaptor<String> argumentCaptorAccountId = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MccEnum> argumentCaptorMcc = ArgumentCaptor.forClass(MccEnum.class);

        verify(accountBalanceRepository, times(1))
                .findByAccountIdAndMcc(argumentCaptorAccountId.capture(), argumentCaptorMcc.capture());
        assertEquals("07", exception.getMessage());
    }

    @Test
    void testShouldSave() {
        AccountBalance accountBalanceResponse = AccountBalanceUtil.makeAccountBalance(UUID.randomUUID().toString(), transactionInput.getAccount(), new BigDecimal(10), MccEnum.FOOD, transactionInput.getMerchant());
        doNothing().when(this.accountBalanceRepository).update(any(BigDecimal.class), any(String.class));

        this.accountBalanceService.save(accountBalanceResponse);

        ArgumentCaptor<BigDecimal> argumentCaptorAccountBalance = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<String> argumentCaptorAccountAccountId = ArgumentCaptor.forClass(String.class);

        verify(accountBalanceRepository, times(1))
                .update(argumentCaptorAccountBalance.capture(), argumentCaptorAccountAccountId.capture());

        assertEquals(accountBalanceResponse.getBalance(), argumentCaptorAccountBalance.getValue());
        assertEquals(accountBalanceResponse.getId(), argumentCaptorAccountAccountId.getValue());
    }

    @Test
    void testShouldThrownAnExceptionForSaving() {
        AccountBalance accountBalanceResponse = AccountBalanceUtil.makeAccountBalance(UUID.randomUUID().toString(), transactionInput.getAccount(), new BigDecimal(10), null, transactionInput.getMerchant());
        doThrow(RuntimeException.class).when(this.accountBalanceRepository).update(any(BigDecimal.class), any(String.class));

        final var exception = assertThrows(TransactionRejectedException.class, () -> this.accountBalanceService.save(accountBalanceResponse));

        ArgumentCaptor<BigDecimal> argumentCaptorAccountBalance = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<String> argumentCaptorAccountAccountId = ArgumentCaptor.forClass(String.class);

        verify(accountBalanceRepository, times(1))
                .update(argumentCaptorAccountBalance.capture(), argumentCaptorAccountAccountId.capture());

        assertEquals(accountBalanceResponse.getBalance(), argumentCaptorAccountBalance.getValue());
        assertEquals(accountBalanceResponse.getId(), argumentCaptorAccountAccountId.getValue());
        assertEquals("51", exception.getMessage());

    }

}