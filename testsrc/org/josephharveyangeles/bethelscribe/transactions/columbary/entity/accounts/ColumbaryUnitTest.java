package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.Period;

import org.junit.Test;

public class ColumbaryUnitTest {

	@Test
	public void testColumbaryUnitInitialization() {
		ColumbaryUnit unit = new ColumbaryUnit("Type", new BigDecimal("2000.00"),
				new RenewScheme(Period.ofYears(5), new BigDecimal(1000)));
		assertNotNull(unit);
	}

	@Test
	public void testColumbaryUnitGetters() {
		final BigDecimal cost = new BigDecimal("2000.00");
		final String type = "UnitType";
		final Period period = Period.ofYears(5);
		final BigDecimal rcost = new BigDecimal(2000);
		ColumbaryUnit unit = new ColumbaryUnit(type, cost, new RenewScheme(period, rcost));
		assertEquals(cost, unit.getUnitCost());
		assertEquals(type, unit.getType());
		assertEquals(period, unit.getRenewalInterval());
		assertEquals(rcost, unit.getRenewCost());
	}

	@Test
	public void testColumbaryUnitGettersNotEqual() {
		final BigDecimal cost = new BigDecimal("2000.00");
		final String type = "UnitType";
		ColumbaryUnit unit = new ColumbaryUnit("Type", new BigDecimal("1000.00"),
				new RenewScheme(Period.ofYears(4), new BigDecimal(10000)));
		assertNotEquals(cost, unit.getUnitCost());
		assertNotEquals(type, unit.getType());
		assertNotEquals(5, unit.getRenewalInterval().getYears());
		assertNotEquals(cost, unit.getRenewCost());
	}

}
