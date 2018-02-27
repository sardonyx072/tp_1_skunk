import java.util.Arrays;

public class Dice implements Rollable {
	private Die[] dice;
	public Dice(Die[] dice) {
		if (dice == null)
			throw new NullPointerException("null dice list given to dice");
		else if (dice.length == 0)
			throw new IllegalArgumentException("no dice given to dice");
		this.dice = dice; 
	}
	public int getValue() {return Arrays.asList(this.dice).stream().mapToInt(die -> die.getValue()).sum();}
	public int roll() {return Arrays.asList(this.dice).stream().mapToInt(die -> die.roll()).sum();}
}
