package main;

import java.util.UUID;

public class SimpleBotPlayer extends BotPlayer {
	public SimpleBotPlayer(int chips, int riskThreshold) {
		super(chips,riskThreshold);
	}
	public SimpleBotPlayer(String name, UUID uuid, int chips, int riskThreshold) {
		super(name,uuid,chips,riskThreshold);
	}
	public String act(Game game) {
		if (game.getCurrentTurnScore() > this.riskThreshold) return "End";
		else return "Roll";
	}
}
