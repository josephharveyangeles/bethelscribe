package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.ColumbaryEntity;

public class RegistrationAccount {

	private final List<Payment> payments;
	private final LocalDate registrationDate;

	public RegistrationAccount(LocalDate date, Payment initialPayment) {
		payments = new ArrayList<>();
		payments.add(initialPayment);
		registrationDate = date;
	}

	public RegistrationAccount(LocalDate registerDate, List<Payment> paymentz) {
		payments = new ArrayList<>();
		payments.addAll(paymentz);
		registrationDate = registerDate;
	}

	public boolean isPaid(BigDecimal unitcost) {
		BigDecimal totalPayment = new BigDecimal(0);
		for (Payment payment : payments) {
			totalPayment = totalPayment.add(payment.getAmount());
		}
		return totalPayment.compareTo(unitcost) == 0;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public List<Payment> getPaymentHistory() {
		return payments;
	}

	public BigDecimal getLastPaymentAmount() {
		return payments.get(payments.size() - 1).getAmount();
	}

	/**
	 * Issue a payment on this registration account. Updating of balance is
	 * outside of its responsibility, see
	 * {@link ColumbaryEntity#addRegistrationPayment(Payment)
	 * ColumbaryAccount.addRegistrationPayment}
	 * 
	 * @param payment
	 */
	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	/**
	 * Removes the previous payment on this registration account. Attempting to
	 * remove the last remaining payment will do nothing, because that
	 * constitute to actual removal of the whole {@link ColumbaryEntity}.
	 * 
	 * @param balance
	 *            the current balance
	 * @return the updated balance
	 */
	public BigDecimal rollbackPayment(BigDecimal balance) {
		BigDecimal result = balance;
		if (payments.size() > 1) {
			int indexOfLast = payments.size() - 1;
			result = balance.add(payments.get(indexOfLast).getAmount());
			payments.remove(indexOfLast);
		}
		return result;
	}

}
