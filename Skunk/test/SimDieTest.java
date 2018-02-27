import static org.junit.Assert.*;

import org.junit.Test;

public class SimDieTest {

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

	@Test
	public void testRoll() {
		SimDie die = new SimDie(new int[] {1});
		assertEquals("roll matched expected", 1, die.roll());
		assertEquals("roll matched expected", 1, die.roll());
		die = new SimDie(new int[] {1,2});
		assertEquals("roll matched expected", 2, die.roll());
		assertEquals("roll matched expected", 1, die.roll());
		assertEquals("roll matched expected", 2, die.roll());
		int[] vals = new int[] {-9999, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 873240823, -7489349, 5};
		die = new SimDie(vals);
		for (int i = 0; i < vals.length; i++) {
			assertEquals("roll matched expected", vals[i+1 >= vals.length ? 0 : i+1],die.roll());
		}
	}

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
		int[] vals = new int[] {-9999, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 873240823, -7489349, 5};
		die = new SimDie(vals);
		for (int i = 0; i < vals.length; i++) {
			assertEquals(die.roll(),die.getValue());
		}
	}

}
