package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Game {
	private static final int MAX_PLAYERS = 1023, MAX_STARTING_CHIPS_PER_PLAYER = 2097152;
	private static final int DEFAULT_TARGET = 100;
	private static final String SEPARATOR = "::";
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
	public static int getMaxPlayers() {return MAX_PLAYERS;}
	public static int getMaxStartingChips() {return MAX_STARTING_CHIPS_PER_PLAYER;}
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
			String action = ((BotPlayer)this.currentPlayer).act(this);
			if (action.equalsIgnoreCase("Roll")) this.actRoll();
			else if (action.equalsIgnoreCase("End")) this.actEnd();
			else LOGGER.warning("Bot player returned unexpected action. Doing nothing instead.");
		}
		return this.isEnded;
	}
	public void actRoll() {
		int value = this.dice.roll();
		RollType type = RollType.find(this.dice);
		this.stats.addRoll(this.numGamesThisMatch,this.currentPlayer, type, value);
		String message = this.currentPlayer.getName() + " rolled " + this.dice.toString() + " (" + type + ")" + "!";
		LOGGER.info(message);
		this.processRoll(type, value);
	}
	private void processRoll(RollType type, int value) {
		if (type.isTurnEnded()) {
			LOGGER.info(this.currentPlayer.getName() + " is forced to end their turn thanks to their (" + type + ") roll.");
			this.processEnd(type, value);
		}
		else {
			LOGGER.info(this.currentPlayer.getName() + "'s current total for this turn is now " + this.turnScore + ", which would bring them to an overall score of " + (this.scores.get(this.currentPlayer)+this.turnScore) + ".");
			this.turnScore += value;
		}
	}
	public void actEnd() {
		this.stats.addEnd(this.numGamesThisMatch, this.currentPlayer);
		LOGGER.info(this.currentPlayer.getName() + " decided to end their turn having accumulated " + this.turnScore + " extra points, for a total of " + (this.scores.get(this.currentPlayer)+this.turnScore) + " points!");
		this.processEnd(RollType.find(this.dice),this.dice.getValue());
	}
	private void processEnd(RollType type, int value) {
		if (type.isGameScoreLost()) {
			LOGGER.info(this.currentPlayer.getName() + " has lost their score for the game thanks to their (" + type + ") roll.");
			this.scores.put(this.currentPlayer, 0);
			LOGGER.info(this.currentPlayer.getName() + "'s total score is now " + this.scores.get(this.currentPlayer) + ".");
		}
		else if (type.isTurnScoreLost()) {
			LOGGER.info(this.currentPlayer.getName() + " has lost their score for the turn thanks to their (" + type + ") roll.");
			this.scores.put(this.currentPlayer, this.scores.get(this.currentPlayer));
			LOGGER.info(this.currentPlayer.getName() + "'s total score is now " + this.scores.get(this.currentPlayer) + ".");
		}
		else {
			LOGGER.info(this.currentPlayer.getName() + " is adding their turn score of " + this.turnScore + " to their total score.");
			this.scores.put(this.currentPlayer, this.scores.get(this.currentPlayer) + this.turnScore);
			LOGGER.info(this.currentPlayer.getName() + "'s total score is now " + this.scores.get(this.currentPlayer) + ".");
			if (this.scores.get(this.currentPlayer) > this.target) {
				LOGGER.info(this.currentPlayer.getName() + " has passed the target score of " + this.target + "! They have become the target player and set the new target at " + this.scores.get(this.currentPlayer));
				this.target = this.scores.get(this.currentPlayer);
				this.targetPlayer = this.currentPlayer;
			}
		}
		this.turnScore = 0;
		LOGGER.info(this.currentPlayer.getName() + " must pay a cost of " + type.getChipCost() + " chips this turn.");
		this.kitty += this.currentPlayer.takeChips(type.getChipCost());
		LOGGER.info(this.currentPlayer.getName() + " passed the dice to " + this.scores.getKeyAfter(this.currentPlayer).getName() + ".");
		this.currentPlayer = this.scores.getKeyAfter(this.currentPlayer);
		if (this.currentPlayer == this.targetPlayer) {
			this.targetPlayer.giveChips(this.kitty);
			LOGGER.info(this.targetPlayer.getName() + " won this game and " + this.kitty + " chips!");
			LOGGER.info("Resetting the target to " + DEFAULT_TARGET + ", kitty to 0.");
			this.kitty = 0;
			this.target = DEFAULT_TARGET;
			this.targetPlayer = null;
			CircularLinkedHashMap<Player,Integer> stillPlaying = new CircularLinkedHashMap<Player,Integer>();
			for (Player player : this.scores.keySet()) {
				if (player.getChips() > 0) {
					LOGGER.info(player.getName() + " has chips! They will be included in the next game.");
					stillPlaying.put(player,0);
				}
				else {LOGGER.info(player.getName() + " is out of chips! They will not be included in the next game.");}
			}
			this.scores = stillPlaying;
			LOGGER.fine("There are " + this.scores.size() + " players remaining in the game.");
			LOGGER.info("Game " + this.numGamesThisMatch + " is over.");
			this.numGamesThisMatch++;
			if (this.scores.keySet().stream().filter(player -> player.getChips() > 0).count() == 1) {
				LOGGER.info(this.currentPlayer.getName() + " won the match, having accumulated all " + this.currentPlayer.getChips() + " chips!");
				LOGGER.info("The match lasted " + this.numGamesThisMatch + " games!");
				this.isEnded = true;
			}
		}
	}
	public static void save(Game game, String file) {
		BufferedWriter writer = null;
		try {
			LOGGER.info("Attempting to save game to \"" + file + "\"...");
			File savedir = new File(file).getParentFile();
			if (savedir != null) savedir.mkdir();
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(":Players");
			for (Player player : game.scores.keySet())
				writer.write("\n" + player.getUUID() + SEPARATOR + player.getName() + SEPARATOR + player.getChips() + SEPARATOR + game.scores.get(player) + (player instanceof BotPlayer ? SEPARATOR + player.getClass().getName() + SEPARATOR + ((BotPlayer)player).getThreshold() : ""));
			LOGGER.fine("Wrote all players to save file");
			writer.write("\n:Game");
			writer.write("\nDice" + SEPARATOR + game.dice.flatten());
			LOGGER.fine("Wrote dice to save file");
			writer.write("\nGameNo" + SEPARATOR + game.numGamesThisMatch);
			LOGGER.fine("Wrote game no to save file");
			writer.write("\nKitty" + SEPARATOR + game.kitty);
			LOGGER.fine("Wrote kitty to save file");
			writer.write("\nTarget" + SEPARATOR + game.target);
			LOGGER.fine("Wrote target to save file");
			writer.write("\nTargetPlayer" + SEPARATOR + (game.targetPlayer != null ? game.targetPlayer.getUUID().toString() : "null"));
			LOGGER.fine("Wrote target player to save file");
			writer.write("\nCurrentPlayer" + SEPARATOR + game.currentPlayer.getUUID().toString());
			LOGGER.fine("Wrote current player to save file");
			writer.write("\nTurnScore" + SEPARATOR + game.turnScore);
			LOGGER.fine("Wrote turn score to save file");
			writer.write("\nisEnded" + SEPARATOR + game.isEnded);
			LOGGER.fine("Wrote is ended to save file");
			writer.write("\n"+game.stats.printHistory(SEPARATOR));
			LOGGER.fine("Wrote history to save file");
			LOGGER.info("Successfully saved game to \"" + file + "\"!");
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
		} finally {
			try {writer.close();} catch (IOException e) {LOGGER.severe(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));}
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
			LOGGER.fine("loaded players");
			// dice
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("Dice");
			dice = (Dice) Class.forName(parts[1]).getConstructor().newInstance(new Object[] {});
			LOGGER.fine("loaded dice");
			game = new Game(players.toArray(new Player[players.size()]),dice);
			for (int i = 0; i < players.size(); i++)
				game.scores.put(players.get(i), scores.get(i));
			// game no
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("GameNo");
			game.numGamesThisMatch = Integer.parseInt(parts[1]);
			LOGGER.fine("loaded game no");
			// kitty
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("Kitty");
			game.kitty = Integer.parseInt(parts[1]);
			LOGGER.fine("loaded kitty");
			// target
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("Target");
			game.target = Integer.parseInt(parts[1]);
			LOGGER.fine("loaded target");
			// target player
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("TargetPlayer");
			final String temp1 = parts[1];
			game.targetPlayer = (temp1.equals("null") ? null : (Player) game.scores.keySet().stream().filter(player -> player.getUUID().equals(UUID.fromString(temp1))).toArray()[0]);
			LOGGER.fine("loaded targetplayer");
			// current player
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("CurrentPlayer");
			final String temp2 = parts[1];
			game.currentPlayer = (Player) game.scores.keySet().stream().filter(player -> player.getUUID().equals(UUID.fromString(temp2))).toArray()[0];
			LOGGER.fine("loaded currentplayer");
			// turn score
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("TurnScore");
			game.turnScore = Integer.parseInt(parts[1]);
			LOGGER.fine("loaded turnscore");
			// is ended
			in = reader.readLine();
			parts = in.split(SEPARATOR);
			assert parts[0].equals("isEnded");
			game.isEnded = Boolean.parseBoolean(parts[1]);
			LOGGER.fine("loaded isended");
			// history
			in = reader.readLine();
			assert in.equals(":Opts");
			while (in != null && (in = reader.readLine()) != null) {
				parts = in.split(SEPARATOR);
				LOGGER.fine(Arrays.toString(parts));
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
			LOGGER.fine("loaded history");
			LOGGER.info("Game loaded successfully!");
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
		} finally {
			try {reader.close();} catch (IOException e) {LOGGER.severe(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));}
		}
		return game;
	}
}
