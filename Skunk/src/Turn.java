
public class Turn {
	private Player player;
	private int score;
	private int chipCost;
	private boolean ended;
	private Roll roll;
	
	public Turn (Player player) {
		this.player = player;
		this.score = 0;
		this.chipCost = 0;
		this.roll = new Roll();
		this.ended = false;
	}
	public Player getPlayer() {return this.player;}
	public int getScore() {return this.score;}
	public int getChipCost() {return this.chipCost;}
	public boolean isEnded() {return this.ended;}
	public void roll() {
		if (!this.ended) {
			this.score += this.roll.roll();
			System.out.println(this.player.getName() + " rolled a " + this.roll.getValue() + " which is a " + this.roll.getType().name() + " roll. Their total score for this turn is " + this.score + " so far.");
			if (this.roll.getType().isGameScoreLost())
				this.score = Integer.MIN_VALUE;
			else if (this.roll.getType().isTurnScoreLost())
				this.score = 0;
			this.chipCost += this.roll.getType().getChipCost();
			this.ended = this.roll.getType().isTurnEnded();
		}
	}
	public void end() {this.ended = true;}
}
