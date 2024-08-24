package io.github.jairandersonsouza.authorizer.factories;

import io.github.jairandersonsouza.authorizer.services.CashTransactionService;
import io.github.jairandersonsouza.authorizer.services.FoodTransactionService;
import io.github.jairandersonsouza.authorizer.services.MealTransactionService;
import io.github.jairandersonsouza.authorizer.services.TransactionService;
import io.github.jairandersonsouza.authorizer.util.TransactionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;


import static io.github.jairandersonsouza.authorizer.entities.MccEnum.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceFactoryTest {

    @InjectMocks
    private TransactionServiceFactory transactionServiceFactory;

    private Map<String, TransactionService> services = mock(Map.class);


    @BeforeEach
    public void init() {
        when(services.get(FOOD.name())).thenReturn(new FoodTransactionService());
        when(services.get(CASH.name())).thenReturn(new CashTransactionService());
        when(services.get(MEAL.name())).thenReturn(new MealTransactionService());
    }


    @Test
    void testShouldReturnsMealTransactionService() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "5811", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(MealTransactionService.class, processor);
    }

    @Test
    void testShouldAlsoReturnsMealTransactionService() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "5811", "Google");

        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(MealTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsCashTransactionServiceBecauseTheMccIsInvalid() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "5800", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsFoodTransactionService() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "5412", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(FoodTransactionService.class, processor);
    }

    @Test
    void testShouldAlsoReturnsFoodTransactionService() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "5411", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(FoodTransactionService.class, processor);
    }

    @Test
    void testShouldAlsoReturnsCashTransactionServiceBecauseTheAmountIsInvalid() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "5412", "Google");


        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(FoodTransactionService.class, processor);
    }

    @Test
    void testShouldAlsoReturnsCashTransactionServiceBecauseTheMccIsInvalid() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShouldAlsoReturnsCashTransactionServiceBecauseTheMccIsAlsoInvalid() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "2234", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsCashTransactionServiceBecauseTheMccIsAlsoInvalid() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "Google");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShould() {
        var transaction = TransactionUtil.makeTransactionInput("026b36f0-8454-4af4-b223-f94fa6bc3568", new BigDecimal(1), "2568", "UBER EATS                   SAO PAULO BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(MealTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsMealTransactionServiceBecauseTheMccIsInvalidAndMerchantIsEats() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "UBER EATS                   SAO PAULO BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(MealTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsMealTransactionServiceBecauseTheMccIsInvalidAndMerchantIsIfood() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "IFOOD                   SAO PAULO BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(MealTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsCashTransactionServiceBecauseTheMccIsInvalidAndMerchantIsUberTrip() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "UBER TRIP                   SAO PAULO BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsCashTransactionServiceBecauseTheMccIsInvalidAndMerchantIsPag() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "PAG*JoseDaSilva          RIO DE JANEI BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShouldAlsoReturnsCashTransactionServiceBecauseTheMccIsInvalidAndMerchantIsBilheteUnico() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "PICPAY*BILHETEUNICO           GOIANIA BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(CashTransactionService.class, processor);
    }

    @Test
    void testShouldReturnsFoodTransactionServiceBecauseTheMccIsInvalidAndMerchantIsHiper() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "HIPER           GOIANIA BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(FoodTransactionService.class, processor);
    }


    @Test
    void testShouldReturnsFoodTransactionServiceBecauseTheMccIsInvalidAndMerchantIsExtra() {
        var transaction = TransactionUtil.makeTransactionInput("1123", new BigDecimal(100), "3454", "EXTRA*DAESQUINA           GOIANIA BR ");
        final var processor = this.transactionServiceFactory.getProcessor(transaction);
        assertInstanceOf(FoodTransactionService.class, processor);
    }


}