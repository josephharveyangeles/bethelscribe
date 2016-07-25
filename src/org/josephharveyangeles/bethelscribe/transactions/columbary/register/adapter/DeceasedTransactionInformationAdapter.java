package org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter;

import java.time.LocalDate;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.DeceasedTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.Name;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters.NameAdapter;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.deceasedinfo.DeceasedInformation;

class DeceasedTransactionInformationAdapter implements DeceasedTransactionInformation {

	private final LocalDate dateOfBirth;
	private final LocalDate dateOfDeath;
	private final Name name;

	DeceasedTransactionInformationAdapter(DeceasedInformation dInfo) {
		dateOfBirth = dInfo.getDateOfBirth();
		dateOfDeath = dInfo.getDateOfDeath();
		name = new NameAdapter(dInfo.getName());
	}

	@Override
	public Name getName() {
		return name;
	}

	@Override
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	@Override
	public LocalDate getDateOfDeath() {
		return dateOfDeath;
	}

}
