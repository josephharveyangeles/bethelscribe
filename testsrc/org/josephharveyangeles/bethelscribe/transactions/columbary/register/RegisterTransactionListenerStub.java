package org.josephharveyangeles.bethelscribe.transactions.columbary.register;

import java.math.BigDecimal;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationResponseAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationTransactionResponse;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationRenewalResponseAccount;

public class RegisterTransactionListenerStub implements RegisterColumbaryTransactionListener {
	private boolean isSet;
	private BigDecimal balance;
	private DeceasedTransactionInformation dinfo;
	private PayorTransactionInformation pinfo;
	private UnitTransactionInformation uinfo;
	private RegistrationResponseAccount registration;
	private RegistrationRenewalResponseAccount renewal;

	@Override
	public void registered(RegistrationTransactionResponse registrationResult) {
		balance = registrationResult.getBalance();
		dinfo = registrationResult.getDeceasedInformation();
		pinfo = registrationResult.getPayorInformation();
		uinfo = registrationResult.getUnitInformation();
		registration = registrationResult.getRegistrationAccount();
		renewal = registrationResult.getRenewalAccount();
		set();
	}

	public boolean isSet() {
		return isSet;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public DeceasedTransactionInformation getDeceasedInformation() {
		return dinfo;
	}

	public PayorTransactionInformation getPayorInformation() {
		return pinfo;
	}

	public UnitTransactionInformation getUnitInformation() {
		return uinfo;
	}

	public RegistrationResponseAccount getRegistrationAccount() {
		return registration;
	}

	public RegistrationRenewalResponseAccount getRenewalAccount() {
		return renewal;
	}

	private void set() {
		isSet = true;
	}
}