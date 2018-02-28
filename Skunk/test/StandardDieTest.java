import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * Comprehensive testing for the StandardDie class.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class StandardDieTest {

	/**
	 * Test to see if StandardDie is a child of Rollable.
	 */
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

	/**
	 * Test to see if StandardDie is a child of Die.
	 */
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

	/**
	 * Test to see if StandardDie cannot be initialized with a null probability distribution (cannot be fabricated here).
	 */
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

	/**
	 * Test to see if StandardDie cannot be initialized with an empty probability distribution (cannot be fabricated here).
	 */
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

	/**
	 * Test to see if StandardDie can be rolled for a random value in an expected range.
	 */
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

	/**
	 * Test to see if StandardDie persists values between rolls.
	 */
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
