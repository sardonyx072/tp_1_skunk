package main;

import java.util.UUID;

public class Player extends Person {
	
	public Player(String name) {
		this(name,UUID.randomUUID());
	}
	public Player(String name, UUID uuid) {
		super(name, uuid);
	}
	// TODO stats?
}
