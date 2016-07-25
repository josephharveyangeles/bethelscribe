package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response;

import java.time.LocalDate;
import java.util.List;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PaymentTransaction;

public interface RegistrationResponseAccount {

	public LocalDate getRegistrationDate();

	public List<PaymentTransaction> getPayments();
}
