package main;

import java.util.List;

public abstract class Client implements Runnable {
	protected Game game;

	public abstract void run();
	public abstract void update(); //update UI
	public List<String> actRoll() {
		return this.game.actRoll();
	}
	public List<String> actEnd() {
		return this.game.actEnd();
	}
}
