package main;

import java.util.ArrayList;
import java.util.List;

public class DiceStats {
	private List<DiceStatPoint> points;
	
	public DiceStats() {
		this.points = new ArrayList<DiceStatPoint>();
	}
	public void add(Player player, RollType type, int value) {
		this.points.add(new DiceStatPoint(player,type,value));
	}
	public int getTotal() {return this.points.stream().mapToInt(point -> point.getValue()).sum();}
	public int getTotalForPlayer(Player player) {return this.points.stream().filter(point -> point.getPlayer() == player).mapToInt(point -> point.getValue()).sum();}
	public int getNumForPlayer(Player player) {return (int) this.points.stream().filter(point -> point.getPlayer() == player).count();}
	public int getNumOfType(RollType type) {return (int) this.points.stream().filter(point -> point.getType() == type).count();}
	public int getNumOfTypeForPlayer(Player player, RollType type) {return (int) this.points.stream().filter(point -> point.getType() == type).filter(point -> point.getPlayer() == player).count();}
	public int getNumOfValue(int value) {return (int) this.points.stream().filter(point -> point.getValue() == value).count();}
	public int[] getValues() {
		int[] result = new int[this.points.stream().mapToInt(point -> point.getValue()).max().getAsInt()];
		this.points.stream().mapToInt(point -> point.getValue()).forEach(point -> result[point]++);
		return result;
	}
	
	private final class DiceStatPoint {
		private Player player;
		private RollType type;
		private int value;
		
		public DiceStatPoint(Player player, RollType type, int value) {
			this.player = player;
			this.type = type;
			this.value = value;
		}
		public Player getPlayer() {return this.player;}
		public RollType getType() {return this.type;}
		public int getValue() {return this.value;}
	}
}
