package test;
import static org.junit.Assert.*;

import org.junit.Test;

import main.Die;
import main.Rollable;
import main.SimDie;

/**
 * 
 * Comprehensive testing for the SimDie class.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class SimDieTest {

	/**
	 * Test to see if SimDie is a child of Rollable.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		boolean test = false;
		try {
			Rollable die = new SimDie(new int[] {1,2});
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	/**
	 * Test to see if SimDie is a child of Die
	 */
	@SuppressWarnings("unused")
	@Test
	public void testIsDie() {
		boolean test = false;
		try {
			Die die = new SimDie(new int[] {1,2});
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	/**
	 * Test to see if SimDie cannot be initialized with a null list of values.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testNotNull() {
		boolean test = true;
		try {
			Die die = new SimDie(null);
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	/**
	 * Test to see if SimDie cannot be initialized with an empty list of values.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testNotEmpty() {
		boolean test = true;
		try {
			Die die = new SimDie(new int[] {});
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	/**
	 * Test to see if SimDie cannot be initialized with an empty list of values.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testNotNegative() {
		boolean test = true;
		try {
			Die die = new SimDie(new int[] {-1});
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a negative value",test);
	}

	/**
	 * Test to see if SimDie can be rolled deterministically given a circular buffer of values.
	 */
	@Test
	public void testRoll() {
		SimDie die = new SimDie(new int[] {1});
		assertEquals("roll matched expected", 1, die.roll());
		assertEquals("roll matched expected", 1, die.roll());
		die = new SimDie(new int[] {1,2});
		assertEquals("roll matched expected", 2, die.roll());
		assertEquals("roll matched expected", 1, die.roll());
		assertEquals("roll matched expected", 2, die.roll());
		int[] vals = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 873240823, 5};
		die = new SimDie(vals);
		for (int i = 0; i < vals.length; i++) {
			assertEquals("roll matched expected", vals[i+1 >= vals.length ? 0 : i+1],die.roll());
		}
	}

	/**
	 * Test to see if SimDie can persist values between rolls.
	 */
	@Test
	public void testGetValue() {
		SimDie die = new SimDie(new int[] {1});
		assertEquals("roll matched expected", 1, die.getValue());
		assertEquals("roll matched expected", 1, die.getValue());
		die = new SimDie(new int[] {1,2});
		assertEquals("roll matched expected", 1, die.getValue());
		die.roll();
		assertEquals("roll matched expected", 2, die.getValue());
		assertEquals("roll matched expected", 2, die.getValue());
		int[] vals = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 873240823, 5};
		die = new SimDie(vals);
		for (int i = 0; i < vals.length; i++) {
			assertEquals(die.roll(),die.getValue());
		}
	}

}
