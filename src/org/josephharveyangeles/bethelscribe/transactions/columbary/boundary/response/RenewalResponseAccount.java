package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.RenewChain;

public interface RenewalResponseAccount {

	public int getRenewPeriod();

	public BigDecimal getRenewCost();

	public LocalDate getRenewalDate();

	public List<RenewChain> getRenewChains();
}
