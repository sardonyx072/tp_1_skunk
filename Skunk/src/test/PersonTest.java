package test;
import static org.junit.Assert.*;

import org.junit.Test;

import main.Person;

public class PersonTest {

	@Test
	public void testName() {
		boolean test = true;
		try {
			Person person = new Person(null);
			test = false;
		} catch (Exception e) {}
		assertTrue("Person throws exception if name is null",test);
		test = true;
		try {
			Person person = new Person("");
			test = false;
		} catch (Exception e) {}
		assertTrue("Person throws exception if name is empty",test);
		String name = "TestName123";
		Person person = new Person(name);
		assertEquals("Person name matches expected",name,person.getName());
	}

}
