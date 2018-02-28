import static org.junit.Assert.*;

import org.junit.Test;

public class StandardDiceTest {
	
	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		boolean test = false;
		try {
			Rollable dice = new StandardDice();
			test = true;
		} catch (Exception e) {}
		assertTrue("Dice was able to be assigned to a Rollable alias",test);
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testIsDice() {
		boolean test = false;
		try {
			Dice dice = new StandardDice();
			test = true;
		} catch (Exception e) {}
		assertTrue("Dice was able to be assigned to a Dice alias",test);
	}	
	
	@SuppressWarnings("unused")
	@Test
	public void testNotNull() {
		boolean test = false;
		try {
			Dice dice = new StandardDice();
			test = true;
		} catch (Exception e) {}
		assertTrue("Dice threw exception upon receiving null dice array",test);
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testNotEmpty() {
		boolean test = false;
		try {
			Dice dice = new StandardDice();
			test = true;
		} catch (Exception e) {}
		assertTrue("Dice threw exception upon receiving empty dice array",test);
	}
	
	@Test
	public void testRoll() {
		final int ROLLS = 100000;
		Dice dice = new StandardDice();
		for (int i = 0; i < ROLLS; i++) {
			int test = dice.roll();
			assertTrue("result of roll matches expected", 2 <= test && test <= 12);
		}
	}
	
	@Test
	public void testGetValue() {
		final int ROLLS = 100000;
		Dice dice = new StandardDice();
		for (int i = 0; i < ROLLS; i++) {
			int test = dice.roll();
			assertEquals("result of getValue matches expected after roll",test,dice.getValue());
			assertEquals("result of getValue matches expected after roll",test,dice.getValue());
			assertEquals("result of getValue matches expected after roll",test,dice.getValue());
		}
	}

}
