package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the Renew Process Entity. Note that, payment on
 * {@code RenewalAccount} refers to payment on {@code RenewCost} only,
 * independent from {@code Registration} thus, adding {@code payments} on a
 * un-renewed {@code RenewalAccount} is not possible.
 * 
 * @author yev
 * @since 2017
 */
public class RenewalAccount {

	private final Period renewInterval;
	private final BigDecimal renewCost;
	private final List<RenewCycle> renewCycles;
	private LocalDate renewDate;

	/**
	 * Creates a {@code RenewalAccount} on the same date of Registration. The
	 * renewal date will be initialized to
	 * {@code registrationDate + renewPeriod}. This scenario is the prime
	 * {@code RenewalAccount} under this setup, payments can only be added upon
	 * renewal.
	 * 
	 * @param registrationDate
	 *            date of registration
	 * @param renewScheme
	 *            renewal scheme
	 */
	public RenewalAccount(final LocalDate registrationDate, final RenewScheme renewScheme) {
		renewInterval = renewScheme.getPeriod();
		renewCost = renewScheme.getCost();
		renewDate = registrationDate.plus(renewInterval);
		renewCycles = new ArrayList<>();
	}

	/**
	 * Creates a {@code RenewalAccount} from raw information. Useful when
	 * creating an instance out of a persistence entity like a database.
	 * 
	 * @param renewalDate
	 * @param renewScheme
	 * @param cycles
	 */
	public RenewalAccount(final LocalDate renewalDate, final RenewScheme renewScheme, final List<RenewCycle> cycles) {
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
	 * @return if it's possible to renew.
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

	public void addPayment(Payment payment) {
		if (renewCycles.isEmpty()) {
			return;
		}

		if (!canRenew()) {
			getLastCycle().addPayment(payment);
		}
	}

	/**
	 * Renews the account. Note that, renewal can only be made on a settled
	 * {@code RenewCyle}, it is a good practice to call
	 * {@link RenewalAccount#canRenew() canRenew()} first.
	 * 
	 * @see RenewCycle
	 * @param payment
	 *            renewal payment
	 * @return remaining balance
	 */
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
