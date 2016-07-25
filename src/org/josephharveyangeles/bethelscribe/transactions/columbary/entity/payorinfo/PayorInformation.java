package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo;

import org.josephharveyangeles.bethelscribe.common.person.PersonName;

public class PayorInformation {

	private String relationWithDeceased;
	private ContactInformation contactInfo;
	private PersonName name;

	public PayorInformation(PersonName pName, ContactInformation contact,
			String relation) {
		name = pName;
		contactInfo = contact;
		relationWithDeceased = relation;
	}

	public PersonName getPayorName() {
		return name;
	}

	public String getDeceasedRelation() {
		return relationWithDeceased;
	}

	public String getContactNumber() {
		return contactInfo.getContactNumber();
	}

	public String getAddress() {
		return contactInfo.getAddress();
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

	public void setDeceasedRelation(String relation) {
		relationWithDeceased = relation;
	}

	public void setContactNumber(String contactNum) {
		String address = contactInfo.getAddress();
		contactInfo = new ContactInformation(address, contactNum);
	}

	public void setAddress(String address) {
		String contactNum = contactInfo.getContactNumber();
		contactInfo = new ContactInformation(address, contactNum);
	}

	public void setContactInformation(String address, String contactNum) {
		contactInfo = new ContactInformation(address, contactNum);
	}

}
