package main;

import java.util.UUID;

public class Player {
	protected UUID uuid;
	protected String name;
	
	public Player(String name) {
		this(name,UUID.randomUUID());
	}
	public Player(String name, UUID uuid) {
		if (name == null) throw new NullPointerException("Person name cannot be null");
		if (name.isEmpty()) throw new IllegalArgumentException("Person name cannot be empty");
		this.name = name;
		this.uuid = uuid;
	}
	public UUID getUUID() {return this.uuid;}
	public String getName() {return this.name;}
	public void setName(String name) {this.name = name;}
	public String toString() {return "{"+this.getName()+","+this.getUUID()+"}";}
	// TODO stats?
}
