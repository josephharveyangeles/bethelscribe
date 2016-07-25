package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PaymentTransaction;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.Payment;

public class PaymentTransactionAdapter implements PaymentTransaction {

	private final BigDecimal amount;
	private final String reference;
	private final LocalDate date;

	public PaymentTransactionAdapter(Payment payment) {
		amount = payment.getAmount();
		reference = payment.getReferenceNumber();
		date = payment.getPaymentDate();
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public String getReferenceNumber() {
		return reference;
	}

	@Override
	public LocalDate getDate() {
		return date;
	}

}
