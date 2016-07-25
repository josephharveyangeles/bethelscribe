package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response;

import java.math.BigDecimal;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;

public interface RegistrationTransactionResponse {

	public BigDecimal getBalance();

	public DeceasedTransactionInformation getDeceasedInformation();

	public PayorTransactionInformation getPayorInformation();

	public UnitTransactionInformation getUnitInformation();

	public RegistrationResponseAccount getRegistrationAccount();

	public RenewalResponseAccount getRenewalAccount();

}
