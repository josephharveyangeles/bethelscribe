package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary;

import java.math.BigDecimal;

public interface UnitTransactionInformation {

	public String getType();

	public BigDecimal getUnitCost();

	public int getRenewInterval();

	public BigDecimal getRenewCost();

}
