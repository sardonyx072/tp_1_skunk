import static org.junit.Assert.*;

import org.junit.Test;

public class DiceTest {
	
	@SuppressWarnings("unused")
	@Test
	public void testIsRollable() {
		int[] vals = new int[] {0,1,2,3,4,5,6,7,8,9,10,100,1000,512837697,9999,555,33,8,2,6,9,1,5,2,3,1,2,1,7,0,7,-108475,6,-1,1,0,8};
		try {
			Rollable dice = new Dice(new SimDie[] {new SimDie(vals)});
			assertTrue("Dice was able to be assigned to a Rollable alias",true);
		} catch (Exception e) {
			fail("Dice was not able to be assigned to a Rollable alias");
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testNotNull() {
		try {
			Dice dice = new Dice(null);
			fail("Dice was able receive null dice array");
		} catch (Exception e) {
			assertTrue("Dice threw exception upon receiving null dice array",true);
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testNotEmpty() {
		try {
			Dice dice = new Dice(new StandardDie[] {});
			fail("Dice was able receive empty dice array");
		} catch (Exception e) {
			assertTrue("Dice threw exception upon receiving empty dice array",true);
		}
	}
	
	@Test
	public void testRoll() {
		int[] vals = new int[] {0,1,2,3,4,5,6,7,8,9,10,100,1000,512837697,9999,555,33,8,2,6,9,1,5,2,3,1,2,1,7,0,7,-108475,6,-1,1,0,8};
		Dice dice = new Dice(new SimDie[] {new SimDie(vals)});
		for (int i = 0; i < vals.length; i++) {
			assertEquals("result of roll matches expected",vals[i+1 == vals.length ? 0 : i+1],dice.roll());
		}
	}
	
	@Test
	public void testGetValue() {
		int[] vals = new int[] {0,1,2,3,4,5,6,7,8,9,10,100,1000,512837697,9999,555,33,8,2,6,9,1,5,2,3,1,2,1,7,0,7,-108475,6,-1,1,0,8};
		Dice dice = new Dice(new SimDie[] {new SimDie(vals)});
		for (int i = 0; i < vals.length; i++) {
			assertEquals("result of getValue persisted",vals[i],dice.getValue());
			int test = dice.roll();
			assertEquals("result of getValue matches expected after roll",test,dice.getValue());
		}
	}
}
