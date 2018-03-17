package test;
import static org.junit.Assert.*;

import org.junit.Test;

import main.Player;

public class PlayerTest {

	@SuppressWarnings("unused")
	@Test
	public void testName() {
		boolean test = true;
		try {
			Player person = new Player(null);
			test = false;
		} catch (Exception e) {}
		assertTrue("Person throws exception if name is null",test);
		
		test = true;
		try {
			Player person = new Player("");
			test = false;
		} catch (Exception e) {}
		assertTrue("Person throws exception if name is empty",test);
		
		test = false;
		try {
			Player person = new Player("A");
			test = true;
		} catch (Exception e) {}
		assertTrue("Person can have one character name",test);
		
		String name = "TestName123";
		Player person = new Player(name);
		assertEquals("Person name matches expected",name,person.getName());
	}
}
