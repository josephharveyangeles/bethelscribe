package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

public class PaymentTest {

	@Test
	public void testPaymentInitialization() {
		final LocalDate firstDate = LocalDate.of(1992, 4, 21);
		final String arnum1 = "ARNUMBER";
		final BigDecimal amountOne = new BigDecimal("200");

		Payment payment = new Payment(arnum1, amountOne, firstDate);

		assertNotNull(payment);
		assertEquals(arnum1, payment.getReferenceNumber());
		assertEquals(amountOne, payment.getAmount());
		assertEquals(firstDate, payment.getPaymentDate());

		final String arnum2 = "arnum";
		final BigDecimal amount2 = new BigDecimal(300);
		Payment payment2 = new Payment(arnum2, amount2);
		assertNotNull(payment2);
		assertEquals(arnum2, payment2.getReferenceNumber());
		assertEquals(amount2, payment2.getAmount());
		assertEquals(LocalDate.now(), payment2.getPaymentDate());
	}

	@Test
	public void testGetARNumber() {
		String arNumber = "A#124";
		BigDecimal amount = new BigDecimal("1000");
		LocalDate date = LocalDate.now();
		Payment payment = new Payment(arNumber, amount, date);
		assertEquals(arNumber, payment.getReferenceNumber());
	}

	@Test
	public void testGetARNumber_Invalid() {
		String arNumber = "A#124";
		BigDecimal amount = new BigDecimal("1000");
		LocalDate date = LocalDate.now();
		Payment payment = new Payment(arNumber, amount, date);
		String aDifferentAR = "sfef";
		assertNotEquals(aDifferentAR, payment.getReferenceNumber());
	}

	@Test
	public void testGetAmount() {
		String arNumber = "A#124";
		BigDecimal amount = new BigDecimal("1000");
		LocalDate date = LocalDate.now();
		Payment payment = new Payment(arNumber, amount, date);
		assertEquals(amount, payment.getAmount());
	}

	@Test
	public void testGetAmount_Invalid() {
		String arNumber = "A#124";
		BigDecimal amount = new BigDecimal("1000");
		LocalDate date = LocalDate.of(1992, 4, 21);
		Payment payment = new Payment(arNumber, amount, date);
		BigDecimal aDifferentAmount = new BigDecimal("200");
		assertNotEquals(aDifferentAmount, payment.getAmount());
	}

	@Test
	public void testGetPaymentDate() {
		String arNumber = "A#124";
		BigDecimal amount = new BigDecimal("1000");
		LocalDate date = LocalDate.of(1990, 12, 18);
		Payment payment = new Payment(arNumber, amount, date);
		assertEquals(date, payment.getPaymentDate());
	}

	@Test
	public void testGetPaymentDate_Invalid() {
		String arNumber = "A#124";
		BigDecimal amount = new BigDecimal("1000");
		LocalDate date = LocalDate.now();
		Payment payment = new Payment(arNumber, amount, date);
		LocalDate aDifferentDate = LocalDate.ofYearDay(1990, 320);
		assertNotEquals(aDifferentDate, payment.getPaymentDate());
	}

}
