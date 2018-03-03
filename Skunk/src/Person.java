
public class Person {
	protected String name;
	
	public Person(String name) {
		if (name == null) throw new NullPointerException("Person name cannot be null");
		if (name.isEmpty()) throw new IllegalArgumentException("Person name cannot be empty");
		this.name = name;
	}
	public String getName() {return this.name;}
}
