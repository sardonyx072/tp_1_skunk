package main;

public class Player extends Person {
	private int chips;
	
	public Player(String name, int chips) {
		super(name);
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
