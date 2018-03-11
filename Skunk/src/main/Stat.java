package main;

import java.util.ArrayList;
import java.util.List;

public class Stat {
	private List<Point> points;
	
	public Stat () {
		this.points = new ArrayList<Point>();
	}
	public void addRoll(int game, Player player, RollType type, int value) {this.points.add(new RollPoint(game, player, type, value));}
	public void addEnd(int game, Player player) {this.points.add(new EndPoint(game, player));}
	public int getTotal(int game, Player player, RollType type, int value) {return this.points.stream().filter(point -> point instanceof RollPoint).map(point -> (RollPoint)point)
			.filter(point -> game != -1 ? point.game == game : true)
			.filter(point -> player != null ? point.player == player : true)
			.filter(point -> type != null ? point.type == type : true)
			.filter(point -> value != -1 ? point.value == value : true)
			.mapToInt(point -> point.value).sum();
	}
	public int getNum(int game, Player player, RollType type, int value) {return (int) this.points.stream().filter(point -> point instanceof RollPoint).map(point -> (RollPoint)point)
			.filter(point -> game != -1 ? point.game == game : true)
			.filter(point -> player != null ? point.player == player : true)
			.filter(point -> type != null ? point.type == type : true)
			.filter(point -> value != -1 ? point.value == value : true)
			.count();
	}
	public int getNum(int game, Player player) {return (int) this.points.stream().filter(point -> point instanceof RollPoint).map(point -> (EndPoint)point)
			.filter(point -> game != -1 ? point.game == game : true)
			.filter(point -> player != null ? point.player == player : true)
			.count();
	}
	public String printHistory(String separator) {
		StringBuilder result = new StringBuilder();
		result.append(":Opts");
		this.points.stream().forEach(point -> result.append("\n" + (point instanceof RollPoint ? 
				"Roll"+separator+point.game+separator+point.player.getUUID()+separator+((RollPoint)point).type+separator+((RollPoint)point).value
				: "End"+separator+point.game+separator+point.player.getUUID())));
		return result.toString();
	}
	
	private abstract class Point {
		public int game;
		public Player player;
		public Point(int game, Player player) {
			this.game = game;
			this.player = player;
		}
	}
	private class RollPoint extends Point {
		public RollType type;
		public int value;
		
		public RollPoint (int game, Player player, RollType type, int value) {
			super(game,player);
			this.type = type;
			this.value = value;
		}
	}
	private class EndPoint extends Point {
		
		public EndPoint (int game, Player player) {
			super(game,player);
		}
	}
}
