package org.josephharveyangeles.bethelscribe.transactions.columbary.register;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationTransactionResponse;

public interface RegisterColumbaryTransactionListener {

	public void registered(RegistrationTransactionResponse registrationResult);
}
