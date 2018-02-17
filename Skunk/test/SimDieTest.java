import static org.junit.Assert.*;

import org.junit.Test;

public class SimDieTest {

	@Test
	public void test() {
		SimDie d = new SimDie(new int[] {1});
		assertEquals(1, d.getValue());
		assertEquals(1, d.roll());
		assertEquals(1, d.getValue());
		d = new SimDie(new int[] {1,2});
		assertEquals(1, d.getValue());
		assertEquals(2, d.roll());
		assertEquals(2, d.getValue());
		assertEquals(1, d.roll());
		assertEquals(1, d.getValue());
	}

}
