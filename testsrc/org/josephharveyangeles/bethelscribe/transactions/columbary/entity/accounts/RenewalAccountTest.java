package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import org.junit.Test;
import org.mockito.Mockito;

import jdk.nashorn.internal.ir.annotations.Ignore;

public class RenewalAccountTest {

	private static final int RENEWAL_COST = 10_000;

	@Test
	public void testInitialization() {
		final Period period = Period.ofYears(5);
		final LocalDate renewDate = LocalDate.now().plus(period);
		final BigDecimal renewCost = new BigDecimal(RENEWAL_COST);
		RenewalAccount renewal = createAccount(5);
		assertNotNull(renewal);
		assertEquals(period, renewal.getRenewPeriod());
		assertEquals(renewDate, renewal.getRenewDate());
		assertEquals(renewCost, renewal.getRenewCost());
		assertEquals(0, renewal.getPaymentHistory().size());
		assertEquals(0, renewal.getRenewCycleCount());
		assertTrue(renewal.canRenew());
	}

	@Test
	@Ignore
	public void testRenewals() {
		testHalfPayment_Renewal();
		testFullPayment_Renewal();
		testTwoSuccessiveRenewals();
		testPayment_withoutRenewing_shouldDoNothing();
	}

	private void testHalfPayment_Renewal() {
		final int years = 5;
		Period period = createPeriod(years);
		RenewalAccount account = createRenewalAccount(period);

		LocalDate expectedRenewDate = LocalDate.now().plus(period);

		assertTrue(account.canRenew());
		assertEquals(expectedRenewDate, account.getRenewDate());

		int halfCost = RENEWAL_COST / 2;
		Payment payment = createMockPayment(halfCost);
		BigDecimal balance = account.renew(payment);

		LocalDate expectedDateAfterRenew = expectedRenewDate.plus(period);

		assertEquals(new BigDecimal(halfCost), balance);
		assertEquals(expectedDateAfterRenew, account.getRenewDate());
		assertFalse(account.canRenew());
		assertEquals(1, account.getRenewCycleCount());
		assertEquals(1, account.getPaymentHistory().size());
	}

	private void testFullPayment_Renewal() {
		int years = 5;
		Period rp = createPeriod(years);
		LocalDate expectedDate = LocalDate.now().plus(rp.multipliedBy(2));
		BigDecimal expectedBalance = BigDecimal.ZERO;
		Payment renewPayment = createPayment(RENEWAL_COST);

		RenewalAccount account = createRenewalAccount(rp);
		BigDecimal balance = account.renew(renewPayment);

		assertEquals(expectedBalance, balance);
		assertEquals(expectedDate, account.getRenewDate());
		assertTrue(account.canRenew());
		assertEquals(1, account.getRenewCycleCount());
		assertEquals(1, account.getPaymentHistory().size());
	}

	private void testTwoSuccessiveRenewals() {
		int years = 2;
		Period rp = createPeriod(years);

		RenewalAccount account = createRenewalAccount(rp);

		Payment initialRenewPayment = createPayment(RENEWAL_COST);
		BigDecimal expectedInitialBalance = BigDecimal.ZERO;
		LocalDate expectedInitialDate = LocalDate.now().plus(rp.multipliedBy(2));

		// Initial Renewal
		BigDecimal balance = account.renew(initialRenewPayment);
		assertEquals(expectedInitialBalance, balance);
		assertEquals(expectedInitialDate, account.getRenewDate());
		assertTrue(account.canRenew());

		// Secondary Renew (downpayment)
		final int downpayment = RENEWAL_COST - 4_000;
		Payment secondaryRenewalPayment = createPayment(downpayment);
		BigDecimal expectedSecondaryBalance = new BigDecimal(RENEWAL_COST - downpayment);
		LocalDate expectedSecondaryDate = expectedInitialDate.plus(rp);

		balance = account.renew(secondaryRenewalPayment);
		assertEquals(expectedSecondaryBalance, balance);
		assertEquals(expectedSecondaryDate, account.getRenewDate());
		assertFalse(account.canRenew());

		// Add payment
		Payment followUpPayment = createPayment(2_000);
		BigDecimal balanceAfterPayment = balance.subtract(followUpPayment.getAmount());
		LocalDate dateAfterPayment = expectedSecondaryDate;

		account.addPayment(followUpPayment);
		assertFalse(account.canRenew());
		assertEquals(dateAfterPayment, account.getRenewDate());

		// Complete payment
		Payment nextPayment = createPayment(balanceAfterPayment.intValue());
		account.addPayment(nextPayment);

		assertTrue(account.canRenew());
		assertEquals(dateAfterPayment, account.getRenewDate());
		assertEquals(2, account.getRenewCycleCount());
		assertEquals(4, account.getPaymentHistory().size());
	}

