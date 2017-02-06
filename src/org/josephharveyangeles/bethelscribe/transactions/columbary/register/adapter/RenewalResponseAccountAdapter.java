package org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.RenewChain;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters.RenewChainAdapter;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationRenewalResponseAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewalAccount;

class RenewalResponseAccountAdapter implements RegistrationRenewalResponseAccount {

	private final int renewPeriod;
	private final BigDecimal cost;
	private final List<RenewChain> chain;
	private final LocalDate date;

	RenewalResponseAccountAdapter(RenewalAccount renewal) {
		renewPeriod = renewal.getRenewPeriod().getYears();
		cost = renewal.getRenewCost();
		chain = new ArrayList<>();
		renewal.getRenewCycles().forEach(rc -> chain.add(new RenewChainAdapter(rc)));
		date = renewal.getRenewDate();
	}

	@Override
	public int getRenewPeriod() {
		return renewPeriod;
	}

	@Override
	public BigDecimal getRenewCost() {
		return cost;
	}

	@Override
	public LocalDate getRenewalDate() {
		return date;
	}

	@Override
	public List<RenewChain> getRenewChains() {
		return chain;
	}

}
