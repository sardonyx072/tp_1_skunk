package main;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CommandLineClient extends Client {
	private static Logger LOGGER = null;
	static {
		try {
			LogManager.getLogManager().readConfiguration(CommandLineClient.class.getClassLoader().getResourceAsStream("main/resources/logging.properties"));
			File logdir = new File(LogManager.getLogManager().getProperty("java.util.logging.FileHandler.pattern")).getParentFile();
			if (logdir != null) logdir.mkdir();
			LOGGER = Logger.getLogger(CommandLineClient.class.getName());
		} catch (Exception e) {e.printStackTrace();}
	}
	private static final InputStream IPUT = System.in;
	private static final PrintStream OPUT = System.out;
	private static final String
			COLUMN_SEPARATOR = " ",
			PLAYER_CURRENT_INDICATOR = "->",
			PLAYER_TARGET_INDICATOR = "T#";
	private static final int
			COLUMN_SEPARATOR_WIDTH = COLUMN_SEPARATOR.length(),
			PLAYER_INDICATOR_WIDTH = Math.max(PLAYER_CURRENT_INDICATOR.length(), PLAYER_TARGET_INDICATOR.length()),
			PLAYER_NUM_WIDTH = Integer.toString(Game.getMaxPlayers()).length(),
			PLAYER_NAME_WIDTH = 32,
			PLAYER_CHIPS_WIDTH = Integer.toString(Game.getMaxPlayers()*Game.getMaxStartingChips()).length(),
			PLAYER_SCORE_WIDTH = Integer.toString(Integer.MAX_VALUE).length();
	private static final String DEFAULT_SAVE_LOCATION = "./sav/", SAVE_EXTENSION = ".skg";
	private static final String DEFAULT_INFO = "Choose an option.";
	private static final Dice DICE = new Dice(new Die[] {new RandomDie(new int[] {0,1,2,3,4}), new RandomDie(new int[] {5,6,7,8,9})});
	private static final int BOT_RISK_THRESHOLD = 15;
	private Scanner in;
	private boolean quit;
	private List<String> info;
	
	public CommandLineClient() {
		this.in = new Scanner(IPUT);
		this.quit = false;
		this.info = new ArrayList<String>();
		this.game = new Game(DICE);
	}
	private String promptGetString(String prompt) {
		final String FAIL = "";
		OPUT.print(prompt);
		try {
			String inStr = this.in.nextLine();
			LOGGER.finest("Raw input for expected String input: " + inStr);
			if (inStr != null && !inStr.equals("")) return inStr;
			else {
				this.info.add("Invalid input. Expected some text input.");
				return FAIL;
			}
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			return FAIL;
		}
	}
	private int promptGetInt(String prompt) {
		final int FAIL = -1;
		OPUT.print(prompt);
		try {
			String inStr = this.in.nextLine();
			LOGGER.finest("Raw input for expected int input: " + inStr);
			try {
				return Integer.parseInt(inStr);
			} catch (Exception e) {
				this.info.add("Invalid input. Expected number input.");
				return FAIL;
			}
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			return FAIL;
		}
	}
	private boolean promptGetConfirm(String prompt, String inStr) {
		final boolean FAIL = false;
		OPUT.print(prompt);
		try {
			inStr = inStr == null ? this.in.nextLine() : inStr;
			LOGGER.finest("Raw input for expected y/n input: " + inStr);
			if (inStr != null && !inStr.equals("") && inStr.length() == 1 && (inStr.equalsIgnoreCase("y") || inStr.equalsIgnoreCase("n"))) return inStr.equalsIgnoreCase("y");
			else {
				this.info.add("Invalid input. Expected y or n input.");
				return FAIL;
			}
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			return FAIL;
		}
	}
	private void save(String inStr) {
		File[] files = new File(DEFAULT_SAVE_LOCATION).listFiles();
		if (files==null) files = new File[] {};
		LOGGER.finer("List of files found in save location: " + Arrays.toString(files));
		File file = null;
		if (inStr == null) {
			OPUT.println("Saved game files:");
			OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + "0. Enter file...");
			for (int i = 0; i < files.length; i++)
				if (files[i].isFile() && files[i].getName().substring(files[i].getName().lastIndexOf('.')+1, files[i].getName().length()).equals(SAVE_EXTENSION.substring(1)))
					OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + (i+1) + ". " + DEFAULT_SAVE_LOCATION + files[i].getName());
			inStr = this.promptGetString("Choose save file: ");
		}
		try {
			int inInt = Integer.parseInt(inStr);
			if (0 <= inInt && inInt <= files.length) {
				file = (inInt == 0 ? new File (this.promptGetString("Enter filename: ")) : files[inInt-1]);
			} else this.info.add("Entered invalid file selection.");
		} catch (Exception e) {file = new File(inStr);}
		if (file.getParentFile() == null) file = new File(DEFAULT_SAVE_LOCATION + file.getPath());
		if (!file.getName().substring(file.getName().lastIndexOf('.')+1).equalsIgnoreCase(SAVE_EXTENSION.substring(1))) file = new File(file.getPath() + SAVE_EXTENSION);
		final boolean formatOK = file.getName().substring(file.getName().lastIndexOf('.')+1).equalsIgnoreCase(SAVE_EXTENSION.substring(1));
		if (!file.exists() || (formatOK && this.promptGetConfirm("File already exists. Overwrite? [y/n]: ",null))) {
			file.getParentFile().mkdir();
			if (Game.save(this.game,file)) this.info.add("Successfully saved to \"" + file + "\".");
			else this.info.add("Saving game to \"" + file + "\" failed!");
		}
		else if (!formatOK) this.info.add("Improper file extension.");
	}
	private void load(String inStr) {
		File[] files = new File(DEFAULT_SAVE_LOCATION).listFiles();
		if (files==null) files = new File[] {};
		LOGGER.finer("List of files found in save location: " + Arrays.toString(files));
		File file = null;
		Game game = null;
		if (inStr == null) {
			OPUT.println("Saved game files:");
			OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + "0. Enter file...");
			for (int i = 0; i < files.length; i++)
				if (files[i].isFile() && files[i].getName().substring(files[i].getName().lastIndexOf('.')+1).equals(SAVE_EXTENSION.substring(1)))
					OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + (i+1) + ". " + DEFAULT_SAVE_LOCATION + files[i].getName());
			inStr = this.promptGetString("Choose save file: ");
		}
		try {
			int inInt = Integer.parseInt(inStr);
			if (0 <= inInt && inInt <= files.length) {
				file = (inInt == 0 ? new File (this.promptGetString("Enter filename: ")) : files[inInt-1]);
			} else this.info.add("Entered invalid file selection.");
		} catch (Exception e) {file = new File(inStr);}
		if (file.exists() && file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length()).equals(SAVE_EXTENSION)) game = Game.load(file);
		else this.info.add("Could not find file or improper file extension.");
		if (game != null) this.game = game;
		else this.info.add("Could not load game.");
	}
	public void run() {
		while(!this.quit) {
			while(!this.quit) {
				this.update();
				if (!this.quit)
					this.getInput();
			}
		}
		try {
			this.in.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
		}
	}
	public void update() { //TODO print "LOST" instead of chips and score for players who ended a game with 0 chips
		LOGGER.entering(this.getClass().getName(), "update");
		this.game.setUpTurn();
		//clear screen
		try {
			OPUT.println("========================================================================");
			final String os = System.getProperty("os.name");
			Runtime.getRuntime().exec(os.contains("Windows") ? "cls" : "clear");
			//System.out.print("\033[H\033[2J");
			//System.out.flush();
		} catch (final Exception e) {LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));}
		//print to console
		OPUT.println("Game: " + this.game.getGameNum());
		OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + "Kitty: " + this.game.getKitty());
		OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + "Target: " + this.game.getTargetScore() + (this.game.getTargetPlayer() != null ? " (" + this.game.getTargetPlayer().getName() + ")" : ""));
		OPUT.println(
				String.format("%-" + (PLAYER_INDICATOR_WIDTH + PLAYER_NUM_WIDTH + 1 + COLUMN_SEPARATOR_WIDTH + PLAYER_NAME_WIDTH) + "s", "Players")
				+ COLUMN_SEPARATOR + String.format("%-" + PLAYER_CHIPS_WIDTH + "s", "Chips")
				+ COLUMN_SEPARATOR + String.format("%-" + PLAYER_SCORE_WIDTH + "s", "Score")
		);
		Player[] players = this.game.getPlayers();
		if (players.length == 0) OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + "<none>");
		else {
			OPUT.println(String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " ") + String.format("%"+PLAYER_NUM_WIDTH+"s", "0") + "." + COLUMN_SEPARATOR + String.format("%-"+PLAYER_NAME_WIDTH+"s", "All Players") + COLUMN_SEPARATOR + String.format("%-"+PLAYER_CHIPS_WIDTH+"s", "") + COLUMN_SEPARATOR + String.format("%-"+PLAYER_SCORE_WIDTH+"s", ""));
			for (int i = 0; i < players.length; i++)
				OPUT.println(
						// indicator
						(players[i] == this.game.getCurrentPlayer() ? String.format("%" + PLAYER_INDICATOR_WIDTH + "s", PLAYER_CURRENT_INDICATOR) : players[i] == this.game.getTargetPlayer() ? String.format("%" + PLAYER_INDICATOR_WIDTH + "s", PLAYER_TARGET_INDICATOR) : String.format("%" + PLAYER_INDICATOR_WIDTH + "s", " "))
						// name and chips
						+ String.format("%" + PLAYER_NUM_WIDTH + "s", (i+1)) + "." + COLUMN_SEPARATOR + String.format("%-"+PLAYER_NAME_WIDTH+"s", players[i].getName()) + COLUMN_SEPARATOR + String.format("%-"+PLAYER_CHIPS_WIDTH+"s", this.game.getChips().get(players[i]))
						// score
						+ COLUMN_SEPARATOR + String.format("%-"+PLAYER_SCORE_WIDTH+"s", this.game.getScores().get(players[i]) + (players[i] == this.game.getCurrentPlayer() ? "+" + this.game.getCurrentScore() + "=" + (this.game.getScores().get(players[i])+this.game.getCurrentScore()) : ""))
				);
		}
		LOGGER.exiting(this.getClass().getName(), "update");
	}
	public void getInput() {
		LOGGER.entering(this.getClass().getName(), "getInput");
		OPUT.println(
				this.game.isActive() ? "Options: [0:quit] [1:load] [2:save] [3:roll] [4:end turn]" // {back}
				: "Options: [0:quit] [1:load] [2:save] [3:add player] [4:edit name] [5:shift order] [6:remove player] [7:give chips] [8:take chips] [9:start game]"
		);
		if (this.info.size() == 0) this.info.add(DEFAULT_INFO);
		for (String i : this.info) OPUT.println("INFO: " + i);
		this.info.clear();
		String inStr = this.promptGetString("Enter Option: ");
		String[] args = inStr.toLowerCase().split(" ");
		LOGGER.finer("Input args while game is " + (this.game.isActive() ? "" : "in") + "active: " + Arrays.toString(args));
		// translate
		try {
			args[0] = (this.game.isActive() ? new String[] {"quit","load","save","roll","end"} : new String[] {"quit","load","save","add","edit","shift","remove","give","take","start"}) [Integer.parseInt(args[0])];
		} catch (Exception e) {}
		LOGGER.finer("Translated input args while game is " + (this.game.isActive() ? "" : "in") + "active: " + Arrays.toString(args));
		// decide
		switch(args[0]) {
		case "quit":
			LOGGER.finest("executing input [quit] for inactive game");
			if (this.promptGetConfirm("Are you sure? [y/n]: ", args.length >= 2 ? args[1] : null)) this.quit = true;
			break;
		case "load":
			LOGGER.finest("executing input [load] for inactive game");
			if (this.promptGetConfirm("All unsaved progress will be lost. Are you sure? [y/n]: ", null)) this.load(args.length >= 2 ? args[1] : null);
			break;
		case "save":
			LOGGER.finest("executing input [save] for inactive game");
			this.save(args.length >= 2 ? args[1] : null);
			break;
		case "add":
			LOGGER.finest("executing input [add player] for inactive game");
			if (args.length == 1) {
				OPUT.println("Player types:");
				OPUT.println("  " + "0. Human");
				OPUT.println("  " + "1. Simple Bot");
				args = this.promptGetString("Create what type of new player?: ").split(" ");
				switch (args[0].toLowerCase()) {
				case "0":
				case "human":
					this.game.addPlayer(new Player(args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Player"+(this.game.getPlayers().length+1)));
					break;
				case "1":
				case "simple":
				case "bot":
					this.game.addPlayer(new SimpleBotPlayer(BOT_RISK_THRESHOLD));
					break;
				default:
					this.info.add("Invalid player type.");
					break;
				}
			}
			if (args.length >= 2) {
				int inInt = Integer.parseInt(args[1]);
				try {
					for (int i = 0; i < inInt; i++) this.game.addPlayer(new Player("Player" + (this.game.getPlayers().length+1)));
				} catch (Exception e) {}
			}
			if (args.length >= 3) {
				int inInt = Integer.parseInt(args[2]);
				try {
					for (int i = 0; i < inInt; i++) this.game.addPlayer(new SimpleBotPlayer(BOT_RISK_THRESHOLD));
				} catch (Exception e) {}
			}
			break;
		case "edit":
			LOGGER.finest("executing input [edit name] for inactive game");
			try {
				this.game.getPlayers()[Integer.parseInt(args.length >= 2 ? args[1] : this.promptGetString("Edit which player's name?: "))-1].setName(args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : this.promptGetString("Enter new name: "));
			} catch (Exception e) {this.info.add("Invalid player selection.");}
			break;
		case "shift": //TODO convenience pass player num to start on second arg OR player num to shift on second arg and position on third arg
			LOGGER.finest("executing input [shift order] for inactive game");
			break;
		case "remove":
			LOGGER.finest("executing input [remove player] for inactive game");
			try {
				int inInt = args.length >= 2 ? Integer.parseInt(args[1]) : this.promptGetInt("Remove which player?: ");
				if (inInt == 0 && this.promptGetConfirm("Are you sure you want to delete all players? [y/n]: ", null)) this.game.getScores().clear();
				else if (inInt > 0 && this.promptGetConfirm("Are you sure you want to delete this player? [y/n]: ", null)) this.game.getScores().remove(this.game.getPlayers()[inInt-1]);
			} catch (Exception e) {this.info.add("Invalid player selection.");}
			break;
		case "give":
			LOGGER.finest("executing input [give chips] for inactive game");
			try {
				int inInt = args.length >= 2 ? Integer.parseInt(args[1]) : this.promptGetInt("Give chips to which player?: ");
				if (inInt == 0) {
					inInt = args.length >= 3 ? Integer.parseInt(args[2]) : this.promptGetInt("Give how many chips?: ");
					for (Player player : this.game.getPlayers()) this.game.giveChips(player, inInt);
				}
				else if (inInt > 0) this.game.giveChips(this.game.getPlayers()[inInt-1],args.length >= 3 ? Integer.parseInt(args[2]) : this.promptGetInt("Give how many chips?: "));
			} catch (Exception e) {this.info.add("Invalid player selection.");}
			break;
		case "take":
			LOGGER.finest("executing input [take chips] for inactive game");
			try {
				int inInt = args.length >= 2 ? Integer.parseInt(args[1]) : this.promptGetInt("Take chips from which player?: ");
				if (inInt == 0) for (Player player : this.game.getPlayers()) this.game.takeChips(player,args.length >= 3 ? Integer.parseInt(args[2]) : this.promptGetInt("Take how many chips?: "));
				else if (inInt > 0) this.game.takeChips(this.game.getPlayers()[inInt-1],args.length >= 3 ? Integer.parseInt(args[2]) : this.promptGetInt("Take how many chips?: "));
			} catch (Exception e) {this.info.add("Invalid player selection.");}
			break;
		case "start":
			LOGGER.finest("executing input [start game] for inactive game");
			if (args.length > 1)
				this.game.getScores().clear();
			if (args.length >= 2) {
				int inInt = Integer.parseInt(args[1]);
				try {
					for (int i = 0; i < inInt; i++) {
						Player p = new Player("Player" + this.game.getPlayers().length+1);
						this.game.addPlayer(p);
						this.game.giveChips(p, 50);
					}
				} catch (Exception e) {}
			}
			if (args.length >= 3) {
				int inInt = Integer.parseInt(args[2]);
				try {
					for (int i = 0; i < inInt; i++) this.game.addPlayer(new SimpleBotPlayer(BOT_RISK_THRESHOLD));
				} catch (Exception e) {}
			}
			if (!this.game.setActive(true)) this.info.add("Could not start the game. Check settings and try again.");
			break;
		case "roll":
			LOGGER.finest("executing input [roll] for active game");
			this.actRoll();
			break;
		case "end":
			LOGGER.finest("executing input [end turn] for active game");
			this.actEnd();
			break;
		case "back":
			LOGGER.finest("executing input [back] for active game");
			this.game.setActive(false);
		default:
			LOGGER.finest("invalid translated input");
			this.info.add("Invalid input.");
			break;
		}
	}
	public static void main(String[] args) {
		CommandLineClient client = new CommandLineClient();
		client.run();
	}
}

/*

========================================================================
Game: 0
  Kitty: 
  Target: 100 (Chuck)
Players:                               Chips            Score           
   0. All Players                      50               0
-> 1. Aaron                            50               0+4=4
   2. Billy                            50               0
T# 3. Chuck                            50               0
   4. David                            50               0
   5. Edgar                            50               0
   6. Frank                            50               0
   7. Grant                            50               0
   8. Harry                            50               0
   9. Irwin                            50               0
  10. James                            50               0
Options: [0:quit] [1:load] [2:save] [3:add player] [4:remove player] [5:give chips] [6:take chips] [7:start game] {move n m} {quick n m}
Options: [0:quit] [1:load] [2:save] [3:roll] [4:end turn] {back}
INFO: information!
Input> 

*/