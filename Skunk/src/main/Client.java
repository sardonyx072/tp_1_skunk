package main;

public abstract class Client implements Runnable {
	protected Game game;

	public abstract void run();
	public abstract void getPlayers(); // get a list of players
	public abstract void update(); //update UI
	public void actRoll() {
		this.game.turnOptRoll();
	}
	public void actEnd() {
		this.game.turnOptEnd();
	}
}
