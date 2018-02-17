import static org.junit.Assert.*;

import org.junit.Test;

public class DieTest {

	@Test
	public void test() {
		for (int sides: new int[] {1,2,4,6,8,10,12,20}) {
			for (int i = 0; i < 10000; i++) {
				Die d = new Die(sides);
				assertEquals(sides,d.getValue());
				int temp = d.roll();
				assertTrue(1 <= temp && temp <= sides);
				assertEquals(temp,d.getValue());
			}
		}
	}

}
