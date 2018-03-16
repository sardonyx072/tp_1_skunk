package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Game {
	private static enum PlayerInfoType {
		CHIPS,
		SCORE,
		STATE;
	}
	private static final int MAX_PLAYERS = 1023, MAX_STARTING_CHIPS_PER_PLAYER = 2097152;
	private static final String SEPARATOR = "|", ILLEGAL_CHARS = SEPARATOR+"";
	private static final int DEFAULT_TARGET = 100;
	private static Logger LOGGER = null;
	static {
		try {
			LogManager.getLogManager().readConfiguration(CommandLineClient.class.getClassLoader().getResourceAsStream("main/resources/logging.properties"));
			File logdir = new File(LogManager.getLogManager().getProperty("java.util.logging.FileHandler.pattern")).getParentFile();
			if (logdir != null) logdir.mkdir();
			LOGGER = Logger.getLogger(CommandLineClient.class.getName());
		} catch (Exception e) {e.printStackTrace();}
	}
	private Dice dice;
	private Stat stats;
	private CircularLinkedHashMap<Player,HashMap<PlayerInfoType,Integer>> info;
	private Player targetPlayer;
	private int targetScore;
	private Player currentPlayer;
	private int currentScore;
	private int kitty;
	private boolean isActive;
	private int gameNum;
	
	public Game(Dice dice) {
		this.dice = dice;
		this.stats = new Stat();
		this.info = new CircularLinkedHashMap<Player,HashMap<PlayerInfoType,Integer>>();
		this.targetPlayer = null;
		this.targetScore = DEFAULT_TARGET;
		this.currentPlayer = null;
		this.currentScore = 0;
		this.kitty = 0;
		this.isActive = false;
		this.gameNum = 0;
	}
	public static int getMaxPlayers() {return MAX_PLAYERS;}
	public static int getMaxStartingChips() {return MAX_STARTING_CHIPS_PER_PLAYER;}
	public static String getIllegalChars() {return ILLEGAL_CHARS;}
	public Player[] getPlayers() {return this.info.keySet().toArray(new Player[this.info.keySet().size()]);}
	private Player getPlayer(UUID uuid) {return (Player) this.info.keySet().stream().filter(player -> player.getUUID().equals(uuid)).toArray()[0];}
	private CircularLinkedHashMap<Player,Integer> getInfo(PlayerInfoType type) {
		CircularLinkedHashMap<Player,Integer> result = new CircularLinkedHashMap<Player,Integer>();
		for (Player player : this.info.keySet()) result.put(player, this.info.get(player).get(type));
		return result;
	}
	public CircularLinkedHashMap<Player,Integer> getScores() {return this.getInfo(PlayerInfoType.SCORE);}
	public CircularLinkedHashMap<Player,Integer> getChips() {return this.getInfo(PlayerInfoType.CHIPS);}
	public CircularLinkedHashMap<Player,Integer> getStates() {return this.getInfo(PlayerInfoType.STATE);}
	public Player[] getPlayersStillIn() {return (Player[]) this.getStates().entrySet().stream().filter(entry -> entry.getValue() != -1).map(entry -> entry.getKey()).toArray();}
	public boolean hasWinner() {return this.getPlayersStillIn().length==1;}
	public void addPlayer(Player player) {
		HashMap<PlayerInfoType,Integer> info = new HashMap<PlayerInfoType,Integer>();
		info.put(PlayerInfoType.SCORE,0);
		info.put(PlayerInfoType.CHIPS,0);
		info.put(PlayerInfoType.STATE,0);
		this.info.put(player, info);
		if (this.currentPlayer == null) this.currentPlayer = player;
	}
	public void removePlayer(Player player) {
		if (this.currentPlayer == player) {
			this.currentPlayer = this.getPlayers().length > 1 ? this.info.getKeyAfter(player) : null;
			this.currentScore = 0;
		}
		this.info.remove(player);
		if (this.targetPlayer == player) {
			Player highest = this.getScores().entrySet().stream().max((score1,score2) -> score1.getValue()-score2.getValue()).get().getKey();
			if (this.getScores().get(highest) > DEFAULT_TARGET) {
				this.targetPlayer = highest;
				this.targetScore = this.getScores().get(highest);
			}
		}
	}
	public void giveChips(Player player, int chips) {
		if (chips < 0) LOGGER.info("Cannot give a player negative chips!");
		else this.info.get(player).put(PlayerInfoType.CHIPS, this.getChips().get(player)+chips);
	}
	public int takeChips(Player player, int chips) {
		int taken = 0;
		if (chips < 0) LOGGER.info("Cannot give a player negative chips!");
		else {
			taken = Math.min(chips, this.getChips().get(player));
			this.info.get(player).put(PlayerInfoType.CHIPS, this.getChips().get(player)-taken);
		}
		return taken;
	}
	public Player getTargetPlayer() {return this.targetPlayer;}
	public int getTargetScore() {return this.targetScore;}
	public Player getCurrentPlayer() {return this.currentPlayer;}
	public int getCurrentScore() {return this.currentScore;}
	public int getKitty() {return this.kitty;}
	public boolean isActive() {return this.isActive;}
	public int getGameNum() {return this.gameNum;}
	public boolean setActive(boolean active) {
		return this.isActive = active && this.currentPlayer!=null;
	}
	public boolean setUpTurn() {
		while (this.isActive && this.currentPlayer instanceof BotPlayer) {
			String action = ((BotPlayer)this.currentPlayer).act(this);
			if (action.equalsIgnoreCase("Roll")) this.actRoll();
			else if (action.equalsIgnoreCase("End")) this.actEnd();
			else LOGGER.warning("Bot player returned unexpected action. Doing nothing instead.");
		}
		return this.isActive;
	}
	public void actRoll() {
		int value = this.dice.roll();
		RollType type = RollType.find(this.dice);
		this.stats.addRoll(this.gameNum,this.currentPlayer, type, value);
		String message = this.currentPlayer.getName() + " rolled " + this.dice.toString() + " (" + type + ")" + "!";
		LOGGER.info(message);
		this.processRoll(type, value);
	}
	private void processRoll(RollType type, int value) {
		if (type.isTurnEnded()) {
			String message = this.currentPlayer.getName() + " is forced to end their turn thanks to their (" + type + ") roll.";
			LOGGER.info(message);
			this.processEnd(type, value);
		}
		else {
			LOGGER.info(this.currentPlayer.getName() + "'s current total for this turn is now " + this.currentScore + ", which would bring them to an overall score of " + (this.getScores().get(this.currentPlayer)+this.currentScore) + ".");
			this.currentScore += value;
		}
	}
	public void actEnd() {
		this.stats.addEnd(this.gameNum, this.currentPlayer);
		LOGGER.info(this.currentPlayer.getName() + " decided to end their turn having accumulated " + this.currentScore + " extra points, for a total of " + (this.getScores().get(this.currentPlayer)+this.currentScore) + " points!");
		this.processEnd(RollType.find(this.dice),this.dice.getValue());
	}
	private void processEnd(RollType type, int value) {
		this.info.get(this.currentPlayer).put(PlayerInfoType.SCORE, (type.isGameScoreLost() ? 0 : this.getScores().get(this.currentPlayer)) + (type.isTurnScoreLost() ? 0 : this.currentScore));
		LOGGER.info("Thanks to their (" + type + ") roll, " + this.currentPlayer.getName()
				+ (type.getChipCost() > 0 ? " must pay " + type.getChipCost() + " chips to the kitty" : " does not have to pay any chips") + ","
				+ (type.isTurnScoreLost() ? " lost their turn score" : " earned " + this.currentScore + " point this turn") + ","
				+ (type.isGameScoreLost() ? " lost their game score" : " retained their game score") + ","
				+ " and their total score is now " + this.getScores().get(this.currentPlayer) + ".");
		this.currentScore = 0;
		this.kitty += Math.min(type.getChipCost(), this.getChips().get(this.currentPlayer));
		this.info.get(this.currentPlayer).put(PlayerInfoType.CHIPS,this.getChips().get(this.currentPlayer)-Math.min(type.getChipCost(), this.getChips().get(this.currentPlayer)));
		LOGGER.info("The kitty now has " + this.getKitty() + " chips.");
		if (this.getScores().get(this.currentPlayer) > this.targetScore) { // target set
			LOGGER.info(this.currentPlayer.getName() + " has passed the target score of " + this.targetScore + "! They have become the target player and set the new target at " + this.getScores().get(this.currentPlayer));
			this.targetPlayer = this.currentPlayer;
			this.targetScore = this.getScores().get(this.targetPlayer);
		}
		LOGGER.info(this.currentPlayer.getName() + " passed the dice to " + (this.currentPlayer = this.getScores().getKeyAfter(this.currentPlayer)).getName() + ".");
		if (this.currentPlayer == this.targetPlayer) { // game over
			LOGGER.info(this.targetPlayer.getName() + " won game " + this.gameNum + ", earning " + this.kitty + " chips from the kitty!");
			this.info.get(this.targetPlayer).put(PlayerInfoType.CHIPS, this.getChips().get(this.targetPlayer)+this.kitty);
			this.kitty = 0;
			this.targetScore = DEFAULT_TARGET;
			this.targetPlayer = null;
			this.gameNum++;
			for (Player player : this.info.keySet()) {
				if (this.getChips().get(player) == 0) {
					LOGGER.info(player.getName() + " is out of chips! They lasted " + this.getStates().get(player) + " games, but have lost the match and will not continue to play.");
					this.info.get(player).put(PlayerInfoType.STATE, -1);
				}
				else {
					LOGGER.info(player.getName() + " has remaining chips and will continue to play! They will not be included in the next game.");
					this.info.get(player).put(PlayerInfoType.STATE, this.getStates().get(player)+1);
				}
			}
			LOGGER.fine("There are " + this.getPlayersStillIn().length + " players remaining in the game.");
			if (this.hasWinner()) {
				LOGGER.info(this.targetPlayer.getName() + " won the match, having accumulated all " + this.getChips().get(this.targetPlayer) + " chips!");
				LOGGER.info("The match lasted " + this.gameNum + " games!");
				this.isActive = false;
			}
		}
	}
	public static boolean save(Game game, File file) {
		boolean success = true;
		BufferedWriter writer = null;
		try {
			LOGGER.info("Attempting to save game to \"" + file + "\"...");
			if (file.getParentFile() != null) file.getParentFile().mkdir();
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(game.dice.flatten());
			for (Player player : game.info.keySet())
				writer.write(String.join(SEPARATOR, new String[]{
					"\nPlayer",
					player.getClass().getName() + (player instanceof BotPlayer ? "[" + ((BotPlayer)player).getThreshold() + "]" : ""),
					player.getUUID().toString(),
					player.getName(),
					game.getStates().get(player).toString(),
					game.getChips().get(player).toString(),
					game.getScores().get(player).toString()
				}));
			for (Object param : new Object[] {
				game.targetPlayer == null ? "null" : game.targetPlayer.getUUID().toString(),
				game.targetScore,
				game.currentPlayer == null ? "null" : game.currentPlayer.getUUID().toString(),
				game.currentScore,
				game.kitty,
				game.isActive,
				game.gameNum
			}) writer.write(String.join(SEPARATOR, new String[] {"\n"+param}));
			writer.write("\n"+game.stats.printHistory(SEPARATOR));
			LOGGER.info("Successfully saved game to \"" + file + "\"!");
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			success = false;
		} finally {
			try {writer.close();} catch (IOException e) {LOGGER.severe(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));}
		}
		return success;
	}
	public static Game load(File file) {
		Game game = null;
		BufferedReader reader = null;
		try {
			Dice dice = null;
			String[] parts;
			LOGGER.info("Attempting to load game from file \"" + file + "\"...");
			reader = new BufferedReader(new FileReader(file));
			// dice
			parts = reader.readLine().split(" ");
			dice = (Dice) Class.forName(parts[0]).getConstructor().newInstance(parts.length > 1 ? (Die[]) Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)).stream().map(die -> {
				try {
					return (Die) Class.forName(die.substring(0,die.indexOf('['))).getConstructor().newInstance((int[]) Arrays.asList(die.substring(die.indexOf('[')+1,die.length()-1).split(",")).stream().mapToInt(num -> Integer.parseInt(num)).toArray());
				} catch (Exception e) {
					LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
					return null;
				}
			}).toArray() : new Object[] {}); // i want to personally apologize for this line.
			game = new Game(dice);
			// players
			while ((parts = reader.readLine().split(SEPARATOR))!=null && parts[0].equals("Player")) {
				Player player = (Player) Class.forName(parts[1].substring(0, parts[1].contains("[") ? parts[1].indexOf("[") : parts[1].length())).getConstructor().newInstance(parts[3],UUID.fromString(parts[2])); 
				game.addPlayer(player);
				game.info.get(player).put(PlayerInfoType.STATE, Integer.parseInt(parts[4]));
				game.info.get(player).put(PlayerInfoType.CHIPS, Integer.parseInt(parts[5]));
				game.info.get(player).put(PlayerInfoType.SCORE, Integer.parseInt(parts[6]));
			}
			// meta
			game.targetPlayer = (parts = reader.readLine().split(SEPARATOR))!=null && !parts[0].equals("null") ? game.getPlayer(UUID.fromString(parts[0])) : null;
			game.targetScore = (parts = reader.readLine().split(SEPARATOR))!=null ? Integer.parseInt(parts[0]) : DEFAULT_TARGET;
			game.currentPlayer = (parts = reader.readLine().split(SEPARATOR))!=null && !parts[0].equals("null") ? game.getPlayer(UUID.fromString(parts[0])) : null;
			game.currentScore = (parts = reader.readLine().split(SEPARATOR))!=null ? Integer.parseInt(parts[0]) : 0;
			game.kitty = (parts = reader.readLine().split(SEPARATOR))!=null ? Integer.parseInt(parts[0]) : 0;
			game.isActive = (parts = reader.readLine().split(SEPARATOR))!=null ? Boolean.parseBoolean(parts[0]) : false;
			game.gameNum = (parts = reader.readLine().split(SEPARATOR))!=null ? Integer.parseInt(parts[0]) : 0;
			while ((parts = reader.readLine().split(SEPARATOR))!=null) {
				if (parts[0].equals("Roll")) game.stats.addRoll(Integer.parseInt(parts[1]), game.getPlayer(UUID.fromString(parts[0])), RollType.valueOf(parts[3]), Integer.parseInt(parts[4]));
				else if (parts[0].equals("End")) game.stats.addEnd(Integer.parseInt(parts[1]), game.getPlayer(UUID.fromString(parts[0])));
				else game = null;
			}
			LOGGER.info("Game loaded successfully!");
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			game = null;
		} finally {
			try {reader.close();} catch (IOException e) {LOGGER.severe(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));}
		}
		return game;
	}
}
