package io.github.jairandersonsouza.authorizer.util;

import io.github.jairandersonsouza.authorizer.entities.Transaction;
import io.github.jairandersonsouza.authorizer.controllers.requests.TransactionInput;

import java.math.BigDecimal;

public class TransactionUtil {

    public static TransactionInput makeTransactionInput(String accountId, BigDecimal totalAmount, String mcc, String merchant) {
        return TransactionInput.create(
                accountId,
                totalAmount,
                mcc,
                merchant);
    }

    public static Transaction makeTransaction(TransactionInput transactionInput) {
        return Transaction.create(transactionInput);
    }
}
