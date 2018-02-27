import static org.junit.Assert.*;

import org.junit.Test;

public class StandardDiceTest {
	
	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		int[] vals = new int[] {0,1,2,3,4,5,6,7,8,9,10,100,1000,512837697,9999,555,33,8,2,6,9,1,5,2,3,1,2,1,7,0,7,-108475,6,-1,1,0,8};
		try {
			Rollable dice = new StandardDice();
			assertTrue("Dice was able to be assigned to a Rollable alias",true);
		} catch (Exception e) {
			fail("Dice was not able to be assigned to a Rollable alias");
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testNotNull() {
		try {
			Dice dice = new StandardDice();
			fail("Dice was able receive null dice array");
		} catch (Exception e) {
			assertTrue("Dice threw exception upon receiving null dice array",true);
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testNotEmpty() {
		try {
			Dice dice = new StandardDice();
			fail("Dice was able receive empty dice array");
		} catch (Exception e) {
			assertTrue("Dice threw exception upon receiving empty dice array",true);
		}
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
