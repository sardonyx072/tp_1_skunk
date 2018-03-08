package test;
import static org.junit.Assert.*;

import org.junit.Test;

import main.Die;
import main.RandomDie;
import main.Rollable;

/**
 * 
 * Comprehensive testing for the RandomDie class.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class RandomDieTest {

	/**
	 * Test to see if RandomDie is a child of Rollable.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		boolean test = false;
		try {
			Rollable die = new RandomDie(new int[] {1,2});
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	/**
	 * Test to see if RandomDie is a child of Die
	 */
	@SuppressWarnings("unused")
	@Test
	public void testIsDie() {
		boolean test = false;
		try {
			Die die = new RandomDie(new int[] {1,2});
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	/**
	 * Test to see if RandomDie can be initialized with a null probability distribution list.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testNotNull() {
		boolean test = true;
		try {
			Die die = new RandomDie(null);
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	/**
	 * Test to see if RandomDie can be initialized with an empty probability distribution list.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testNotEmpty() {
		boolean test = true;
		try {
			Die die = new RandomDie(new int[] {});
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	/**
	 * Test to see if RandomDie can roll random values.
	 */
	@Test
	public void testRoll() {
		final int ROLLS = 100000; 
		RandomDie die = new RandomDie(new int[] {1});
		for (int i = 0; i < ROLLS; i++) {
			assertEquals("roll matched expected", 0, die.roll());
		}
		die = new RandomDie(new int[] {0,0,0,1,0,0,0,0,0,0});
		for (int i = 0; i < ROLLS; i++) {
			assertEquals("roll matched expected", 3, die.roll());
		}
		die = new RandomDie(new int[] {0,0,999,0,0,0,0,0,0,0});
		for (int i = 0; i < ROLLS; i++) {
			assertEquals("roll matched expected", 2, die.roll());
		}
		die = new RandomDie(new int[] {0,0,1,2,3,4,5,6,5,4,3,2,1,0,0,0,0,0,0,0,0,0,0,0,0});
		boolean[] hit = new boolean[] {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
		int count = 0;
		for (int i = 0; i < ROLLS; i++) {
			int test = die.roll();
			if (!hit[test]) {
				hit[test] = true;
				count++;
			}
			assertTrue("roll matched expected", 2 <= test && test <= 12);
		}
		assertTrue("roll matched expected", 2 <= count && count <= 11);
	}

	/**
	 * Test to see if RandomDie can presist the same value between rolling it.
	 */
	@Test
	public void testGetValue() {
		RandomDie die = new RandomDie(new int[] {1});
		assertEquals("roll matched expected", 0, die.getValue());
		assertEquals("roll matched expected", 0, die.getValue());
		assertEquals("roll matched expected", 0, die.getValue());
		die.roll();
		assertEquals("roll matched expected", 0, die.getValue());
		assertEquals("roll matched expected", 0, die.getValue());
		assertEquals("roll matched expected", 0, die.getValue());
		die = new RandomDie(new int[] {0, 0, 1, 0, 0, 0});
		assertEquals("roll matched expected", 2, die.getValue());
		assertEquals("roll matched expected", 2, die.getValue());
		assertEquals("roll matched expected", 2, die.getValue());
		die.roll();
		assertEquals("roll matched expected", 2, die.getValue());
		assertEquals("roll matched expected", 2, die.getValue());
		assertEquals("roll matched expected", 2, die.getValue());
	}

}
