import java.util.Arrays;
import java.util.LinkedHashMap;

public class Game {
	private LinkedHashMap<Player,Integer> scores;
	private boolean isEnded;
	private int kitty;
	private int i;
	private Turn currentTurn;
	private int target;
	private Player targetPlayer;
	
	public Game(Player[] players) {
		this.scores = new LinkedHashMap<Player,Integer>();
		for (Player player : players)
			this.scores.put(player, 0);
		//Arrays.asList(players).stream().map(player -> {this.scores.put(player, 0);});
		//Arrays.asList(players).stream().map(player -> this.scores.put(player, 0));
		this.kitty = 0;
		this.isEnded = false;
		this.i = 0;
		this.currentTurn = new Turn(this.scores.keySet().toArray(new Player[this.scores.keySet().size()])[i]);
		this.target = 100;
		this.targetPlayer = null;
	}
	public LinkedHashMap<Player,Integer> getScores() {return this.scores;}
	public boolean isEnded() {return this.isEnded;}
	public int getKitty() {return this.kitty;}
	public Turn getCurrentTurn() {return this.currentTurn;}
	public int getTarget() {return this.target;}
	public Player getTargetPlayer() {return this.targetPlayer;}
	public void roll() {
		this.currentTurn.roll();
		if (this.currentTurn.isEnded()) {this.end();}
	}
	public void end() {
		this.scores.put(this.currentTurn.getPlayer(), this.scores.get(this.currentTurn.getPlayer()) + this.currentTurn.getScore());
		this.kitty += this.currentTurn.getPlayer().takeChips(this.currentTurn.getChipCost());
		if (this.scores.get(this.currentTurn.getPlayer()) > this.target) {
			this.target = this.scores.get(this.currentTurn.getPlayer());
			this.targetPlayer = this.currentTurn.getPlayer();
			System.out.println(this.targetPlayer.getName() + " set a new target at " + this.target + "!");
		}
		i = i+1 == this.scores.keySet().size() ? 0 : i+1;
		this.currentTurn = new Turn(this.scores.keySet().toArray(new Player[this.scores.keySet().size()])[i]);
		System.out.println("Passing the dice to " + this.currentTurn.getPlayer().getName() + ".");
		if (this.currentTurn.getPlayer() == this.targetPlayer) {
			this.isEnded = true;
			this.targetPlayer.giveChips(this.kitty);
			System.out.println(this.targetPlayer.getName() + " won " + this.kitty + " chips!");
		}
	}
}
