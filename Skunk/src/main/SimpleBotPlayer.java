package main;

import java.util.UUID;

public class SimpleBotPlayer extends BotPlayer {
	public SimpleBotPlayer(int riskThreshold) {
		super(riskThreshold);
	}
	public SimpleBotPlayer(String name, UUID uuid, int chips, int riskThreshold) {
		super(name,uuid,riskThreshold);
	}
	public String act(Game game) {
		if (game.getCurrentScore() > this.riskThreshold) return "End";
		else return "Roll";
	}
}