	private void testPayment_withoutRenewing_shouldDoNothing() {
		int years = 1;
		Period period = createPeriod(years);
		RenewalAccount account = createAccount(years);

		LocalDate expectedRenewDate = LocalDate.now().plus(period);
		assertTrue(account.canRenew());
		assertEquals(expectedRenewDate, account.getRenewDate());

		Payment p = createPayment(5_000);
		account.addPayment(p);

		assertTrue(account.canRenew());
		assertEquals(expectedRenewDate, account.getRenewDate());
		assertEquals(0, account.getRenewCycleCount());
		assertEquals(0, account.getPaymentHistory().size());
	}

	// @Test
	public void testPayments() {
		testPayment_whileNotRenewingFirst_shouldDoNothing();
		testSecondaryPayment_resulting_NotFullyPaid();
		testSecondaryPayment_resulting_FullyPaid();
	}

	// @Test
	public void testPaymentRollback() {
		testPaymentRollbackOnZeroPayments();
		testPaymentRollbackOnInitialPayment();
		testPaymentRollbackOnRenewedAccount_shouldAlsoRollbackDate();
		testPaymentRollbackOnRenewedAccountWithExcessPayment_shouldNotRollbackDate();
		testMultipleRollbacks();
	}

	private void testPayment_whileNotRenewingFirst_shouldDoNothing() {
		final Period period = Period.ofYears(5);
		final LocalDate registrationDate = LocalDate.now();
		final BigDecimal renewCost = new BigDecimal(RENEWAL_COST);
		final RenewScheme renewScheme = new RenewScheme(period, renewCost);
		RenewalAccount renewal = new RenewalAccount(registrationDate, renewScheme);

		final Payment payment = new Payment("arnum", new BigDecimal(5000), LocalDate.now());
		renewal.addPayment(payment);
		assertTrue(renewal.canRenew());
		assertEquals(0, renewal.getRenewCycleCount());
		assertEquals(0, renewal.getPaymentHistory().size());
	}

	private void testSecondaryPayment_resulting_NotFullyPaid() {
		RenewalAccount renewal = createAccount(5);
		Payment initialPayment = createPayment(3000);
		renewal.addPayment(initialPayment);

		assertFalse(renewal.canRenew());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		Payment secondaryPayment = createPayment(2000);
		renewal.addPayment(secondaryPayment);

		assertFalse(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
	}

	private void testSecondaryPayment_resulting_FullyPaid() {
		RenewalAccount renewal = createAccount(5);
		final int halfCost = RENEWAL_COST / 2;
		Payment initialPayment = createPayment(halfCost);
		Payment secondaryPayment = createPayment(halfCost);

		renewal.addPayment(initialPayment);
		assertFalse(renewal.canRenew());

		renewal.addPayment(secondaryPayment);
		assertTrue(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
	}

	private void testPaymentOnFullyPaidAccount_willDoNothing() {
		RenewalAccount renewal = createAccount(5);
		Payment first = createPayment(5000);
		Payment second = createPayment(5000);
		Payment third = createPayment(5000);

		renewal.addPayment(first);

		assertFalse(renewal.canRenew());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		renewal.addPayment(second);

		assertTrue(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		renewal.addPayment(third);

		assertTrue(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
	}

	private void testPaymentRollbackOnZeroPayments() {
		RenewalAccount renewal = createAccount(5);
		BigDecimal balance = new BigDecimal(RENEWAL_COST);

		assertEquals(balance, renewal.rollbackPayment(balance));
		assertEquals(0, renewal.getPaymentHistory().size());
		assertEquals(0, renewal.getRenewCycleCount());
		assertTrue(renewal.canRenew());
	}

	private void testPaymentRollbackOnInitialPayment() {
		RenewalAccount renewal = createAccount(5);
		Payment payment = createPayment(4000);

		renewal.addPayment(payment);
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		BigDecimal balance = payment.getAmount().subtract(new BigDecimal(RENEWAL_COST));
		assertEquals(new BigDecimal(RENEWAL_COST), renewal.rollbackPayment(balance));
		assertEquals(0, renewal.getPaymentHistory().size());
		assertEquals(0, renewal.getRenewCycleCount());
	}

	private void testPaymentRollbackOnRenewedAccount_shouldAlsoRollbackDate() {
		RenewalAccount renewal = createAccount(5);
		Payment initialPayment = createPayment(RENEWAL_COST);
		renewal.addPayment(initialPayment);

		Payment renewPayment = createPayment(5000);
		BigDecimal balance = new BigDecimal(RENEWAL_COST).subtract(renewPayment.getAmount());

		BigDecimal balanceAfterRenew = renewal.renew(renewPayment);
		LocalDate expectedRenewalDate = LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(2));

		assertEquals(balance, balanceAfterRenew);
		assertEquals(expectedRenewalDate, renewal.getRenewDate());

		BigDecimal expectedBalanceAfterRollback = new BigDecimal(RENEWAL_COST);

		assertEquals(expectedBalanceAfterRollback, renewal.rollbackPayment(balance));
		assertEquals(LocalDate.now().plus(renewal.getRenewPeriod()), renewal.getRenewDate());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
	}

	private void testPaymentRollbackOnRenewedAccountWithExcessPayment_shouldNotRollbackDate() {
		RenewalAccount renewal = createAccount(5);
		Payment initialPayment = createPayment(RENEWAL_COST);
		renewal.addPayment(initialPayment);

		Payment renewPayment = createPayment(5000);
		BigDecimal balance = new BigDecimal(RENEWAL_COST).subtract(renewPayment.getAmount());

		BigDecimal balanceAfterRenew = renewal.renew(renewPayment);
		LocalDate expectedRenewalDate = LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(2));

		assertEquals(balance, balanceAfterRenew);
		assertEquals(expectedRenewalDate, renewal.getRenewDate());

		Payment excessPayment = createPayment(5000);
		renewal.addPayment(excessPayment);

		assertEquals(3, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());

		assertEquals(excessPayment.getAmount(), renewal.rollbackPayment(new BigDecimal(0)));
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());
		assertEquals(expectedRenewalDate, renewal.getRenewDate());
	}

	private void testMultipleRollbacks() {
		RenewalAccount renewal = createAccount(5);
		Payment halfPrice = createPayment(RENEWAL_COST / 2);
		int renewMultiplier = 1;
		int paymentCount = 0;
		int cycleCount = 1;

		renewal.addPayment(halfPrice);
		renewal.addPayment(halfPrice);
		paymentCount += 2;

		assertTrue(renewal.canRenew());
		assertEquals(paymentCount, renewal.getPaymentHistory().size());
		assertEquals(cycleCount, renewal.getRenewCycleCount());
		assertEquals(LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(renewMultiplier)),
				renewal.getRenewDate());

		renewal.renew(halfPrice);
		cycleCount++;
		paymentCount++;
		renewMultiplier++;

		assertFalse(renewal.canRenew());
		assertEquals(paymentCount, renewal.getPaymentHistory().size());
		assertEquals(cycleCount, renewal.getRenewCycleCount());
		assertEquals(LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(renewMultiplier)),
				renewal.getRenewDate());

