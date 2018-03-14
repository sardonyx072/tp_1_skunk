package main;

import java.io.File;
import java.io.IOException;
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
	private static final int NAME_LENGTH_LONG = 32, NAME_LENGTH_SHORT = 8, GAME_LENGTH = 4;
	private static final String DEFAULT_SAVE_LOCATION = "./sav/", SAVE_EXTENSION = ".sav";
	private static final String DEFAULT_INFO = "Choose an option.";
	private static final Dice dice = new StandardDice();
	private Scanner in;
	private boolean play;
	private boolean quit;
	private String info;
	private List<Player> players;
	
	//TODO play can be replaced with this.game==null
	
	public CommandLineClient() {
		this.in = new Scanner(System.in);
		this.play = false;
		this.quit = false;
		this.info = DEFAULT_INFO;
		this.players = new ArrayList<Player>();
	}
	private String promptGetString(String prompt) {
		LOGGER.finest("Prompt to get string");
		System.out.print(prompt);
		try {
			String inStr = this.in.nextLine();
			LOGGER.finest("Raw input: " + inStr);
			if (inStr != null && !inStr.equals("")) return inStr;
			else {
				this.info = "Invalid input";
				LOGGER.warning("invalid input in string prompt, return empty string instead");
				return "";
			}
		} catch (Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			return "";
		}
	}
	private int promptGetInt(String prompt) {
		LOGGER.finest("Prompt to get int");
		try {
			return Integer.parseInt(this.promptGetString(prompt));
		} catch (Exception e) {
			e.printStackTrace();
			this.info = "Invalid input";
			LOGGER.warning("invalid input in int prompt, returning -1 instead");
			return -1;
		}
	}
	private boolean promptGetConfirm(String prompt) {
		LOGGER.finest("Prompt to get confirmation (y/n)");
		String inStr = this.promptGetString(prompt);
		LOGGER.finest("Raw input: " + inStr);
		if (inStr != null && !inStr.equals("") && inStr.length() == 1 && (inStr.equalsIgnoreCase("y") || inStr.equalsIgnoreCase("n"))) return inStr.equalsIgnoreCase("y");
		else {
			LOGGER.warning("invalid input in confirmation prompt, returning false instead");
			return false;
		}
	}
	public void run() {
		while(!this.quit) {
			while(!this.quit) {
				this.update();
				if (!this.quit)
					this.getInput();
			}
			if (this.game!=null && this.game.isEnded()) {
				System.out.println(this.game.getCurrentPlayer().getName() + " is the Winner!");
				if (this.promptGetConfirm("Play again? [y/n]: ")) {
					this.quit = false;
					this.play = false;
					this.game = null;
				}
			}
			else if(this.game!=null && !this.game.isEnded()) {
				if (this.promptGetConfirm("Play a game? [y/n]: ")) {
					this.quit = false;
					this.play = false;
					this.game = null;
				}
			}
		}
		try {
			this.in.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
		}
	}
	public void update() {
		LOGGER.entering(this.getClass().getName(), "update");
		//clear screen
		try {
			System.out.println("================================================================================================================================================");
			final String os = System.getProperty("os.name");
			Runtime.getRuntime().exec(os.contains("Windows") ? "cls" : "clear");
			//System.out.print("\033[H\033[2J");
			//System.out.flush();
		} catch (final Exception e) {
			LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
			this.info = this.info + " " + "Also, Could not clear the screen";
		}
		//print to console
		StringBuilder result = new StringBuilder();
		if (!this.play) {
			result.append("Players:");
			if (this.players.size() == 0) result.append("\n <none>");
			else {
				result.append("\n 0: All players");
				for (int i = 0; i < this.players.size(); i++) result.append("\n " + (i+1) + ": " + String.format("%-" + NAME_LENGTH_LONG +"s",this.players.get(i).getName()) + " " + this.players.get(i).getChips());
			}
		}
		else if (!this.game.isEnded()) {
			this.quit = this.game.setUpTurn();
			result.append("game " + String.format("%" + GAME_LENGTH + "s", this.game.getNumGames()) + ": ");
			for (Player player : this.game.getScores().keySet())
				result.append(String.format((player == this.game.getCurrentPlayer() ? "{%s}" : " %s "), String.format("[%-" + NAME_LENGTH_SHORT + "s]", player.getName().substring(0, Math.min(NAME_LENGTH_SHORT, player.getName().length())))));
			result.append("\n");
			result.append(String.format("%" + (GAME_LENGTH+5) + "s: ","chips"));
			for (Player player : this.game.getScores().keySet())
				result.append(String.format((player == this.game.getCurrentPlayer() ? "{%s}" : " %s "), String.format("[%-" + NAME_LENGTH_SHORT + "s]", player.getChips())));
			result.append("\n");
			result.append(String.format("%" + (GAME_LENGTH+5) + "s: ","score"));
			for (Player player : this.game.getScores().keySet())
				result.append(String.format((player == this.game.getCurrentPlayer() ? "{%s}" : " %s "), String.format("[%-" + NAME_LENGTH_SHORT + "s]", this.game.getScores().get(player))));
			result.append("\n");
			result.append(String.format("%" + (GAME_LENGTH+5) + "s: ","turn"));
			for (Player player : this.game.getScores().keySet())
				if (this.game.getCurrentPlayer() == player) result.append(String.format((player == this.game.getCurrentPlayer() ? "{%s}" : " %s "), String.format("[%-8s]", this.game.getScores().get(player)+this.game.getCurrentTurnScore())));
				else if (this.game.getTargetPlayer() == player) result.append(String.format(" %s ", String.format("#%-" + NAME_LENGTH_SHORT + "s#", "TARGET").replace(' ', '#')));
				else result.append(String.format(" %s ", String.format(" %-" + NAME_LENGTH_SHORT + "s ", "")));
		}
		else {
			this.quit = true;
		}
		System.out.println(result.toString());
		LOGGER.exiting(this.getClass().getName(), "update");
	}
	public void getInput() {
		LOGGER.entering(this.getClass().getName(), "getInput");
		StringBuilder result = new StringBuilder();
		String inStr = "";
		int inInt = -1;
		File[] files = new File(DEFAULT_SAVE_LOCATION).listFiles();
		if (files==null) files = new File[] {};
		LOGGER.finer("List of files found in save location: " + Arrays.toString(files));
		System.out.println("INFO: " + this.info);
		if (!this.play) {
			LOGGER.entering(this.getClass().getName(), "getInput-preplay");
			System.out.println("Options: [0: quit] [1: load] [2: start over] [3: add player] [4: remove player] [5: reset chips] [6: give chips] [7: take chips] [8: start game]");
			inStr = this.promptGetString("Choose Option: ");
			LOGGER.info("Input selection: " + inStr);
			switch (inStr) {
			case "0": //quit
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": quit]");
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) this.quit = true;
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [quit]");
				break;
			case "1": //load
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": load]");
				result.append("Saved game files:");
				result.append("\n 0: Enter file...");
				for (int i = 0; i < files.length; i++)
					if (files[i].getName().substring(files[i].getName().lastIndexOf('.'), files[i].getName().length()).equals(SAVE_EXTENSION))
						result.append("\n " + (i+1) + ": " + DEFAULT_SAVE_LOCATION + files[i].getName());
				result.append("\nChoose a saved game file to load: ");
				inInt = this.promptGetInt(result.toString());
				if (0 <= inInt && inInt <= files.length) {
					File file = (inInt == 0 ? new File(this.promptGetString("Enter file to load: ")) : files[inInt-1]);
					if (file.exists() && file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length()).equals(SAVE_EXTENSION)) {
						try {
							this.game = Game.load(file.getAbsolutePath());
							this.play = true;
						} catch (Exception e) {
							LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
							this.info = "Something went wrong loading the file.";
						}
					}
					else {this.info = "Invalid file entered. Please enter a valid file.";}
				}
				else {this.info = "Invalid save file selection. Please enter a valid selection.";}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [load]");
				break;
			case "2": //start over
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": start over]");
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) {
					this.players = new ArrayList<Player>();
					this.info = "All player info cleared.";
				}
				else {this.info = DEFAULT_INFO;}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [start over]");
				break;
			case "3": //add player
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": add player]");
				if (this.players.size() < Game.getMaxPlayers()) {
					result.append("Player type:");
					result.append("\n 0: Human player");
					result.append("\n 1: Simple Bot");
					result.append("\nEnter player type: ");
					inInt = this.promptGetInt(result.toString());
					switch (inInt) {
					case 0:
						inStr = this.promptGetString("Enter player name: ");
						if (inStr.length() == 0 || inStr.length() > NAME_LENGTH_LONG) this.info = "Invalid name length. Names must contain 1-" + NAME_LENGTH_LONG + " characters.";
						else {
							this.players.add(new Player(inStr,0));
							this.info = DEFAULT_INFO;
						}
						break;
					case 1:
						this.players.add(new SimpleBotPlayer(0,15));
						break;
					default:
						this.info = "Invalid player type selection";
						break;
					}
				}
				else {this.info = "Too many players.";}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [add player]");
				break;
			case "4": //remove player
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": remove player]");
				inInt = this.promptGetInt("Enter player number: ");
				if (inInt < 0 || inInt > this.players.size()) this.info = "Not a valid player number. Please enter a valid player number.";
				else {
					if (this.promptGetConfirm("Are you sure? [y/n]: ")) {
						if (inInt==0) this.players = new ArrayList<Player>();
						else {this.players.remove(inInt-1);}
						this.info = "Player(s) removed.";
					}
					else {this.info = DEFAULT_INFO;}
				}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [remove player]");
				break;
			case "5": //reset chips
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": reset chips]");
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) {
					this.players.stream().forEach(player -> player.takeChips(player.getChips()));
					this.info = "Chips reset.";
				}
				else {this.info = DEFAULT_INFO;}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [reset chips]");
				break;
			case "6": //give chips
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": give chips]");
				int iplayergive = this.promptGetInt("Enter player number: ");
				if (iplayergive < 0 || iplayergive > this.players.size()) this.info = "Not a valid player number. Please enter a valid player number.";
				else {
					inInt = this.promptGetInt("Enter number of chips to give: ");
					if (inInt > Game.getMaxStartingChips() || (iplayergive != 0 && this.players.get(iplayergive).getChips()+inInt > Game.getMaxStartingChips())) {
						this.info = "Cannot set starting chips this high.";
					}
					else if (inInt > 0) {
						if (iplayergive == 0)
							for (Player player : this.players)
								player.giveChips(inInt);
						else {this.players.get(iplayergive-1).giveChips(inInt);}
					this.info = "Chips given.";
					}
				}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [give chips]");
				break;
			case "7": //take chips
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": take chips]");
				int iplayertake = this.promptGetInt("Enter player number: ");
				if (iplayertake < 0 || iplayertake > this.players.size()) this.info = "Not a valid player number. Please enter a valid player number.";
				else {
					inInt = this.promptGetInt("Enter number of chips to take: ");
					if (inInt > 0) {
						if (iplayertake == 0)
							for (Player player : this.players)
								player.takeChips(inInt);
						else {this.players.get(iplayertake-1).takeChips(inInt);}
						this.info = "Chips taken.";
					}
				}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [take chips]");
				break;
			case "8": //start game
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": start game]");
				if (players.size() < 2) this.info = "Not enough players to start game. At least two players are required.";
				else if (players.stream().mapToInt(player -> player.getChips()).sum() == 0) this.info = "There must be at least one chip in play. Give the players some chips!";
				else {
					try {
						this.game = new Game(this.players.toArray(new Player[this.players.size()]), dice);
						this.info = "Game successfully started.";
						this.play = true;
					} catch (SecurityException | IOException e) {
						LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
						this.info = "Something went wrong trying to start the game.";
					}
				}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [start game]");
				break;
			case "debug": //quick start game with defaults
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": debug]");
				this.players = new ArrayList<Player>();
				this.players.add(new Player("Aaron",50));
				this.players.add(new Player("Billy",50));
				this.players.add(new Player("Chuck",50));
				this.players.add(new SimpleBotPlayer(50,10));
				try {
					this.game = new Game(this.players.toArray(new Player[this.players.size()]), dice);
					this.info = "Game successfully started.";
					this.play = true;
				} catch (SecurityException | IOException e) {
					LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
					this.info = "Something went wrong trying to start the game.";
				}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [debug]");
				break;
			case "bots": //quick start game with all bots
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [" + inStr + ": bots]");
				this.players = new ArrayList<Player>();
				this.players.add(new SimpleBotPlayer(50,10));
				this.players.add(new SimpleBotPlayer(50,10));
				this.players.add(new SimpleBotPlayer(50,10));
				this.players.add(new SimpleBotPlayer(50,10));
				this.players.add(new SimpleBotPlayer(50,10));
				try {
					this.game = new Game(this.players.toArray(new Player[this.players.size()]), dice);
					this.info = "Game successfully started.";
					this.play = true;
				} catch (SecurityException | IOException e) {
					LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
					this.info = "Something went wrong trying to start the game.";
				}
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [bots]");
				break;
			default:
				LOGGER.entering(this.getClass().getName(), "getInput-preplay: [default]");
				this.info = "Invalid option.";
				LOGGER.exiting(this.getClass().getName(), "getInput-preplay: [default]");
				break;
			}
			LOGGER.exiting(this.getClass().getName(), "getInput-preplay");
		}
		else {
			LOGGER.entering(this.getClass().getName(), "getInput-inplay");
			System.out.println("Options: [0: quit] [1: load] [2: save] [3: roll] [4: end turn]");
			inInt = this.promptGetInt("Choose option: ");
			LOGGER.info("Input selection: " + inInt);
			switch (inInt) {
			case 0: //quit
				LOGGER.entering(this.getClass().getName(), "getInput-inplay: [" + inInt + ": quit]");
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) this.quit = true;
				LOGGER.exiting(this.getClass().getName(), "getInput-inplay: [quit]");
				break;
			case 1: //load
				LOGGER.entering(this.getClass().getName(), "getInput-inplay: [" + inInt + ": load]");
				if (this.promptGetConfirm("Are you sure? All unsaved progress will be lost. [y/n]: ")) {
					result.append("Saved game files:");
					result.append("\n 0: Enter file...");
					for (int i = 0; i < files.length; i++)
						if (files[i].getName().substring(files[i].getName().lastIndexOf('.'), files[i].getName().length()).equals(SAVE_EXTENSION))
							result.append("\n " + (i+1) + ": " + DEFAULT_SAVE_LOCATION + files[i].getName());
					result.append("\nChoose a saved game file to load: ");
					inInt = this.promptGetInt(result.toString());
					if (0 <= inInt && inInt <= files.length) {
						File file = (inInt == 0 ? new File(this.promptGetString("Enter file to load: ")) : files[inInt-1]);
						if (file.exists() && file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length()).equals(SAVE_EXTENSION)) {
							try {
								this.game = Game.load(file.getAbsolutePath());
								this.play = true;
							} catch (Exception e) {
								LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
								this.info = "Something went wrong loading the file.";
							}
						}
						else {this.info = "Invalid file entered. Please enter a valid file.";}
					}
					else {this.info = "Invalid save file selection. Please enter a valid selection.";}
				}
				else {this.info = DEFAULT_INFO;}
				LOGGER.exiting(this.getClass().getName(), "getInput-inplay: [load]");
				break;
			case 2: //save
				LOGGER.entering(this.getClass().getName(), "getInput-inplay: [" + inInt + ": save]");
				result.append("Saved games:");
				result.append("\n 0: Enter file...");
				for (int i = 0; i < files.length; i++)
					if (files[i].getName().substring(files[i].getName().lastIndexOf('.'), files[i].getName().length()).equals(SAVE_EXTENSION))
						result.append("\n " + (i+1) + ": " + DEFAULT_SAVE_LOCATION + files[i].getName());
				result.append("\nChoose a file to save to: ");
				inInt = this.promptGetInt(result.toString());
				if (0 <= inInt && inInt <= files.length) {
					File file = null;
					if (inInt == 0) {
						inStr = this.promptGetString("Enter save file name: ");
						if (inStr.lastIndexOf('/') <= 0) inStr = DEFAULT_SAVE_LOCATION + inStr;
						if (inStr.lastIndexOf('.') < inStr.lastIndexOf('/')) inStr = inStr + SAVE_EXTENSION;
						file = new File(inStr);
					}
					else {file = files[inInt-1];}
					if ((!file.exists()) || (file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length()).equals(SAVE_EXTENSION) && this.promptGetConfirm("File already exists. Overwrite? [y/n]: "))) {
						try {
							Game.save(this.game, file.getAbsolutePath());
							this.info  = "Game saved to " + file.getAbsolutePath();
						} catch (Exception e) {
							LOGGER.warning(e.getMessage() + Arrays.asList(e.getStackTrace()).stream().map(elem -> elem.toString()).reduce("",(out,elem) -> out+"\r\n\t"+elem));
							this.info = "Something went wrong saving the file.";
						}
					}
				}
				else {this.info = "Invalid save file selection. Please enter a valid selection.";}
				LOGGER.exiting(this.getClass().getName(), "getInput-inplay: [: save]");
				break;
			case 3: //roll
				LOGGER.entering(this.getClass().getName(), "getInput-inplay: [" + inInt + ": roll]");
				this.actRoll();
				LOGGER.exiting(this.getClass().getName(), "getInput-inplay: [: roll]");
				break;
			case 4: //end turn
				LOGGER.entering(this.getClass().getName(), "getInput-inplay: [" + inInt + ": end]");
				this.actEnd();
				LOGGER.exiting(this.getClass().getName(), "getInput-inplay: [: end]");
				break;
			default:
				LOGGER.entering(this.getClass().getName(), "getInput-inplay: [default]");
				this.info = "Option invalid. Choose a valid option from the list below.";
				LOGGER.exiting(this.getClass().getName(), "getInput-inplay: [default]");
				break;
			}
			LOGGER.exiting(this.getClass().getName(), "getInput-inplay");
		}
		LOGGER.exiting(this.getClass().getName(), "getInput");
	}
	public static void main(String[] args) {
		CommandLineClient client = new CommandLineClient();
		client.run();
	}
}


//1SKUNK
//1SK1DC
//2SKUNK

/*
=====================================================
BASIC
=====================================================
game 0001: [Aaron   ] {[Billy   ]} [Chuck   ]
    chips: [12      ] {[12      ]} [12      ]
    score: [12      ] {[12      ]} [16      ]
     turn:            {[34      ]} #TARGET###

=====================================================
BASIC-UPDATED
=====================================================
game 0001: [All     ]  [Aaron   ] {[Billy   ]} [Chuck   ]
    chips: [        ]  [12      ] {[12      ]} [12      ] Kitty: 5
    score: [        ]  [12      ] {[12      ]} [16      ] Target: 100
     turn:                        {[34      ]} #TARGET###

=====================================================
DETAILED
=====================================================
game 0001: [All     ]  [Aaron   ] {[Billy   ]} [Chuck   ]
    chips: [        ]  [12      ] {[12      ]} [12      ] Kitty: 5
    score: [        ]  [12      ] {[12      ]} [16      ] Target: 100
     turn:                        {[34      ]} #TARGET###
     mean:  7
   median:  7
     mode:  6
    >=1SK:  33.33%
 */