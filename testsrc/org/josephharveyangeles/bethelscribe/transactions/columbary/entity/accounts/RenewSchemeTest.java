package org.josephharveyangeles.bethelscribe.transactions.columbary.entity.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.Period;

import org.junit.Test;

public class RenewSchemeTest {

	@Test
	public void testInitialization() {
		RenewScheme scheme = new RenewScheme(Period.ofYears(5), new BigDecimal(100));
		assertNotNull(scheme);
	}

	@Test
	public void testSetters() {
		final int interval = 5;
		final int cost = 3000;
		final Period period = Period.ofYears(interval);

		RenewScheme scheme = new RenewScheme(period, new BigDecimal(cost));
		assertEquals(period, scheme.getPeriod());
		assertEquals(new BigDecimal(cost), scheme.getCost());
	}

}
