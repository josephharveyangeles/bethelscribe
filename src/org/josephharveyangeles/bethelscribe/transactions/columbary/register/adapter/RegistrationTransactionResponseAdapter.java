package org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter;

import java.math.BigDecimal;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationResponseAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationTransactionResponse;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RenewalResponseAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.ColumbaryEntity;

public class RegistrationTransactionResponseAdapter implements RegistrationTransactionResponse {

	private final BigDecimal balance;
	private final DeceasedTransactionInformation dInfo;
	private final PayorTransactionInformation pInfo;
	private final UnitTransactionInformation uInfo;
	private final RegistrationResponseAccount registration;
	private final RenewalResponseAccount renewal;

	public RegistrationTransactionResponseAdapter(ColumbaryEntity entity) {
		balance = entity.getBalance();
		dInfo = new DeceasedTransactionInformationAdapter(entity.getDeceasedInformation());
		pInfo = new PayorTransactionInformationAdapter(entity.getPayorInformation());
		uInfo = new UnitTransactionInformationAdapter(entity.getColumbaryInformation());
		registration = new RegistrationResponseAccountAdapter(entity.getRegistrationAccount());
		renewal = new RenewalResponseAccountAdapter(entity.getRenewalAccount());
	}

	@Override
	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	public DeceasedTransactionInformation getDeceasedInformation() {
		return dInfo;
	}

	@Override
	public PayorTransactionInformation getPayorInformation() {
		return pInfo;
	}

	@Override
	public UnitTransactionInformation getUnitInformation() {
		return uInfo;
	}

	@Override
	public RegistrationResponseAccount getRegistrationAccount() {
		return registration;
	}

	@Override
	public RenewalResponseAccount getRenewalAccount() {
		return renewal;
	}
}
