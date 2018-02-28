import static org.junit.Assert.*;

import org.junit.Test;

public class StandardDieTest {

	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		boolean test = false;
		try {
			Rollable die = new StandardDie();
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	@SuppressWarnings("unused")
	@Test
	public void testIsDie() {
		boolean test = false;
		try {
			Die die = new StandardDie();
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	@SuppressWarnings("unused")
	@Test
	public void testNotNull() {
		boolean test = true;
		try {
			Die die = new StandardDie();
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	@SuppressWarnings("unused")
	@Test
	public void testNotEmpty() {
		boolean test = true;
		try {
			Die die = new StandardDie();
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	@Test
	public void testRoll() {
		final int ROLLS = 100000; 
		StandardDie die = new StandardDie();
		boolean[] hit = new boolean[] {false,false,false,false,false,false,false,false,false};
		int count = 0;
		for (int i = 0; i < ROLLS; i++) {
			int test = die.roll();
			if (!hit[test]) {
				hit[test] = true;
				count++;
			}
			assertTrue("roll matched expected", 1 <= test && test <= 6);
		}
		assertTrue("roll matched expected", 2 <= count && count <= 6);
	}

	@Test
	public void testGetValue() {
		StandardDie die = new StandardDie();
		int test = die.roll();
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		test = die.roll();
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
		assertEquals("roll matched expected", test, die.getValue());
	}

}
