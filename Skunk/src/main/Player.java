package main;

import java.util.UUID;

public class Player extends Person {
	private int chips;
	
	public Player(String name, int chips) {
		this(name,UUID.randomUUID(),chips);
	}
	public Player(String name, UUID uuid, int chips) {
		super(name, uuid);
		if (chips < 0)
			throw new IllegalArgumentException("invalid number of starting chips for player");
		this.chips = chips;
	}
	public int getChips() {
		return this.chips;
	}
	public void giveChips(int chips) {
		if (chips < 0) throw new IllegalArgumentException("cannot give negative chips");
		this.chips+=chips;
	}
	public int takeChips(int chips) {
		int taken = Math.min(this.chips, chips);
		this.chips -= taken;
		return taken;
	}
	// TODO stats?
}
