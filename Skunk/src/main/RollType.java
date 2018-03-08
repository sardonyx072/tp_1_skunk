package main;
import java.util.stream.IntStream;

public enum RollType {
	DoubleSkunk(true,true,true,4),
	SkunkDeuce(true,true,false,2),
	Skunk(true,true,false,1),
	Normal(false,false,false,0);
	
	private boolean turnEnd, loseTurnScore, loseGameScore;
	private int chipCost;
	private RollType(boolean turnEnd, boolean loseTurnScore, boolean loseGameScore, int chipCost) {
		this.turnEnd = turnEnd;
		this.loseTurnScore = loseTurnScore;
		this.loseGameScore = loseGameScore;
		this.chipCost = chipCost;
	}
	public boolean isTurnEnded() {return this.turnEnd;}
	public boolean isTurnScoreLost() {return this.loseTurnScore;}
	public boolean isGameScoreLost() {return this.loseGameScore;}
	public int getChipCost() {return this.chipCost;}
	public static RollType find(Dice dice) {
		if (dice.getValue() == 2) {return RollType.DoubleSkunk;}
		else if (dice.getValue() == 3) {return RollType.SkunkDeuce;}
		else if (IntStream.of(dice.getValues()).anyMatch(i -> i==1)) {return RollType.Skunk;}
		else {return RollType.Normal;}
	}
}
