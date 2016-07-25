package org.josephharveyangeles.bethelscribe.transactions.columbary.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import org.josephharveyangeles.bethelscribe.common.person.PersonName;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryAccounts;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryUnit;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.Payment;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RegistrationAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewScheme;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewalAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.deceasedinfo.DeceasedInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.ContactInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.PayorInformation;
import org.junit.Test;

public class ColumbaryAccountTest {

	@Test
	public void testInitialization() {
		PersonName deceasedName = new PersonName("Joseph Harvey", "Catapia", "Angeles");
		LocalDate dob = LocalDate.of(1990, 12, 18);
		LocalDate dod = LocalDate.of(2015, 12, 18);
		DeceasedInformation deceasedInfo = new DeceasedInformation(deceasedName, dob, dod);

		PersonName payorName = new PersonName("Nestly Anne", "Malapitan", "Cruz");
		ContactInformation contact = new ContactInformation("212 Col.S.Cruz Street, Balite, Montalban, Rizal", "1234");
		String relation = "Spouse";

		PayorInformation payorInfo = new PayorInformation(payorName, contact, relation);
		RenewScheme renewScheme = new RenewScheme(Period.ofYears(5), new BigDecimal(5000));

		ColumbaryUnit unit = new ColumbaryUnit("Apartment", new BigDecimal(10_000), renewScheme);

		ColumbaryAccountInformation accountInfo = new ColumbaryAccountInformation(deceasedInfo, payorInfo, unit);

		final BigDecimal downpayment = new BigDecimal(5000);
		RegistrationAccount registration = new RegistrationAccount(LocalDate.now(),
				new Payment("ARNUMBER", downpayment));
		RenewalAccount renewal = new RenewalAccount(LocalDate.now(), renewScheme);

		ColumbaryAccounts accounts = new ColumbaryAccounts(registration, renewal);

		ColumbaryEntity account = new ColumbaryEntity(accountInfo, accounts, unit.getUnitCost().subtract(downpayment));

		assertNotNull(account);
		assertEquals(unit.getUnitCost(), account.getColumbaryCost());
		assertEquals(unit.getType(), account.getColumbaryType());
		assertEquals(deceasedInfo, account.getDeceasedInformation());
		assertEquals(payorInfo, account.getPayorInformation());
		assertEquals(new BigDecimal(5000), account.getBalance());
		assertFalse(account.canRenew());
		assertFalse(account.isRegistrationPaid());
		account.rollbackRegistrationPayment();
	}

	private ColumbaryEntity createAccount() {
		PersonName deceasedName = new PersonName("Joseph Harvey", "Catapia", "Angeles");
		LocalDate dob = LocalDate.of(1990, 12, 18);
		LocalDate dod = LocalDate.of(2015, 12, 18);
		DeceasedInformation deceasedInfo = new DeceasedInformation(deceasedName, dob, dod);

		PersonName payorName = new PersonName("Nestly Anne", "Malapitan", "Cruz");
		ContactInformation contact = new ContactInformation("212 Col.S.Cruz Street, Balite, Montalban, Rizal", "1234");
		String relation = "Spouse";

		PayorInformation payorInfo = new PayorInformation(payorName, contact, relation);
		RenewScheme renewScheme = new RenewScheme(Period.ofYears(5), new BigDecimal(5000));

		ColumbaryUnit unit = new ColumbaryUnit("Apartment", new BigDecimal(10_000), renewScheme);

		ColumbaryAccountInformation accountInfo = new ColumbaryAccountInformation(deceasedInfo, payorInfo, unit);

		final BigDecimal downpayment = new BigDecimal(5000);
		RegistrationAccount registration = new RegistrationAccount(LocalDate.now(),
				new Payment("ARNUMBER", downpayment));
		RenewalAccount renewal = new RenewalAccount(LocalDate.now(), renewScheme);

		ColumbaryAccounts accounts = new ColumbaryAccounts(registration, renewal);

		return new ColumbaryEntity(accountInfo, accounts, unit.getUnitCost().subtract(downpayment));
	}

}
