package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary;

import java.time.LocalDate;

public interface DeceasedTransactionInformation {

	public Name getName();

	public LocalDate getDateOfBirth();

	public LocalDate getDateOfDeath();
}
