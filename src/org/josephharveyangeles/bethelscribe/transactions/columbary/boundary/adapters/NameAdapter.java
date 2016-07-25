package org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters;

import org.josephharveyangeles.bethelscribe.common.person.PersonName;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.Name;

public class NameAdapter implements Name {

	private final String firstName;
	private final String middleName;
	private final String lastName;
	private final String suffix;

	public NameAdapter(PersonName name) {
		firstName = name.getFirstName();
		middleName = name.getMiddleName();
		lastName = name.getLastName();
		suffix = name.getSuffix();
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getMiddleName() {
		return middleName;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

}
