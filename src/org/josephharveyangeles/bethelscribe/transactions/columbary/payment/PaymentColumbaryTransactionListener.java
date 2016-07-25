package org.josephharveyangeles.bethelscribe.transactions.columbary.payment;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PaymentTransaction;

public interface PaymentColumbaryTransactionListener {

	public void paidRegistration(String id, PaymentTransaction payment);

	public void paidRenewal(String id);

}
