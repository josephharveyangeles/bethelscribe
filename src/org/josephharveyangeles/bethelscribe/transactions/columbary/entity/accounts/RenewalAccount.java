package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RenewalAccount {

	private final Period renewInterval;
	private final BigDecimal renewCost;
	private final List<RenewCycle> renewCycles;
	private LocalDate renewDate;

	public RenewalAccount(LocalDate registrationDate, RenewScheme renewScheme) {
		renewInterval = renewScheme.getPeriod();
		renewCost = renewScheme.getCost();
		renewDate = registrationDate.plus(renewInterval);
		renewCycles = new ArrayList<>();
	}

	public RenewalAccount(LocalDate renewalDate, RenewScheme renewScheme, List<RenewCycle> cycles) {
		renewCost = renewScheme.getCost();
		renewInterval = renewScheme.getPeriod();
		renewDate = renewalDate;
		renewCycles = new ArrayList<>();
		renewCycles.addAll(cycles);
	}

	public Period getRenewPeriod() {
		return renewInterval;
	}

	public LocalDate getRenewDate() {
		return renewDate;
	}

	public BigDecimal getRenewCost() {
		return renewCost;
	}

	public int getRenewCycleCount() {
		return renewCycles.size();
	}

	public List<RenewCycle> getRenewCycles() {
		return renewCycles;
	}

	public List<Payment> getPaymentHistory() {
		return renewCycles.stream().flatMap(cycle -> cycle.getPayments().stream()).collect(Collectors.toList());
	}

	/**
	 * TODO renewCycle should be empty on first renewal!
	 * 
	 * @return
	 */
	public boolean canRenew() {
		return isLastCyclePaid();
	}

	private boolean isLastCyclePaid() {
		return renewCycles.isEmpty() || getTotalPaymentOnLastCycle().compareTo(renewCost) == 0;
	}

	private BigDecimal getTotalPaymentOnLastCycle() {
		BigDecimal totalPaidOnLastCyle = new BigDecimal(0);
		for (Payment p : getLastCycle().getPayments()) {
			totalPaidOnLastCyle = totalPaidOnLastCyle.add(p.getAmount());
		}
		return totalPaidOnLastCyle;
	}

	private RenewCycle getLastCycle() {
		return renewCycles.get(renewCycles.size() - 1);
	}

	/**
	 * TODO renewCycle is empty upon registration, payment is not allowed!
	 * 
	 * @param payment
	 */
	public void addPayment(Payment payment) {
		if (renewCycles.isEmpty()) {
			return;
		}

		if (!canRenew()) {
			getLastCycle().addPayment(payment);
		}
	}

	public BigDecimal renew(Payment payment) {
		BigDecimal balance = payment.getAmount();
		if (canRenew()) {
			renewCycles.add(new RenewCycle(payment));
			renewDate = renewDate.plus(renewInterval);
			balance = renewCost.subtract(payment.getAmount());
		}
		return balance;
	}

	/**
	 * Removes a single payment at a time. Removing the initial renew payment
	 * will also rollback the renewal date.
	 * 
	 * @param balance
	 *            current balance
	 * @return the updated balance
	 */
	public BigDecimal rollbackPayment(final BigDecimal balance) {
		BigDecimal result = balance;
		if (renewCycles.isEmpty()) {
			return result;
		}

		RenewCycle lastCycle = getLastCycle();
		if (lastCycle.hasOnePayment()) {
			rollbackLastCycle();
			result = getRenewCost();
		} else {
			result = lastCycle.removeLastPayment(balance);
		}
		return result;
	}

	private void rollbackLastCycle() {
		if (!renewCycles.isEmpty()) {
			renewCycles.remove(renewCycles.size() - 1);
			renewDate = renewDate.minus(renewInterval);
		}
	}
}
