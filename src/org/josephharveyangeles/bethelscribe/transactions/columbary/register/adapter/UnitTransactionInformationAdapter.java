package org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter;

import java.math.BigDecimal;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryUnit;

class UnitTransactionInformationAdapter implements UnitTransactionInformation {

	private final String type;
	private final BigDecimal cost;
	private final int renewInterval;
	private final BigDecimal renewCost;

	UnitTransactionInformationAdapter(ColumbaryUnit uInfo) {
		type = uInfo.getType();
		cost = uInfo.getUnitCost();
		renewCost = uInfo.getRenewCost();
		renewInterval = uInfo.getRenewalInterval().getYears();
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public BigDecimal getUnitCost() {
		return cost;
	}

	@Override
	public int getRenewInterval() {
		return renewInterval;
	}

	@Override
	public BigDecimal getRenewCost() {
		return renewCost;
	}

}
