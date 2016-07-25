package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary;

import java.time.LocalDate;
import java.util.List;

public interface RenewChain {

	public List<PaymentTransaction> getPayments();

	public LocalDate getChainStarted();
}
