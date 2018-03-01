import java.util.stream.IntStream;

public class Turn {
	private Player player;
	private boolean loseScore;
	private int score;
	private Dice dice;
	
	public Turn (Player player) {
		this.player = player;
		this.loseScore = false;
		this.score = 0;
		this.dice = new StandardDice();
	}
	public Player getPlayer() {return this.player;}
	public boolean getLoseScore() {return this.loseScore;}
	public int getScore() {return this.score;}
	public boolean roll() {
		boolean endTurn = false;
		this.dice.roll();
		if (this.dice.getValue() == 2) { //double skunk
			this.loseScore = true;
			this.score = 0;
			endTurn = true;
		}
		else if (this.dice.getValue() == 3) { //skunk deuce
			
			this.score = 0;
			endTurn = true;
		}
		else if (IntStream.of(this.dice.getValues()).anyMatch(i -> i==1)) { // skunk
			this.score = 0;
			endTurn = true;
		}
		else {
			this.score+=this.dice.getValue();
			endTurn = false;
		}
		return endTurn;
	}
}
