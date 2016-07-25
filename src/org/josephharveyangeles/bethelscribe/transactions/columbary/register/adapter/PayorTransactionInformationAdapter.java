package org.josephharveyangeles.bethelscribe.transactions.columbary.register.adapter;

import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.Name;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.PayorTransactionInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.boundary.adapters.NameAdapter;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.PayorInformation;

class PayorTransactionInformationAdapter implements PayorTransactionInformation {

	private final Name name;
	private final String relation;
	private final String contactNumber;
	private final String address;

	PayorTransactionInformationAdapter(PayorInformation pInfo) {
		name = new NameAdapter(pInfo.getPayorName());
		relation = pInfo.getDeceasedRelation();
		contactNumber = pInfo.getContactNumber();
		address = pInfo.getAddress();
	}

	@Override
	public Name getName() {
		return name;
	}

	@Override
	public String getDeceasedRelation() {
		return relation;
	}

	@Override
	public String getContactNumber() {
		return contactNumber;
	}

	@Override
	public String getAddress() {
		return address;
	}

}
