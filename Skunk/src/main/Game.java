package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {
	private static Logger LOGGER = null;
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		LOGGER = Logger.getLogger(Game.class.getName());
		try {
			LOGGER.addHandler(new FileHandler("./log/log.txt"));
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.setLevel(Level.ALL);
	}
	private static final String SEPARATOR = "::";
	private static final int DEFAULT_TARGET = 100;
	private Dice dice;
	private CircularLinkedHashMap<Player,Integer> scores;
	private boolean isEnded;
	private int kitty;
	private int target;
	private Player targetPlayer;
	private Player currentPlayer;
	private int turnScore;
	private int numGamesThisMatch;
	private Stat stats;
	
	public Game(Player[] players, Dice dice) throws SecurityException, IOException {
		this.scores = new CircularLinkedHashMap<Player,Integer>();
		Arrays.asList(players).stream().forEach(player -> this.scores.put(player, 0));
		this.dice = dice;
		this.kitty = 0;
		this.isEnded = false;
		this.target = DEFAULT_TARGET;
		this.targetPlayer = null;
		this.currentPlayer = new ArrayList<Player>(this.scores.keySet()).get(0);
		this.turnScore = 0;
		this.numGamesThisMatch = 0;
		this.stats = new Stat();
//		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
//		LOGGER = Logger.getLogger(Game.class.getName());
//		LOGGER.addHandler(new FileHandler("./log/log.txt"));
	}
	public CircularLinkedHashMap<Player,Integer> getScores() {return this.scores;}
	public boolean isEnded() {return this.isEnded;}
	public int getKitty() {return this.kitty;}
	public int getTarget() {return this.target;}
	public Player getTargetPlayer() {return this.targetPlayer;}
	public Player getCurrentPlayer() {return this.currentPlayer;}
	public int getCurrentTurnScore() {return this.turnScore;}
	public int getNumGames() {return this.numGamesThisMatch;}
	public Stat getStats() {return this.stats;}
	public void actRoll() {
		this.dice.roll();
		LOGGER.info(this.currentPlayer.getName() + " rolled a " + this.dice.getValues()[0] + "+" + this.dice.getValues()[1] + "=" + this.dice.getValue() + " (" + RollType.find(this.dice) + ")" + "!");
		this.processRoll(RollType.find(this.dice), this.dice.getValue());
	}
	private void actRoll(RollType type, int value) {
		this.processRoll(type,value);
	}
	private void processRoll(RollType type, int value) {
		this.stats.addRoll(this.numGamesThisMatch,this.currentPlayer, type, value);
		if (type.isTurnEnded()) {this.processEnd(type, value);}
		else {this.turnScore += value;}
	}
	public void actEnd() {
		LOGGER.info(this.currentPlayer.getName() + " decided to end their turn having accumulated " + this.turnScore + " extra points, for a total of " + (this.scores.get(this.currentPlayer)+this.turnScore) + " points!");
		this.processEnd(RollType.find(this.dice),this.dice.getValue());
	}
	private void actEnd(RollType type, int value) {
		this.processEnd(type, value);
	}
	private void processEnd(RollType type, int value) {
		this.stats.addEnd(this.getNumGames(), this.currentPlayer);
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
		LOGGER.info("Passing the dice to " + this.currentPlayer.getName() + ".");
		if (this.currentPlayer == this.targetPlayer) {
			this.targetPlayer.giveChips(this.kitty);
			LOGGER.info(this.targetPlayer.getName() + " won " + this.kitty + " chips!");
			this.kitty = 0;
			this.scores.keySet().stream().forEach(player -> this.scores.put(player, 0));
			this.target = DEFAULT_TARGET;
			this.targetPlayer = null;
			this.numGamesThisMatch++;
			if (this.scores.keySet().stream().filter(player -> player.getChips() > 0).count() == 1) {
				LOGGER.info(this.currentPlayer.getName() + " won the match!");
				LOGGER.info("The match lasted " + this.numGamesThisMatch + " games!");
				this.isEnded = true;
			}
		}
	}
	public static void save(Game game, String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(":Players");
			for (Player player : game.scores.keySet())
				writer.write("\n" + player.getUUID() + SEPARATOR + player.getName() + SEPARATOR + player.getChips());
			writer.write("\n");
			writer.write(":Game");
			writer.write("\n");
			writer.write(game.dice.getClass().getName());
			writer.write("\n");
			writer.write(game.stats.printHistory(SEPARATOR));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static Game load(String file) {
		Game game = null;
		BufferedReader reader = null;
		try {
			List<Player> players = new ArrayList<Player>();
			Dice dice = null;
			LOGGER.info("Attempting to load game from file \"" + file + "\"...");
			reader = new BufferedReader(new FileReader(new File(file)));
			String in = reader.readLine();
			assert in.equals(":Players");
			while (in != null && (in = reader.readLine()) != null && !in.equals(":Game")) {
				String[] parts = in.split(SEPARATOR);
				players.add(new Player(parts[1],UUID.fromString(parts[0]),Integer.parseInt(parts[2])));
			}
			if (in != null && (in = reader.readLine()) != null) {
				dice = (Dice) Class.forName(in).getConstructor().newInstance(new Object[] {});
			}
			game = new Game(players.toArray(new Player[players.size()]),dice);
			in = reader.readLine();
			assert in.equals(":Opts");
			RollType lastType = RollType.Normal;
			int lastValue = 0;
			while (in != null && (in = reader.readLine()) != null) {
				String[] parts = in.split(SEPARATOR);
				assert game.numGamesThisMatch == Integer.parseInt(parts[1]);
				assert game.getCurrentPlayer().getUUID().toString().equals(parts[2]);
				if (parts[0].equals("Roll")) {
					lastType = RollType.valueOf(parts[3]);
					lastValue = Integer.parseInt(parts[4]);
					game.actRoll(lastType, lastValue);
				}
				else if (parts[0].equals("End")) {
					game.actEnd(lastType,lastValue);
					lastType = RollType.Normal;
					lastValue = 0;
				}
				else
					throw new IOException("could not properly read file");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LOGGER.info("Game loaded successfully!");
		return game;
	}
}
