import java.util.Arrays;

public class SkunkGame {
	private final int SKUNK = 1;
	private final int DEUCE = 2;
	private final int STARTING_CHIPS = 50;
	private final int CHIPCOST_DOUBLESKUNK = 4;
	private final int CHIPCOST_SKUNKDEUCE = 2;
	private final int CHIPCOST_SKUNK = 1;
	private int round;
	private int target = 100;
	private int goal;
	private int iSetGoalFirst;
	private int[] scores;
	private int[] chips;
	private int iCurrent;
	private IDie d1, d2;
	private int sum;
	private int kitty;
	
	public SkunkGame(int numPlayers) {
		this.goal = 0;
		this.iSetGoalFirst = -1;
		this.scores = new int[numPlayers];
		Arrays.fill(this.chips = new int[numPlayers],this.STARTING_CHIPS);
		this.iCurrent = 0;
		this.d1 = new SkunkDie();
		this.d2 = new SkunkDie();
		this.sum = 0;
		this.kitty = 0;
	}
	public int getRoundNum() {return this.round;}
	public int getTarget() {return this.target;}
	public int getCurrentSum() {return this.sum;}
	public int getKitty() {return this.kitty;}
	public int getCurrentPlayer() {return this.iCurrent;}
	public int getPlayerScore(int index) {return this.scores[index];}
	public int getPlayerChips(int index) {return this.chips[index];}
	public void roll() {
		d1.roll();
		d2.roll();
		if (d1.getValue() == this.SKUNK && d2.getValue() == this.SKUNK) {
			this.scores[this.iCurrent] = 0;
			this.sum = 0;
			this.chips[this.iCurrent]-=CHIPCOST_DOUBLESKUNK;
			this.kitty+=CHIPCOST_DOUBLESKUNK;
			this.pass();
		}
		else if ((d1.getValue() == this.SKUNK && d2.getValue() == this.DEUCE)
				|| (d1.getValue() == this.DEUCE && d2.getValue() == this.SKUNK)) {
			this.sum = 0;
			this.chips[this.iCurrent]-=CHIPCOST_SKUNKDEUCE;
			this.kitty+=CHIPCOST_SKUNKDEUCE;
			this.pass();
		}
		else if (d1.getValue() == this.SKUNK || d2.getValue() == this.SKUNK) {
			this.sum = 0;
			this.chips[this.iCurrent]-=CHIPCOST_SKUNK;
			this.kitty+=CHIPCOST_SKUNK;
			this.pass();
		}
	}
	public void pass() {
		this.scores[this.iCurrent] += this.sum;
		if (this.iSetGoalFirst == this.iCurrent) {
			//game over
			//find winner
			// TODO what if two players tie?
		}
		else if (this.scores[this.iCurrent] >= this.target) {
			this.goal = this.scores[this.iCurrent];
			this.iSetGoalFirst = this.iCurrent;
		}
		else if (this.iSetGoalFirst >= 0 && this.scores[this.iCurrent] > this.goal) {
			this.goal = this.scores[this.iCurrent];
		}
		this.iCurrent++;
		if (this.iCurrent >= this.scores.length) {
			this.iCurrent = 0;
			this.round++;
		}
	}
}