		renewal.addPayment(halfPrice);
		paymentCount++;
		assertTrue(renewal.canRenew());
		assertEquals(paymentCount, renewal.getPaymentHistory().size());
		assertEquals(cycleCount, renewal.getRenewCycleCount());

		renewal.renew(halfPrice);
		cycleCount++;
		paymentCount++;
		renewMultiplier++;

		assertFalse(renewal.canRenew());
		assertEquals(paymentCount, renewal.getPaymentHistory().size());
		assertEquals(cycleCount, renewal.getRenewCycleCount());
		assertEquals(LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(renewMultiplier)),
				renewal.getRenewDate());

		BigDecimal dummy = new BigDecimal(0);
		renewal.rollbackPayment(dummy);
		paymentCount--;
		renewMultiplier--;
		cycleCount--;

		renewal.rollbackPayment(dummy);
		paymentCount--;

		renewal.rollbackPayment(dummy);
		paymentCount--;
		cycleCount--;
		renewMultiplier--;

		renewal.rollbackPayment(dummy);
		paymentCount--;

		renewal.rollbackPayment(dummy);
		paymentCount--;
		cycleCount--;
		renewMultiplier--;

		assertFalse(renewal.canRenew());
		assertEquals(paymentCount, renewal.getPaymentHistory().size());
		assertEquals(cycleCount, renewal.getRenewCycleCount());
		assertEquals(LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(renewMultiplier)),
				renewal.getRenewDate());

	}

	private RenewalAccount createAccount(int period) {
		return createAccount(period, LocalDate.now());
	}

	private RenewalAccount createAccount(int period, LocalDate date) {
		return createAccount(period, date, RENEWAL_COST);
	}

	private RenewalAccount createAccount(int year, LocalDate date, int cost) {
		return new RenewalAccount(date, new RenewScheme(Period.ofYears(year), new BigDecimal(cost)));
	}

	private Payment createPayment(int amount) {
		return new Payment("arnum", new BigDecimal(amount), LocalDate.now());
	}

	private Payment createMockPayment(int amount) {
		Payment p = Mockito.mock(Payment.class);
		when(p.getAmount()).thenReturn(new BigDecimal(amount));
		when(p.getReferenceNumber()).thenReturn("Mock Reference Number");
		when(p.getPaymentDate()).thenReturn(LocalDate.now());
		return p;
	}

	private RenewalAccount createRenewalAccount(Period period) {
		RenewScheme mockScheme = Mockito.mock(RenewScheme.class);
		when(mockScheme.getCost()).thenReturn(new BigDecimal(RENEWAL_COST));
		when(mockScheme.getPeriod()).thenReturn(period);
		return new RenewalAccount(LocalDate.now(), mockScheme);
	}

	private Period createPeriod(int years) {
		return Period.ofYears(years);
	}
}
