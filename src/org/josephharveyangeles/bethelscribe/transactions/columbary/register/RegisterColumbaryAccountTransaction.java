package org.josephharveyangeles.bethelscribe.transactions.columbary.register;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.josephharveyangeles.bethelscribe.common.person.PersonName;
import org.josephharveyangeles.bethelscribe.core.Transaction;
import org.josephharveyangeles.bethelscribe.core.TransactionException;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.Name;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.UnitTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.request.RegisterColumbaryTransactionRequest;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.response.RegistrationTransactionResponse;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.ColumbaryAccountInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.ColumbaryEntity;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryAccounts;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryUnit;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.Payment;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RegistrationAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewScheme;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewalAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.deceasedinfo.DeceasedInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.ContactInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.PayorInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter.RegistrationTransactionResponseAdapter;

public class RegisterColumbaryAccountTransaction implements Transaction<RegisterColumbaryTransactionRequest> {

	private final List<RegisterColumbaryTransactionListener> transactionListeners;

	public RegisterColumbaryAccountTransaction(RegisterColumbaryTransactionListener listener) {
		transactionListeners = new ArrayList<>();
		transactionListeners.add(listener);
	}

	public void addRegistrationTransactionListener(RegisterColumbaryTransactionListener listener) {
		transactionListeners.add(listener);
	}

	@Override
	public final void execute(RegisterColumbaryTransactionRequest requestData) throws TransactionException {
		ColumbaryEntity columbary = createColumbaryEntity(requestData);
		notifyListeners(new RegistrationTransactionResponseAdapter(columbary));
	}

	private void notifyListeners(RegistrationTransactionResponse registrationResult) {
		transactionListeners.forEach(listener -> listener.registered(registrationResult));
	}

	private ColumbaryEntity createColumbaryEntity(RegisterColumbaryTransactionRequest requestData) {
		ColumbaryAccountInformation accountInfo = createAccountInformation(requestData);
		Payment payment = createRegistrationPayment(requestData);
		ColumbaryAccounts accounts = createColumbaryAccounts(payment, accountInfo);
		BigDecimal balance = calculateBalance(accountInfo.getColumbaryUnit(), payment);

		return new ColumbaryEntity(accountInfo, accounts, balance);
	}

	private ColumbaryAccountInformation createAccountInformation(RegisterColumbaryTransactionRequest registrationData) {
		DeceasedInformation deceasedInformation = createDeceasedInformation(registrationData.getDeceasedInformation());
		PayorInformation payorInformation = createPayorInformation(registrationData.getPayorInformation());
		ColumbaryUnit unit = createColumbaryUnit(registrationData.getUnitInformation());
		return new ColumbaryAccountInformation(deceasedInformation, payorInformation, unit);
	}

	private Payment createRegistrationPayment(RegisterColumbaryTransactionRequest registrationData) {
		String refNumber = registrationData.getARNumber();
		BigDecimal amount = registrationData.getDownpayment();
		LocalDate date = registrationData.getDate();
		return new Payment(refNumber, amount, date);
	}

	private DeceasedInformation createDeceasedInformation(DeceasedTransactionInformation transOb) {
		PersonName name = createPersonName(transOb.getName());
		return new DeceasedInformation(name, transOb.getDateOfBirth(), transOb.getDateOfDeath());
	}

	private PayorInformation createPayorInformation(PayorTransactionInformation transOb) {
		PersonName name = createPersonName(transOb.getName());
		ContactInformation contactInfo = new ContactInformation(transOb.getAddress(), transOb.getContactNumber());
		return new PayorInformation(name, contactInfo, transOb.getDeceasedRelation());
	}

	private PersonName createPersonName(Name name) {
		String firstName = name.getFirstName();
		String lastName = name.getLastName();
		String middleName = name.getMiddleName();
		String suffix = name.getSuffix();
		return new PersonName(firstName, middleName, lastName, suffix);
	}

	private ColumbaryUnit createColumbaryUnit(UnitTransactionInformation transOb) {
		String type = transOb.getType();
		BigDecimal cost = transOb.getUnitCost();
		Period period = Period.ofYears(transOb.getRenewInterval());
		BigDecimal renewCost = transOb.getRenewCost();
		return new ColumbaryUnit(type, cost, new RenewScheme(period, renewCost));
	}

	private ColumbaryAccounts createColumbaryAccounts(Payment payment, ColumbaryAccountInformation accountInfo) {
		ColumbaryUnit unit = accountInfo.getColumbaryUnit();
		return createColumbaryAccounts(payment, unit.getRenewScheme());
	}

	private ColumbaryAccounts createColumbaryAccounts(Payment payment, RenewScheme scheme) {
		LocalDate date = payment.getPaymentDate();
		RegistrationAccount registration = new RegistrationAccount(date, payment);
		RenewalAccount renewal = new RenewalAccount(date, scheme);
		return new ColumbaryAccounts(registration, renewal);
	}

	private BigDecimal calculateBalance(ColumbaryUnit unit, Payment payment) {
		return unit.getUnitCost().subtract(payment.getAmount());
	}

}
