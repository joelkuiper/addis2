package org.drugis.common.hibernate;

import static org.junit.Assert.*;

import org.drugis.common.hibernate.PluralizedNamingStrategy;
import org.junit.Test;

public class PluralizedNamingStrategyTest {

	@Test
	public void test() {
		PluralizedNamingStrategy pns = new PluralizedNamingStrategy();
		assertEquals("users", pns.classToTableName("User"));
		assertEquals("ponies", pns.classToTableName("Pony"));
		assertEquals("pony_riders", pns.classToTableName("PonyRider"));
	}

}
