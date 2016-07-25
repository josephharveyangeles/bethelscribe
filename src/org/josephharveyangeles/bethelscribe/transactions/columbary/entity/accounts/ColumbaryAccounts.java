package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;


public class ColumbaryAccounts {

	private final RegistrationAccount registration;

	private final RenewalAccount renewal;

	public ColumbaryAccounts(RegistrationAccount registration,
			RenewalAccount renewal) {
		this.registration = registration;
		this.renewal = renewal;
	}

	public RegistrationAccount getRegistration() {
		return registration;
	}

	public RenewalAccount getRenewal() {
		return renewal;
	}

}
