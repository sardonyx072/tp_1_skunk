import static org.junit.Assert.*;

import org.junit.Test;

public class SkunkDieTest {

	@Test
	public void test() {
		for (int i = 0; i < 10000; i++) {
			SkunkDie d = new SkunkDie();
			assertEquals(6,d.getValue());
			int temp = d.roll();
			assertTrue(1 <= temp && temp <= 6);
			assertEquals(temp,d.getValue());
		}
	}

}
