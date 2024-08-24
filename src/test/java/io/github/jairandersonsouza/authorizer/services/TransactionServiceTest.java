package io.github.jairandersonsouza.authorizer.services;


import io.github.jairandersonsouza.authorizer.repositories.TransactionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private CashTransactionService cashTransactionService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private AccountBalanceService accountBalanceService;

    @Mock
    private TransactionRepository transactionRepository;


}