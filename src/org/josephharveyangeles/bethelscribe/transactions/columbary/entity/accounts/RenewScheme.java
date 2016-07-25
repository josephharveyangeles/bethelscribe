package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.Period;

public class RenewScheme {

	private final Period renewPeriod;
	private final BigDecimal cost;

	public RenewScheme(Period period, BigDecimal cost) {
		renewPeriod = period;
		this.cost = cost;
	}

	public Period getPeriod() {
		return renewPeriod;
	}

	public BigDecimal getCost() {
		return cost;
	}

}
