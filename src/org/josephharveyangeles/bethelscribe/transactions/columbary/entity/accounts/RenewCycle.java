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
		BigDecimal result;
		if (!payments.isEmpty()) {
			int indexOfLast = payments.size() - 1;
			result = balance.add(payments.get(indexOfLast).getAmount());
			payments.remove(indexOfLast);
		} else {
			result = balance;
		}
		return result;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public boolean hasOnePayment() {
		return payments.size() == 1;
	}
}
