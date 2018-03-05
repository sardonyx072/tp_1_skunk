import java.util.ArrayList;
import java.util.Arrays;

public class Game {
	private Dice dice = new StandardDice();
	private CircularLinkedHashMap<Player,Integer> scores;
	private boolean isEnded;
	private int kitty;
	private int target;
	private Player targetPlayer;
	private Player currentPlayer;
	private int turnScore;
	
	public Game(Player[] players, Dice dice) {
		this.scores = new CircularLinkedHashMap<Player,Integer>();
		Arrays.asList(players).stream().forEach(player -> this.scores.put(player, 0));
		this.kitty = 0;
		this.isEnded = false;
		this.target = 100;
		this.targetPlayer = null;
		this.currentPlayer = new ArrayList<Player>(this.scores.keySet()).get(0);
		this.turnScore = 0;
	}
	public CircularLinkedHashMap<Player,Integer> getScores() {return this.scores;}
	public boolean isEnded() {return this.isEnded;}
	public int getKitty() {return this.kitty;}
	public int getTarget() {return this.target;}
	public Player getTargetPlayer() {return this.targetPlayer;}
	public Player getCurrentPlayer() {return this.currentPlayer;}
	public int getCurrentTurnScore() {return this.turnScore;}
	public void turnOptRoll() {
		this.dice.roll();
		System.out.println(this.currentPlayer.getName() + " rolled a " + this.dice.getValue() + " (" + RollType.find(this.dice) + ")" + "!");
		if (RollType.find(this.dice).isTurnEnded()) {this.turnOptEnd();}
		else {this.turnScore += this.dice.getValue();}
	}
	public void turnOptEnd() {
		RollType type = RollType.find(this.dice);
		if (type.isGameScoreLost()) {this.scores.put(this.currentPlayer, 0);}
		else if (type.isTurnScoreLost()) {this.scores.put(this.currentPlayer, this.scores.get(this.currentPlayer));}
		else {
			this.scores.put(this.currentPlayer, this.scores.get(this.currentPlayer) + this.turnScore);
			if (this.scores.get(this.currentPlayer) > this.target) {
				this.target = this.scores.get(this.currentPlayer);
				this.targetPlayer = this.currentPlayer;
			}
		}
		this.turnScore = 0;
		this.kitty += this.currentPlayer.takeChips(type.getChipCost());
		this.currentPlayer = this.scores.getKeyAfter(this.currentPlayer);
		System.out.println("Passing the dice to " + this.currentPlayer.getName() + ".");
		if (this.currentPlayer == this.targetPlayer) {
			this.isEnded = true;
			this.targetPlayer.giveChips(this.kitty);
			System.out.println(this.targetPlayer.getName() + " won " + this.kitty + " chips!");
			this.kitty = 0;
		}
	}
}
