import java.util.stream.IntStream;

public class Roll implements Rollable {
	private Dice dice;
	private RollType type;
	
	public Roll() {
		this.dice = new StandardDice();
		this.roll();
	}
	public int getValue() {return this.dice.getValue();}
	public int roll() {
		this.dice.roll();
		if (this.dice.getValue() == 2) {this.type = RollType.DoubleSkunk;}
		else if (this.dice.getValue() == 3) {this.type = RollType.SkunkDeuce;}
		else if (IntStream.of(this.dice.getValues()).anyMatch(i -> i==1)) {this.type = RollType.Skunk;}
		else {this.type = RollType.Normal;}
		return this.dice.getValue();
	}
	public RollType getType() {return this.type;}
}
