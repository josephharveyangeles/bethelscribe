package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.josephharveyangeles.bethelscribe.core.TransactionRequest;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;

public interface RegisterColumbaryTransactionRequest extends TransactionRequest {

	public String getARNumber();

	public BigDecimal getDownpayment();

	public LocalDate getDate();

	public DeceasedTransactionInformation getDeceasedInformation();

	public PayorTransactionInformation getPayorInformation();

	public UnitTransactionInformation getUnitInformation();
}
