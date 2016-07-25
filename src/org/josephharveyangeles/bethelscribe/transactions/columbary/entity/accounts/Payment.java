package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {

	private final String RNumber;
	private final BigDecimal amount;
	private final LocalDate paymentDate;

	public Payment(String rNumber, BigDecimal amount) {
		this.RNumber = rNumber;
		this.amount = amount;
		this.paymentDate = LocalDate.now();
	}

	public Payment(String rNumber, BigDecimal amount, LocalDate paymentDate) {
		this.RNumber = rNumber;
		this.amount = amount;
		this.paymentDate = paymentDate;
	}

	public String getReferenceNumber() {
		return RNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

}
