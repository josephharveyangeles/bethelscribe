package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.request;

import java.time.LocalDate;

import org.josephharveyangeles.bethelscribe.core.TransactionRequest;

public interface RenewColumbaryTransactionRequest extends TransactionRequest {

	public String getRequestAccountID();

	public LocalDate getRenewDate();

}
