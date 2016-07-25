package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.Period;

public class ColumbaryUnit {
	private final String type;
	private final BigDecimal cost;
	private final RenewScheme scheme;

	public ColumbaryUnit(String type, BigDecimal cost, RenewScheme scheme) {
		this.type = type;
		this.cost = cost;
		this.scheme = scheme;
	}

	public String getType() {
		return type;
	}

	public BigDecimal getUnitCost() {
		return cost;
	}

	public Period getRenewalInterval() {
		return scheme.getPeriod();
	}

	public BigDecimal getRenewCost() {
		return scheme.getCost();
	}

	public RenewScheme getRenewScheme() {
		return scheme;
	}

}
