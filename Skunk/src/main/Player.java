package main;

import java.util.UUID;

public class Player extends Person {
	
	public Player(String name) {
		this(name,UUID.randomUUID());
	}
	public Player(String name, UUID uuid) {
		super(name, uuid);
	}
	public String toString() {return "{"+this.getName()+","+this.getUUID()+"}";}
	// TODO stats?
}
