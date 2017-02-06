package org.josephharveyangeles.bethelscribe.transactions.columbary.register;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.List;

import org.josephharveyangeles.bethelscribe.core.Transaction;
import org.josephharveyangeles.bethelscribe.core.TransactionException;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.Name;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PaymentTransaction;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.RenewChain;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.request.RegisterColumbaryTransactionRequest;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationResponseAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationRenewalResponseAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RegisterColumbaryAccountTransactionTest {

	private static final LocalDate JUNE_12_2012 = LocalDate.of(2012, Month.JUNE, 12);

	private RegisterTransactionListenerStub resultStub;

	private RegisterColumbaryTransactionRequest regReq;

	private UnitTransactionInformation uinfo;

	private Transaction<RegisterColumbaryTransactionRequest> registration;

	@Before
	public void setUp() {
		resultStub = new RegisterTransactionListenerStub();
		uinfo = Mockito.mock(UnitTransactionInformation.class);
		regReq = Mockito.mock(RegisterColumbaryTransactionRequest.class);
		DeceasedTransactionInformation dinfo = createMockDeceasedInformation();
		PayorTransactionInformation pinfo = createMockPayorInformation();

		when(regReq.getDeceasedInformation()).thenReturn(dinfo);
		when(regReq.getPayorInformation()).thenReturn(pinfo);
		when(regReq.getUnitInformation()).thenReturn(uinfo);

		registration = new RegisterColumbaryAccountTransaction(resultStub);
	}

	@Test
	public void testRegistrationOnCurrentDate_withInitialPayment_and5YearRenewPeriod() throws TransactionException {
		final BigDecimal unitCost = new BigDecimal(5000);
		final BigDecimal renewCost = new BigDecimal(10_000);
		final int renewInterval = 5;
		final BigDecimal downPayment = unitCost.divide(new BigDecimal(2));
		final LocalDate date = LocalDate.now();

		mockUnitInformation(unitCost, renewCost, renewInterval);
		mockPayment(downPayment, date);

		registration.execute(regReq);

		assertTrue(resultStub.isSet());

		assertPayorInformation();
		assertDeceasedInformation();
		assertBalance(unitCost, downPayment);
		assertRegistration(date, downPayment);
		assertRenewal(date, renewCost, renewInterval);
	}

	@Test
	public void testRegistrationOnDate_withFullPayment_and7YearRenewPeriod() throws TransactionException {
		final BigDecimal unitCost = new BigDecimal(5000);
		final BigDecimal renewCost = new BigDecimal(10_000);
		final int renewInterval = 7;
		final BigDecimal downPayment = new BigDecimal(2000);
		final LocalDate date = LocalDate.of(1998, Month.DECEMBER, 18);

		mockUnitInformation(unitCost, renewCost, renewInterval);
		mockPayment(downPayment, date);

		registration.execute(regReq);

		assertTrue(resultStub.isSet());

		assertPayorInformation();
		assertDeceasedInformation();
		assertBalance(unitCost, downPayment);
		assertRegistration(date, downPayment);
		assertRenewal(date, renewCost, renewInterval);
	}

	private void assertDeceasedInformation() {
		DeceasedTransactionInformation dinfo = resultStub.getDeceasedInformation();
		Name deceasedName = dinfo.getName();
		assertEquals("Juan", deceasedName.getFirstName());
		assertEquals("M", deceasedName.getMiddleName());
		assertEquals("Dela Cruz", deceasedName.getLastName());
		assertEquals(JUNE_12_2012, dinfo.getDateOfBirth());
		assertEquals(LocalDate.now(), dinfo.getDateOfDeath());
	}

	private void assertPayorInformation() {
		PayorTransactionInformation pinfo = resultStub.getPayorInformation();
		Name payorName = pinfo.getName();
		assertEquals("John", payorName.getFirstName());
		assertEquals("Smith", payorName.getMiddleName());
		assertEquals("Doe", payorName.getLastName());
		assertEquals("12345", pinfo.getContactNumber());
		assertEquals("22B Baker Street", pinfo.getAddress());
		assertEquals("Best Friend", pinfo.getDeceasedRelation());
	}

	private void assertRegistration(LocalDate registrationDate, BigDecimal downPayment) {
		RegistrationResponseAccount registration = resultStub.getRegistrationAccount();
		final List<PaymentTransaction> payments = registration.getPayments();
		PaymentTransaction payment = payments.get(0);

		assertEquals(registrationDate, registration.getRegistrationDate());
		assertEquals(1, payments.size());
		assertEquals("testARNumber", payment.getReferenceNumber());
		assertEquals(registrationDate, payment.getDate());
		assertEquals(downPayment, payment.getAmount());
	}

	private void assertRenewal(LocalDate regDate, BigDecimal renewCost, int interval) {
		RegistrationRenewalResponseAccount renewal = resultStub.getRenewalAccount();
		List<RenewChain> chains = renewal.getRenewChains();

		assertTrue(chains.isEmpty());
		assertEquals(renewCost, renewal.getRenewCost());
		assertEquals(interval, renewal.getRenewPeriod());
		assertEquals(regDate.plus(Period.ofYears(interval)), renewal.getRenewalDate());
	}

	private void assertBalance(BigDecimal unitCost, BigDecimal downPayment) {
		BigDecimal expected = unitCost.subtract(downPayment);
		assertEquals(expected, resultStub.getBalance());
	}

	private void mockUnitInformation(BigDecimal cost, BigDecimal renewCost, int interval) {
		when(uinfo.getType()).thenReturn("TestType");
		when(uinfo.getUnitCost()).thenReturn(cost);
		when(uinfo.getRenewCost()).thenReturn(renewCost);
		when(uinfo.getRenewInterval()).thenReturn(interval);
	}

	private void mockPayment(BigDecimal downpayment, LocalDate paymentDate) {
		when(regReq.getARNumber()).thenReturn("testARNumber");
		when(regReq.getDownpayment()).thenReturn(downpayment);
		when(regReq.getDate()).thenReturn(paymentDate);
	}

	private DeceasedTransactionInformation createMockDeceasedInformation() {
		DeceasedTransactionInformation dinfo = Mockito.mock(DeceasedTransactionInformation.class);
		final Name deceasedName = createMockName("Juan", "M", "Dela Cruz");
		when(dinfo.getName()).thenReturn(deceasedName);
		when(dinfo.getDateOfBirth()).thenReturn(JUNE_12_2012);
		when(dinfo.getDateOfDeath()).thenReturn(LocalDate.now());
		return dinfo;
	}

	private PayorTransactionInformation createMockPayorInformation() {
		PayorTransactionInformation pinfo = Mockito.mock(PayorTransactionInformation.class);
		Name payorName = createMockName("John", "Smith", "Doe");
		when(pinfo.getName()).thenReturn(payorName);
		when(pinfo.getContactNumber()).thenReturn("12345");
		when(pinfo.getAddress()).thenReturn("22B Baker Street");
		when(pinfo.getDeceasedRelation()).thenReturn("Best Friend");
		return pinfo;
	}

	private Name createMockName(String firstName, String middleName, String lastName) {
		Name name = Mockito.mock(Name.class);
		when(name.getFirstName()).thenReturn(firstName);
		when(name.getMiddleName()).thenReturn(middleName);
		when(name.getLastName()).thenReturn(lastName);
		when(name.getSuffix()).thenReturn("");
		return name;
	}

}
