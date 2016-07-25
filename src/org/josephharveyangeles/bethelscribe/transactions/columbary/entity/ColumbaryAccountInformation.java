package org.josephharveyangeles.bethelscribe.transactions.columbary.entity;

import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts.ColumbaryUnit;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.deceasedinfo.DeceasedInformation;
import org.josephharveyangeles.bethelscribe.transactions.columbary.entity.payorinfo.PayorInformation;

public class ColumbaryAccountInformation {
	private final DeceasedInformation deceasedInfo;
	private final PayorInformation payorInfo;
	private final ColumbaryUnit unitInfo;

	public ColumbaryAccountInformation(DeceasedInformation deceasedInfo, PayorInformation payorInfo,
			ColumbaryUnit unitInfo) {
		this.deceasedInfo = deceasedInfo;
		this.payorInfo = payorInfo;
		this.unitInfo = unitInfo;
	}

	public DeceasedInformation getDeceasedInformation() {
		return deceasedInfo;
	}

	public PayorInformation getPayorInformation() {
		return payorInfo;
	}

	public ColumbaryUnit getColumbaryUnit() {
		return unitInfo;
	}

}
