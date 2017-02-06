package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RenewCycle {

	private final List<Payment> payments;
	private final LocalDate cycleDateStarted;

	public RenewCycle(Payment payment) {
		cycleDateStarted = payment.getPaymentDate();
		payments = new ArrayList<>();
		payments.add(payment);
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	public LocalDate getCycleStartDate() {
		return cycleDateStarted;
	}

	public BigDecimal removeLastPayment(final BigDecimal balance) {
		if (!payments.isEmpty()) {
			int indexOfLast = payments.size() - 1;
			Payment payment = payments.remove(indexOfLast);
			return balance.add(payment.getAmount());
		}
		return balance;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public boolean hasOnePayment() {
		return payments.size() == 1;
	}
}
