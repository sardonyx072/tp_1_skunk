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
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Game {
	private static final String SEPARATOR = "::";
	private static final int DEFAULT_TARGET = 100;
	private static Logger LOGGER = null;
	static {
		try {
			LogManager.getLogManager().readConfiguration(CommandLineClient.class.getClassLoader().getResourceAsStream("main/resources/logging.properties"));
			File logdir = new File(LogManager.getLogManager().getProperty("java.util.logging.FileHandler.pattern")).getParentFile();
			if (logdir != null) logdir.mkdir();
			LOGGER = Logger.getLogger(CommandLineClient.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
	public boolean setUpTurn() {
		while (!this.isEnded && this.currentPlayer instanceof BotPlayer) {
			switch(((BotPlayer)this.currentPlayer).act(this)) {
			case "Roll":
				this.actRoll();
				break;
			case "End":
				this.actEnd();
				break;
			default:
				LOGGER.warning("Bot player returned unexpected action");
				break;
			}
		}
		return this.isEnded;
	}
	public void actRoll() {
		int value = this.dice.roll();
		RollType type = RollType.find(this.dice);
		String message = this.currentPlayer.getName() + " rolled " + this.dice.toString() + " (" + type + ")" + "!";
		LOGGER.info(message);
		this.processRoll(type, value);
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
			File savedir = new File(file).getParentFile();
			if (savedir != null) savedir.mkdir();
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(":Players");
			for (Player player : game.scores.keySet())
				writer.write("\n" + player.getUUID() + SEPARATOR + player.getName() + SEPARATOR + player.getChips() + SEPARATOR + game.scores.get(player) + (player instanceof BotPlayer ? SEPARATOR + player.getClass().getName() + SEPARATOR + ((BotPlayer)player).getThreshold() : ""));
			LOGGER.info("Wrote all players to save file");
			writer.write("\n");
			writer.write(":Game");
			writer.write("\n");
			writer.write("Dice" + SEPARATOR + game.dice.flatten());
			LOGGER.info("Wrote dice to save file");
			writer.write("\n");
			writer.write("GameNo" + SEPARATOR + game.numGamesThisMatch);
			LOGGER.info("Wrote game no to save file");
			writer.write("\n");
			writer.write("Kitty" + SEPARATOR + game.kitty);
			LOGGER.info("Wrote kitty to save file");
			writer.write("\n");
			writer.write("Target" + SEPARATOR + game.target);
			LOGGER.info("Wrote target to save file");
			writer.write("\n");
			writer.write("TargetPlayer" + SEPARATOR + (game.targetPlayer != null ? game.targetPlayer.getUUID().toString() : "null"));
			LOGGER.info("Wrote target player to save file");
			writer.write("\n");
			writer.write("CurrentPlayer" + SEPARATOR + game.currentPlayer.getUUID().toString());
			LOGGER.info("Wrote current player to save file");
			writer.write("\n");
			writer.write("TurnScore" + SEPARATOR + game.turnScore);
			LOGGER.info("Wrote turn score to save file");
			writer.write("\n");
			writer.write("isEnded" + SEPARATOR + game.isEnded);
			LOGGER.info("Wrote is ended to save file");
			writer.write("\n");
			writer.write(game.stats.printHistory(SEPARATOR));
			LOGGER.info("Wrote history to save file");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.info("Exception!");
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
			List<Integer> scores = new ArrayList<Integer>();
			Dice dice = null;
			String[] parts;
			LOGGER.info("Attempting to load game from file \"" + file + "\"...");
			reader = new BufferedReader(new FileReader(new File(file)));
			// players
			String in = reader.readLine();
			assert in.equals(":Players");
			while (in != null && (in = reader.readLine()) != null && !in.equals(":Game")) {
				parts = in.split(SEPARATOR);
				if (parts.length == 4)
					players.add(new Player(parts[1],UUID.fromString(parts[0]),Integer.parseInt(parts[2])));
				else if (parts.length == 6)
					players.add((Player) Class.forName(parts[4]).getConstructor(String.class,UUID.class,int.class,int.class).newInstance(new Object[] {parts[1],UUID.fromString(parts[0]),Integer.parseInt(parts[2]),Integer.parseInt(parts[5])}));
				scores.add(Integer.parseInt(parts[3]));
			}
			LOGGER.info("loaded players");
			// dice
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("Dice");
			dice = (Dice) Class.forName(parts[1]).getConstructor().newInstance(new Object[] {});
			LOGGER.info("loaded dice");
			game = new Game(players.toArray(new Player[players.size()]),dice);
			for (int i = 0; i < players.size(); i++)
				game.scores.put(players.get(i), scores.get(i));
			// game no
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("GameNo");
			game.numGamesThisMatch = Integer.parseInt(parts[1]);
			LOGGER.info("loaded game no");
			// kitty
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("Kitty");
			game.kitty = Integer.parseInt(parts[1]);
			LOGGER.info("loaded kitty");
			// target
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("Target");
			game.target = Integer.parseInt(parts[1]);
			LOGGER.info("loaded target");
			// target player
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("TargetPlayer");
			final String temp1 = parts[1];
			game.targetPlayer = (temp1.equals("null") ? null : (Player) game.scores.keySet().stream().filter(player -> player.getUUID().equals(UUID.fromString(temp1))).toArray()[0]);
			LOGGER.info("loaded targetplayer");
			// current player
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("CurrentPlayer");
			final String temp2 = parts[1];
			game.currentPlayer = (Player) game.scores.keySet().stream().filter(player -> player.getUUID().equals(UUID.fromString(temp2))).toArray()[0];
			LOGGER.info("loaded currentplayer");
			// turn score
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("TurnScore");
			game.turnScore = Integer.parseInt(parts[1]);
			LOGGER.info("loaded turnscore");
			// is ended
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("isEnded");
			game.isEnded = Boolean.parseBoolean(parts[1]);
			LOGGER.info("loaded isended");
			// history
			in = reader.readLine();
			assert in.equals(":Opts");
			while (in != null && (in = reader.readLine()) != null) {
				parts = in.split(SEPARATOR);
				LOGGER.info(Arrays.toString(parts));
				final String temp3 = parts[2];
				if (parts[0].equals("Roll")) {
					game.stats.addRoll(Integer.parseInt(parts[1]), (Player) game.scores.keySet().stream().filter(player -> player.getUUID().equals(UUID.fromString(temp3))).toArray()[0], RollType.valueOf(parts[3]), Integer.parseInt(parts[4]));
				}
				else if (parts[0].equals("End")) {
					game.stats.addEnd(Integer.parseInt(parts[1]), (Player) game.scores.keySet().stream().filter(player -> player.getUUID().equals(UUID.fromString(temp3))).toArray()[0]);
				}
				else
					throw new IOException("could not properly read file");
			}
			LOGGER.info("loaded history");
			LOGGER.info("Game loaded successfully!");
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return game;
	}
}
