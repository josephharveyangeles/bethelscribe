package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class RenewalAccountTest {

	private static final int RENEWAL_COST = 10_000;

	@Test
	public void testInitialization() {
		testRegistrationRenewalInitialization();
		testRawDataRenewalInitialization();
	}

	@Test
	public void testRenewals() {
		testHalfPaymentRenewal();
		testFullPaymentRenewal();
		testTwoSuccessiveRenewals();
		testRenewalWhileStillHaveUnpaidCycle();
	}

	@Test
	public void testPayments() {
		testOverPayment();
		testPaymentAfterRenew();
		testPaymentOnFullyPaidShouldDoNothing();
		testPaymentWithoutRenewShouldDoNothing();
	}

	@Test
	public void testPaymentRollback() {
		testRollbackOnNoPayments();
		testRollbackOnInitialPayment();
		testRollbackOnRenewedAccountShouldRollbackDate();
		testRollbackOnPartialPaidRenewalShouldNotRollbackDate();
		testMultipleRollbacks();
	}

	private void testRegistrationRenewalInitialization() {
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
		assertTrue(renewal.getRenewCycles().isEmpty());
	}

	private void testRawDataRenewalInitialization() {
		RenewScheme renewScheme = Mockito.mock(RenewScheme.class);
		Mockito.when(renewScheme.getCost()).thenReturn(new BigDecimal(RENEWAL_COST));
		Mockito.when(renewScheme.getPeriod()).thenReturn(Period.ofYears(5));
		List<RenewCycle> cycles = new ArrayList<>();
		RenewalAccount renewal = new RenewalAccount(LocalDate.now(), renewScheme, cycles);
		assertNotNull(renewal);
		assertTrue(renewal.canRenew());
		assertEquals(cycles, renewal.getRenewCycles());
		assertEquals(RENEWAL_COST, renewal.getRenewCost().intValue());
		assertEquals(5, renewal.getRenewPeriod().getYears());
		assertEquals(LocalDate.now(), renewal.getRenewDate());
	}

	private void testRenewalWhileStillHaveUnpaidCycle() {
		RenewalAccount renewal = createAccount(5);
		Payment initialRenewalFee = createPayment(5000);
		assertTrue(renewal.canRenew());

		renewal.renew(initialRenewalFee);
		assertFalse(renewal.canRenew());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		BigDecimal balance = renewal.renew(initialRenewalFee);
		assertFalse(renewal.canRenew());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(RENEWAL_COST, balance.intValue());
	}

	private void testHalfPaymentRenewal() {
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

	private void testFullPaymentRenewal() {
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

	private void testPaymentWithoutRenewShouldDoNothing() {
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

	private void testPaymentAfterRenew() {
		RenewalAccount renewal = createAccount(5);
		assertTrue(renewal.canRenew());

		final int three_thousand = 3000;
		Payment renewPayment = createPayment(three_thousand);
		BigDecimal expectedRenewBalance = new BigDecimal(RENEWAL_COST - three_thousand);
		BigDecimal renewBalance = renewal.renew(renewPayment);

		assertFalse(renewal.canRenew());
		assertEquals(expectedRenewBalance, renewBalance);
		assertEquals(1, renewal.getPaymentHistory().size());

		Payment secondaryPayment = createPayment(three_thousand);
		BigDecimal expectedBalanceAfterPayment = new BigDecimal(RENEWAL_COST - (three_thousand * 2));
		BigDecimal balanceAfterPayment = renewal.addPayment(secondaryPayment);

		assertFalse(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(expectedBalanceAfterPayment, balanceAfterPayment);
		assertEquals(1, renewal.getRenewCycleCount());
	}

	private void testOverPayment() {
		RenewalAccount renewal = createAccount(5);
		Payment initialPayment = createPayment(9000);
		Payment overPayment = createPayment(2000);

		assertTrue(renewal.canRenew());

		renewal.renew(initialPayment);

		assertFalse(renewal.canRenew());
		renewal.addPayment(overPayment);
		assertTrue(renewal.canRenew());
	}

	private void testPaymentOnFullyPaidShouldDoNothing() {
		RenewalAccount renewal = createAccount(5);
		Payment first = createPayment(5000);
		Payment second = createPayment(5000);
		Payment third = createPayment(5000);

		assertTrue(renewal.canRenew());

		renewal.renew(first);
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

	private void testRollbackOnNoPayments() {
		RenewalAccount renewal = createAccount(5);
		BigDecimal balance = new BigDecimal(RENEWAL_COST);

		assertEquals(balance, renewal.rollbackPayment(balance));
		assertEquals(0, renewal.getPaymentHistory().size());
		assertEquals(0, renewal.getRenewCycleCount());
		assertTrue(renewal.canRenew());
	}

	private void testRollbackOnInitialPayment() {
		RenewalAccount renewal = createAccount(5);
		Payment payment = createPayment(4000);

		assertTrue(renewal.canRenew());

		BigDecimal balance = renewal.renew(payment);

		assertFalse(renewal.canRenew());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		BigDecimal balanceAfter = renewal.rollbackPayment(balance);

		assertTrue(renewal.canRenew());
		assertEquals(0, renewal.getPaymentHistory().size());
		assertEquals(0, renewal.getRenewCycleCount());
		assertEquals(0, balanceAfter.intValue());
	}

	private void testRollbackOnRenewedAccountShouldRollbackDate() {
		RenewalAccount renewal = createAccount(5);
		Payment initialPayment = createPayment(RENEWAL_COST);

		LocalDate expectedRenewDate = LocalDate.now().plus(renewal.getRenewPeriod());

		assertTrue(renewal.canRenew());
		assertEquals(expectedRenewDate, renewal.getRenewDate());

		LocalDate expectedRenewDateAfterRenewal = LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(2));
		renewal.renew(initialPayment);
		assertTrue(renewal.canRenew());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(expectedRenewDateAfterRenewal, renewal.getRenewDate());

		BigDecimal rollbackBalance = renewal.rollbackPayment(BigDecimal.ZERO);

		assertTrue(renewal.canRenew());
		assertTrue(renewal.getPaymentHistory().isEmpty());
		assertEquals(0, renewal.getRenewCycleCount());
		assertEquals(expectedRenewDate, renewal.getRenewDate());
		assertEquals(0, rollbackBalance.intValue());
	}

	private void testRollbackOnPartialPaidRenewalShouldNotRollbackDate() {
		RenewalAccount renewal = createAccount(5);
		Payment initialPayment = createPayment(RENEWAL_COST);

		renewal.renew(initialPayment);

		LocalDate expectedRenewDate = LocalDate.now().plus(renewal.getRenewPeriod().multipliedBy(2));
		assertEquals(expectedRenewDate, renewal.getRenewDate());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());

		Payment secondRenewalPayment = createPayment(2000);
		LocalDate expectedNewRenewDate = expectedRenewDate.plus(renewal.getRenewPeriod());
		renewal.renew(secondRenewalPayment);
		assertEquals(expectedNewRenewDate, renewal.getRenewDate());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());

		BigDecimal balance = renewal.addPayment(secondRenewalPayment);
		assertEquals(expectedNewRenewDate, renewal.getRenewDate());
		assertEquals(3, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());

		renewal.rollbackPayment(balance);
		assertEquals(expectedNewRenewDate, renewal.getRenewDate());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());
	}

	private void testMultipleRollbacks() {
		RenewalAccount renewal = createAccount(5);
		Payment halfPrice = createPayment(RENEWAL_COST / 2);

		assertTrue(renewal.canRenew());
		assertTrue(renewal.getPaymentHistory().isEmpty());
		assertTrue(renewal.getRenewCycles().isEmpty());
		assertEquals(LocalDate.now().plus(Period.ofYears(5)), renewal.getRenewDate());

		final LocalDate tenYearsFromNow = LocalDate.now().plus(Period.ofYears(10));

		renewal.renew(halfPrice);
		assertFalse(renewal.canRenew());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(tenYearsFromNow, renewal.getRenewDate());

		renewal.addPayment(halfPrice);
		assertTrue(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(tenYearsFromNow, renewal.getRenewDate());

		renewal.addPayment(halfPrice);
		assertTrue(renewal.canRenew());
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(tenYearsFromNow, renewal.getRenewDate());

		final LocalDate fifteenYearsFromNow = LocalDate.now().plus(Period.ofYears(15));
		renewal.renew(halfPrice);
		assertFalse(renewal.canRenew());
		assertEquals(3, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());
		assertEquals(fifteenYearsFromNow, renewal.getRenewDate());

		renewal.addPayment(halfPrice);
		assertTrue(renewal.canRenew());
		assertEquals(4, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());
		assertEquals(fifteenYearsFromNow, renewal.getRenewDate());

		BigDecimal balance = renewal.rollbackPayment(BigDecimal.ZERO);
		assertFalse(renewal.canRenew());
		assertEquals(halfPrice.getAmount().intValue(), balance.intValue());
		assertEquals(3, renewal.getPaymentHistory().size());
		assertEquals(2, renewal.getRenewCycleCount());
		assertEquals(fifteenYearsFromNow, renewal.getRenewDate());

		BigDecimal balanceAfterRenewRollback = renewal.rollbackPayment(balance);
		assertTrue(renewal.canRenew());
		assertEquals(BigDecimal.ZERO, balanceAfterRenewRollback);
		assertEquals(2, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(tenYearsFromNow, renewal.getRenewDate());

		BigDecimal balanceAfterRollbackOnLastCycle = renewal.rollbackPayment(BigDecimal.ZERO);
		assertFalse(renewal.canRenew());
		assertEquals(halfPrice.getAmount().intValue(), balanceAfterRollbackOnLastCycle.intValue());
		assertEquals(1, renewal.getPaymentHistory().size());
		assertEquals(1, renewal.getRenewCycleCount());
		assertEquals(tenYearsFromNow, renewal.getRenewDate());

		BigDecimal balanceAfterLastRollback = renewal.rollbackPayment(BigDecimal.ZERO);
		assertTrue(renewal.canRenew());
		assertEquals(BigDecimal.ZERO, balanceAfterLastRollback);
		assertTrue(renewal.getPaymentHistory().isEmpty());
		assertTrue(renewal.getRenewCycles().isEmpty());
		assertEquals(LocalDate.now().plus(Period.ofYears(5)), renewal.getRenewDate());
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
