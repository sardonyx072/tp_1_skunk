package main;

public class SimpleBotPlayer extends BotPlayer {
	public SimpleBotPlayer(int chips, int riskThreshold) {
		super(chips,riskThreshold);
	}
	public String act(Game game) {
		if (game.getCurrentTurnScore() >= this.riskThreshold) return "End";
		else return "Roll";
	}
}
