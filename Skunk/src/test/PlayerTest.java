package test;
import static org.junit.Assert.*;

import org.junit.Test;

import main.Person;
import main.Player;

public class PlayerTest {
	
	@SuppressWarnings("unused")
	@Test
	public void testIsPerson() {
		boolean test = false;
		try {
			Person person = new Player("A",50);
			test = true;
		} catch (Exception e) {}
		assertTrue("Player can be assigned to a Person alias",test);
	}

	@SuppressWarnings("unused")
	@Test
	public void testName() {
		boolean test = true;
		try {
			Person person = new Player(null,50);
			test = false;
		} catch (Exception e) {}
		assertTrue("Person throws exception if name is null",test);
		
		test = true;
		try {
			Person person = new Player("",50);
			test = false;
		} catch (Exception e) {}
		assertTrue("Person throws exception if name is empty",test);
		
		test = false;
		try {
			Person person = new Player("A",50);
			test = true;
		} catch (Exception e) {}
		assertTrue("Person can have one character name",test);
		
		String name = "TestName123";
		Person person = new Player(name,50);
		assertEquals("Person name matches expected",name,person.getName());
	}

	@SuppressWarnings("unused")
	@Test
	public void testChips() {
		boolean test = true;
		try {
			Player player = new Player("A",-1);
			test = false;
		} catch (Exception e) {}
		assertTrue("Player starting chips cannot be negative", test);

		test = true;
		try {
			Player player = new Player("A",0);
			test = false;
		} catch (Exception e) {}
		assertTrue("Player starting chips cannot be zero", test);

		test = false;
		try {
			Player player = new Player("A",1);
			test = true;
		} catch (Exception e) {}
		assertTrue("Player starting chips can be one", test);
	}
}
