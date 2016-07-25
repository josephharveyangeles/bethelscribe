package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class RegistrationAccountTest {

	private static final BigDecimal UnitCost = new BigDecimal(10_000);

	@Test
	public void testInitialization() {
		testInitializationOnCurrentDate();
		testInitializationOnDifferentDate();
	}

	@Test
	public void testIsRegistrationPaid() {
		testIsRegistrationPaid_OnHalfDownPayment();
		testIsRegistrationPaid_OnFullDownPayment();
		testIsRegistrationPaid_OnSecondPayment();
		testIsRegistrationPaid_OnFourPayments();
	}

	@Test
	public void testRegistrationPaymentRemoval() {
		testRegistrationPaymentRemoval_OnceOnOnePayment();
		testRegistrationPaymentRemoval_TwiceOnOnePayment();
		testRegistrationPaymentRemoval_OnPayments();
	}

	private void testInitializationOnCurrentDate() {
		Payment payment = new Payment("arnumber", new BigDecimal(2_000), LocalDate.now());
		RegistrationAccount registration = new RegistrationAccount(LocalDate.now(), payment);
		assertNotNull(registration);
		assertEquals(LocalDate.now(), registration.getRegistrationDate());
		assertEquals(payment.getAmount(), registration.getLastPaymentAmount());

		List<Payment> paymentsHistory = registration.getPaymentHistory();
		assertEquals(1, paymentsHistory.size());
	}

	private void testInitializationOnDifferentDate() {
		Payment payment = new Payment("arnumber", new BigDecimal(2_000), LocalDate.now());
		LocalDate registrationDate = LocalDate.ofYearDay(1990, 320);
		RegistrationAccount registration = new RegistrationAccount(registrationDate, payment);
		assertNotNull(registration);
		assertEquals(registrationDate, registration.getRegistrationDate());
		assertEquals(payment.getAmount(), registration.getLastPaymentAmount());

		List<Payment> paymentsHistory = registration.getPaymentHistory();
		assertEquals(1, paymentsHistory.size());
	}

	private void testIsRegistrationPaid_OnHalfDownPayment() {
		RegistrationAccount registration = createInitialRegistrationAccount(UnitCost.doubleValue() / 2);
		assertFalse(registration.isPaid(UnitCost));
	}

	private void testIsRegistrationPaid_OnFullDownPayment() {
		RegistrationAccount registration = createInitialRegistrationAccount(UnitCost.doubleValue());
		assertTrue(registration.isPaid(UnitCost));
	}

	private void testIsRegistrationPaid_OnSecondPayment() {
		double halfCost = UnitCost.doubleValue() / 2;
		RegistrationAccount registration = createInitialRegistrationAccount(halfCost);
		assertFalse(registration.isPaid(UnitCost));
		Payment secondPayment = new Payment("arnumber", new BigDecimal(halfCost), LocalDate.now());
		registration.addPayment(secondPayment);
		assertTrue(registration.isPaid(UnitCost));
		assertEquals(secondPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(2, registration.getPaymentHistory().size());
	}

	private void testIsRegistrationPaid_OnFourPayments() {
		double quarterCost = UnitCost.doubleValue() / 4;
		RegistrationAccount registration = createInitialRegistrationAccount(quarterCost);
		assertFalse(registration.isPaid(UnitCost));

		Payment secondPayment = new Payment("arnumber", new BigDecimal(quarterCost), LocalDate.now());
		registration.addPayment(secondPayment);
		assertFalse(registration.isPaid(UnitCost));

		Payment thirdPayment = new Payment("arnumber", new BigDecimal(quarterCost), LocalDate.now());
		registration.addPayment(thirdPayment);
		assertFalse(registration.isPaid(UnitCost));

		Payment fourthPayment = new Payment("arnumber", new BigDecimal(quarterCost), LocalDate.now());
		registration.addPayment(fourthPayment);
		assertTrue(registration.isPaid(UnitCost));

		assertEquals(fourthPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(4, registration.getPaymentHistory().size());
	}

	private void testRegistrationPaymentRemoval_OnceOnOnePayment() {
		final LocalDate registerDate = LocalDate.ofYearDay(1990, 320);
		Payment initialPayment = new Payment("ARNUM", new BigDecimal(1000), registerDate);
		final BigDecimal balance = UnitCost.subtract(initialPayment.getAmount());
		RegistrationAccount registration = new RegistrationAccount(registerDate, initialPayment);
		assertFalse(registration.isPaid(UnitCost));
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(registerDate, registration.getRegistrationDate());
		assertEquals(1, registration.getPaymentHistory().size());

		final BigDecimal postRemoveBalance = registration.rollbackPayment(balance);
		assertEquals(balance, postRemoveBalance);
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(1, registration.getPaymentHistory().size());
	}

	private void testRegistrationPaymentRemoval_TwiceOnOnePayment() {
		final LocalDate registerDate = LocalDate.ofYearDay(1990, 320);
		Payment initialPayment = new Payment("ARNUM", new BigDecimal(1000), registerDate);
		final BigDecimal balance = UnitCost.subtract(initialPayment.getAmount());
		RegistrationAccount registration = new RegistrationAccount(registerDate, initialPayment);
		assertFalse(registration.isPaid(UnitCost));
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(registerDate, registration.getRegistrationDate());
		assertEquals(1, registration.getPaymentHistory().size());

		// Remove one
		final BigDecimal postRemoveBalance = registration.rollbackPayment(balance);
		assertEquals(balance, postRemoveBalance);
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(1, registration.getPaymentHistory().size());

		// Remove another one
		assertEquals(balance, registration.rollbackPayment(postRemoveBalance));
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(1, registration.getPaymentHistory().size());
	}

	private void testRegistrationPaymentRemoval_OnPayments() {
		final LocalDate registerDate = LocalDate.ofYearDay(1990, 320);
		final Payment initialPayment = new Payment("ARNUM", new BigDecimal(1000), registerDate);
		final BigDecimal balance = UnitCost.subtract(initialPayment.getAmount());
		final RegistrationAccount registration = new RegistrationAccount(registerDate, initialPayment);
		assertFalse(registration.isPaid(UnitCost));
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(registerDate, registration.getRegistrationDate());
		assertEquals(1, registration.getPaymentHistory().size());

		// Add another payment
		final Payment secondaryPayment = new Payment("ARNUM2", new BigDecimal(9000), LocalDate.now());
		final BigDecimal secondaryBalance = balance.subtract(secondaryPayment.getAmount());

		registration.addPayment(secondaryPayment);
		assertEquals(BigDecimal.ZERO, secondaryBalance);
		assertTrue(registration.isPaid(UnitCost));
		assertEquals(secondaryPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(registerDate, registration.getRegistrationDate());
		assertEquals(2, registration.getPaymentHistory().size());

		final BigDecimal postRemoveBalance = registration.rollbackPayment(secondaryBalance);
		assertEquals(balance, postRemoveBalance);
		assertEquals(initialPayment.getAmount(), registration.getLastPaymentAmount());
		assertEquals(1, registration.getPaymentHistory().size());
	}

	private RegistrationAccount createInitialRegistrationAccount(double amount) {
		Payment payment = new Payment("arnumber", new BigDecimal(amount), LocalDate.now());
		return new RegistrationAccount(LocalDate.now(), payment);
	}

}
