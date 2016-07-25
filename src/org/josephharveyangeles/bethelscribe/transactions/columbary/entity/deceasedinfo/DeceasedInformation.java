package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.deceasedinfo;

import java.time.LocalDate;

import org.josephharveyangeles.bethelscribe.common.person.PersonName;

public class DeceasedInformation {

	private LocalDate dateOfBirth;
	private LocalDate dateOfDeath;
	private PersonName name;

	public DeceasedInformation(PersonName dName, LocalDate dob, LocalDate dod) {
		name = dName;
		dateOfBirth = dob;
		dateOfDeath = dod;
	}

	public PersonName getName() {
		return name;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public LocalDate getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfBirth(LocalDate date) {
		dateOfBirth = date;
	}

	public void setDateOfDeath(LocalDate date) {
		dateOfDeath = date;
	}

	public void setFirstName(String firstName) {
		String lastName = name.getLastName();
		String middleName = name.getMiddleName();
		String suffix = name.getSuffix();
		name = new PersonName(firstName, middleName, lastName, suffix);
	}

	public void setMiddleNameName(String middleName) {
		String firstName = name.getFirstName();
		String lastName = name.getLastName();
		String suffix = name.getSuffix();
		name = new PersonName(firstName, middleName, lastName, suffix);
	}

	public void setLastName(String lastName) {
		String firstName = name.getFirstName();
		String middleName = name.getMiddleName();
		String suffix = name.getSuffix();
		name = new PersonName(firstName, middleName, lastName, suffix);
	}

	public void setSuffixName(String suffix) {
		String firstName = name.getFirstName();
		String lastName = name.getLastName();
		String middleName = name.getMiddleName();
		name = new PersonName(firstName, middleName, lastName, suffix);
	}

	public void setName(String first, String middle, String last) {
		name = new PersonName(first, middle, last);
	}

	public void setName(String first, String middle, String last, String suffix) {
		name = new PersonName(first, middle, last, suffix);
	}

}
