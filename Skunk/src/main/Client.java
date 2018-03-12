package main;

public abstract class Client implements Runnable {
	protected Game game;

	public abstract void run();
	public abstract void update(); //update UI
	public void actRoll() {
		this.game.actRoll();
	}
	public void actEnd() {
		this.game.actEnd();
	}
}
