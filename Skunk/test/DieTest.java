import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * Comprehensive testing for the Die class
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class DieTest {

	/**
	 * Test to see if Die is a child of Rollable.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		boolean test = false;
		try {
			Rollable die = (Die)(new SimDie(new int[] {1,2}));
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
		test = false;
		try {
			Rollable die = (Die)(new RandomDie(new int[] {1,2}));
			test = true;
		} catch (Exception e) {}
		assertTrue("Die object could be assigned to Rollable alias",test);
	}

	/**
	 * Test to see if Die cannot be initialized with a null list of values. 
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
		test = true;
		try {
			Die die = new RandomDie(null);
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

	/**
	 * Test to see if Die cannot be initialized with an empty list of values.
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
		test = true;
		try {
			Die die = new RandomDie(new int[] {});
			test = false;
		} catch (Exception e) {}
		assertTrue("Die threw exception upon receiving a null value",test);
	}

}
