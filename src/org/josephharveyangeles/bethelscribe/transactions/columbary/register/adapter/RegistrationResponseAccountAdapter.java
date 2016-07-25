package org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PaymentTransaction;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters.PaymentTransactionAdapter;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationResponseAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RegistrationAccount;

class RegistrationResponseAccountAdapter implements RegistrationResponseAccount {

	private final LocalDate date;
	private final List<PaymentTransaction> payments;

	RegistrationResponseAccountAdapter(RegistrationAccount registration) {
		date = registration.getRegistrationDate();
		payments = new ArrayList<>();
		registration.getPaymentHistory().forEach(p -> payments.add(new PaymentTransactionAdapter(p)));
	}

	@Override
	public LocalDate getRegistrationDate() {
		return date;
	}

	@Override
	public List<PaymentTransaction> getPayments() {
		return payments;
	}

}
