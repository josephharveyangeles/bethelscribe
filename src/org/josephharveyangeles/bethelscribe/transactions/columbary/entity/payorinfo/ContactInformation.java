package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo;

public class ContactInformation {

	private final String address;
	private final String contactNum;

	public ContactInformation(String address, String contactNum) {
		this.address = address;
		this.contactNum = contactNum;
	}

	public String getAddress() {
		return address;
	}

	public String getContactNumber() {
		return contactNum;
	}

}
