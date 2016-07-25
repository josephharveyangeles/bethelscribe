package org.josephharveyangeles.bethelscribe.transactions.columbary.entity;

import java.math.BigDecimal;

import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryAccounts;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryUnit;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.Payment;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RegistrationAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.RenewalAccount;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.deceasedinfo.DeceasedInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.PayorInformation;

public class ColumbaryEntity {

	private final DeceasedInformation deceasedInfo;
	private final PayorInformation payorInfo;
	private final ColumbaryUnit columbaryUnit;
	private final RegistrationAccount registrationAccount;
	private final RenewalAccount renewalAccount;
	private BigDecimal balance;

	public ColumbaryEntity(ColumbaryAccountInformation accountDetails, ColumbaryAccounts accounts, BigDecimal bal) {
		deceasedInfo = accountDetails.getDeceasedInformation();
		payorInfo = accountDetails.getPayorInformation();
		columbaryUnit = accountDetails.getColumbaryUnit();
		registrationAccount = accounts.getRegistration();
		renewalAccount = accounts.getRenewal();
		balance = bal;
	}

	public PayorInformation getPayorInformation() {
		return payorInfo;
	}

	public DeceasedInformation getDeceasedInformation() {
		return deceasedInfo;
	}

	public ColumbaryUnit getColumbaryInformation() {
		return columbaryUnit;
	}

	public String getColumbaryType() {
		return columbaryUnit.getType();
	}

	public BigDecimal getColumbaryCost() {
		return columbaryUnit.getUnitCost();
	}

	public boolean isOverPayment(Payment payment) {
		return isNegative(balance.subtract(payment.getAmount()));
	}

	public void addRegistrationPayment(Payment payment) {
		if (!isRegistrationPaid()) {
			updateBalance(balance.subtract(payment.getAmount()));
			registrationAccount.addPayment(payment);
		}
	}

	public void addRenewalPayment(Payment payment) {
		if (isRegistrationPaid()) {
			updateBalance(balance.subtract(payment.getAmount()));
			renewalAccount.addPayment(payment);
		}
	}

	public boolean canRenew() {
		return isRegistrationPaid() && renewalAccount.canRenew();
	}

	public boolean isRegistrationPaid() {
		return registrationAccount.isPaid(columbaryUnit.getUnitCost());
	}

	public void renew(Payment payment) {
		if (canRenew()) {
			updateBalance(renewalAccount.renew(payment));
		}
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void rollbackRegistrationPayment() {
		if (renewalAccount.getRenewCycleCount() > 0) {
			return;
		}
		updateBalance(registrationAccount.rollbackPayment(balance));
	}

	public RegistrationAccount getRegistrationAccount() {
		return registrationAccount;
	}

	public RenewalAccount getRenewalAccount() {
		return renewalAccount;
	}

	public void rollbackRenewPayment() {
		updateBalance(renewalAccount.rollbackPayment(balance));
	}

	private void updateBalance(BigDecimal newBalance) {
		balance = newBalance;
	}

	private boolean isNegative(BigDecimal value) {
		return value.signum() == -1;
	}

}
