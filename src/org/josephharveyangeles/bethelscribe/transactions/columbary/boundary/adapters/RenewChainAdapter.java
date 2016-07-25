package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PaymentTransaction;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.RenewChain;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewCycle;

public class RenewChainAdapter implements RenewChain {

	private final List<PaymentTransaction> payments;
	private final LocalDate date;

	public RenewChainAdapter(RenewCycle cycle) {
		date = cycle.getCycleStartDate();
		payments = new ArrayList<>();
		cycle.getPayments().forEach(p -> payments.add(new PaymentTransactionAdapter(p)));
	}

	@Override
	public List<PaymentTransaction> getPayments() {
		return payments;
	}

	@Override
	public LocalDate getChainStarted() {
		return date;
	}

}
