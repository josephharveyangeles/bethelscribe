package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentTransaction {

	public BigDecimal getAmount();

	public String getReferenceNumber();

	public LocalDate getDate();
}
