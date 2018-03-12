package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineClient extends Client {
	private static final int NAME_LENGTH_LONG = 32, NAME_LENGTH_SHORT = 8, GAME_LENGTH = 4;
	private static final String DEFAULT_SAVE_LOCATION = "./sav/", SAVE_EXTENSION = ".sav";
	private static final String DEFAULT_INFO = "Choose an option.";
	private static final Dice dice = new StandardDice();
	private Scanner in;
	private boolean play;
	private boolean quit;
	private String info;
	private List<Player> players;
	
	public CommandLineClient() {
		this.in = new Scanner(System.in);
		this.play = false;
		this.quit = false;
		this.info = DEFAULT_INFO;
		this.players = new ArrayList<Player>();
	}
	private String promptGetString(String prompt) {
		System.out.print(prompt);
		String inStr = this.in.nextLine();
		if (inStr != null && !inStr.equals("")) return inStr;
		else {
			this.info = "Invalid input";
			return "";
		}
	}
	private int promptGetInt(String prompt) {
		try {
			return Integer.parseInt(this.promptGetString(prompt));
		} catch (Exception e) {
			e.printStackTrace();
			this.info = "Invalid input";
			return -1;
		}
	}
	private boolean promptGetConfirm(String prompt) {
		String inStr = this.promptGetString(prompt);
		if (inStr != null && !inStr.equals("") && inStr.length() == 1 && (inStr.equalsIgnoreCase("y") || inStr.equalsIgnoreCase("n"))) return inStr.equalsIgnoreCase("y");
		else {return false;}
	}
	public void run() {
		while(!this.quit) {
			this.update();
			this.getInput();
		}
	}
	public void update() {
//		try {
//			final String os = System.getProperty("os.name");
//			Runtime.getRuntime().exec(os.contains("Windows") ? "cls" : "clear");
//		} catch (final Exception e) {
//			e.printStackTrace();
//			this.info = this.info + " " + "Also, Could not clear the screen";
//		}
		StringBuilder result = new StringBuilder();
		if (!this.play) {
			result.append("Players:");
			if (this.players.size() == 0) result.append("\n <none>");
			else {
				result.append("\n 0: All players");
				for (int i = 0; i < this.players.size(); i++) result.append("\n " + (i+1) + ": " + String.format("%-" + NAME_LENGTH_LONG +"s",this.players.get(i).getName()) + " " + this.players.get(i).getChips());
			}
		}
		else {
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
			result.append("\n");
		}
		System.out.println(result.toString());
	}
	public void getInput() {
		String inStr = "";
		int inInt = -1;
		File[] files = new File(DEFAULT_SAVE_LOCATION).listFiles();
		System.out.println("INFO: " + this.info);
		if (!this.play) {
			System.out.println("Options: [0: quit] [1: load] [2: start over] [3: add player] [4: remove player] [5: reset chips] [6: give chips] [7: take chips] [8: start game]");
			inStr = this.promptGetString("Choose Option: ");
			switch (inStr) {
			case "0": //quit
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) this.quit = true;
				break;
			case "1": //load
				StringBuilder result = new StringBuilder();
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
							e.printStackTrace();
							this.info = "Something went wrong loading the file.";
						}
					}
					else {this.info = "Invalid file entered. Please enter a valid file.";}
				}
				else {this.info = "Invalid save file selection. Please enter a valid selection.";}
				break;
			case "2": //start over
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) {
					this.players = new ArrayList<Player>();
					this.info = "All player info cleared.";
				}
				else {this.info = DEFAULT_INFO;}
				break;
			case "3": //add player
				inStr = this.promptGetString("Enter player name: ");
				if (inStr.length() == 0 || inStr.length() > NAME_LENGTH_LONG) this.info = "Invalid name length. Names must contain 1-" + NAME_LENGTH_LONG + " characters.";
				else {
					this.players.add(new Player(inStr,0));
					this.info = DEFAULT_INFO;
				}
				break;
			case "4": //remove player
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
				break;
			case "5": //reset chips
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) {
					this.players.stream().forEach(player -> player.takeChips(player.getChips()));
					this.info = "Chips reset.";
				}
				else {this.info = DEFAULT_INFO;}
				break;
			case "6": //give chips
				int iplayergive = this.promptGetInt("Enter player number: ");
				if (iplayergive < 0 || iplayergive > this.players.size()) this.info = "Not a valid player number. Please enter a valid player number.";
				else {
					inInt = this.promptGetInt("Enter number of chips to give: ");
					if (inInt > 0) {
						if (iplayergive == 0)
							for (Player player : this.players)
								player.giveChips(inInt);
						else {this.players.get(iplayergive-1).giveChips(inInt);}
					this.info = "Chips taken.";
					}
				}
				break;
			case "7": //take chips
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
				break;
			case "8": //start game
				if (players.size() < 2) this.info = "Not enough players to start game. At least two players are required.";
				else if (players.stream().mapToInt(player -> player.getChips()).sum() == 0) this.info = "There must be at least one chip in play. Give the players some chips!";
				else {
					try {
						this.game = new Game(this.players.toArray(new Player[this.players.size()]), dice);
						this.info = "Game successfully started.";
						this.play = true;
					} catch (SecurityException | IOException e) {
						e.printStackTrace();
						this.info = "Something went wrong trying to start the game.";
					}
				}
				break;
			case "debug": //quick start game with defaults
				this.players = new ArrayList<Player>();
				this.players.add(new Player("Aaron",50));
				this.players.add(new Player("Billy",50));
				this.players.add(new Player("Chuck",50));
				try {
					this.game = new Game(this.players.toArray(new Player[this.players.size()]), dice);
					this.info = "Game successfully started.";
					this.play = true;
				} catch (SecurityException | IOException e) {
					e.printStackTrace();
					this.info = "Something went wrong trying to start the game.";
				}
			default:
				this.info = "Invalid option.";
				break;
			}
		}
		else {
			System.out.println("Options: [0: quit] [1: load] [2: save] [3: roll] [4: end turn]");
			inInt = this.promptGetInt("Choose option: ");
			switch (inInt) {
			case 0: //quit
				if (this.promptGetConfirm("Are you sure? [y/n]: ")) this.quit = true;
				break;
			case 1: //load
				if (this.promptGetConfirm("Are you sure? All unsaved progress will be lost. [y/n]: ")) {
					StringBuilder result = new StringBuilder();
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
								e.printStackTrace();
								this.info = "Something went wrong loading the file.";
							}
						}
						else {this.info = "Invalid file entered. Please enter a valid file.";}
					}
					else {this.info = "Invalid save file selection. Please enter a valid selection.";}
				}
				else {this.info = DEFAULT_INFO;}
				break;
			case 2: //save
				StringBuilder result = new StringBuilder();
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
						if (inStr.lastIndexOf('.') < inStr.lastIndexOf('/')) inStr = inStr + "." + SAVE_EXTENSION;
						file = new File(inStr);
					}
					else {file = files[inInt-1];}
					if ((!file.exists()) || (file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length()).equals(SAVE_EXTENSION) && this.promptGetConfirm("File already exists. Overwrite? [y/n]: "))) {
						try {
							Game.save(this.game, file.getAbsolutePath());
							this.info  = "Game saved to " + file.getAbsolutePath();
						} catch (Exception e) {
							this.info = "Something went wrong saving the file.";
						}
					}
				}
				else {this.info = "Invalid save file selection. Please enter a valid selection.";}
				break;
			case 3: //roll
				this.actRoll();
				break;
			case 4: //end turn
				this.actEnd();
				break;
			default:
				this.info = "Option invalid. Choose a valid option from the list below.";
				break;
			}
		}
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
game 0001: [Aaron   ] [Billy   ] [Chuck   ]
    chips: [12      ] [12      ] [12      ]
    score: [12      ] [12      ] [16      ]
     turn:            [34 (18) ]  #TARGET#

=====================================================
DETAILED
=====================================================
           [All     ] [Aaron   ] [Billy   ] [Chuck   ]
  overall: roll mean:
       roll median:
         roll mode:
 */