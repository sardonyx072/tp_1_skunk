package main;

import java.util.ArrayList;
import java.util.List;

public class DiceStats {
	private List<DiceStatPoint> points;
	
	public DiceStats() {
		this.points = new ArrayList<DiceStatPoint>();
	}
	public void add(int game, Player player, RollType type, int value) {
		this.points.add(new DiceStatPoint(game,player,type,value));
	}
	public int getTotal(int game, Player player, RollType type, int value) {return this.points.stream()
			.filter(point -> game != -1 ? point.getGame() == game : true)
			.filter(point -> player != null ? point.getPlayer() == player : true)
			.filter(point -> type != null ? point.getType() == type : true)
			.filter(point -> value != -1 ? point.getValue() == value : true)
			.mapToInt(point -> point.getValue()).sum();
	}
	public int getNum(int game, Player player, RollType type, int value) {return (int) this.points.stream()
			.filter(point -> game != -1 ? point.getGame() == game : true)
			.filter(point -> player != null ? point.getPlayer() == player : true)
			.filter(point -> type != null ? point.getType() == type : true)
			.filter(point -> value != -1 ? point.getValue() == value : true)
			.count();
	}
	public int[] getDistribution() {
		int[] result = new int[this.points.stream().mapToInt(point -> point.getValue()).max().getAsInt()+1];
		this.points.stream().mapToInt(point -> point.getValue()).forEach(point -> result[point]++);
		return result;
	}
	
	private final class DiceStatPoint {
		private int game;
		private Player player;
		private RollType type;
		private int value;
		
		public DiceStatPoint(int game, Player player, RollType type, int value) {
			this.game = game;
			this.player = player;
			this.type = type;
			this.value = value;
		}
		public int getGame() {return this.game;}
		public Player getPlayer() {return this.player;}
		public RollType getType() {return this.type;}
		public int getValue() {return this.value;}
	}
}
