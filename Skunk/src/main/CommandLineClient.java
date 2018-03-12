package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineClient extends Client {
	private static final int NAME_LENGTH_LONG = 32, NAME_LENGTH_SHORT = 8, GAME_LENGTH = 4;
	private static final String DEFAULT_SAVE_LOCATION = "./sav/", SAVE_EXTENSION = "sav";
	private Scanner in;
	private boolean quit;
	
	public CommandLineClient() {
		this.in = new Scanner(System.in);
		this.quit = false;
	}
	public void run() {
		this.getPlayers();
		while(!this.quit && !this.game.isEnded()) {
			String info = "Choose an option below.";
			this.update();
			info = this.getInput(info);
		}
	}
	public void getPlayers() {
		List<Player> players = new ArrayList<Player>();
		boolean done = false;
		String info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
		int inInt = 0;
		String inStr = "";
		while (!this.quit && !done) {
			System.out.println("Players:");
			if (players.size() == 0) System.out.println(" <none>");
			else {
				System.out.println(" 0: All players");
				for (int i = 0; i < players.size(); i++) System.out.println(" " + (i+1) + ": " + String.format("%-" + NAME_LENGTH_LONG +"s",players.get(i).getName()) + " " + players.get(i).getChips());
			}
			System.out.println("INFO: " + info);
			System.out.println("Choose an option below.");
			System.out.println("Options: [0: quit] [1: load] [2: start over] [3: add player] [4: remove player] [5: reset chips] [6: give/take chips] [7: start game]");
			System.out.print("Choose option: ");
			inStr = this.in.nextLine();
			switch (inStr) {
			case "0": //quit
				System.out.print("Are you sure? [y/n]: ");
				inStr = this.in.nextLine();
				if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
				else {
					if (inStr.toLowerCase().charAt(0) == 'y') this.quit = true;
					else info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
				}
				break;
			case "1": //load
				File[] files = new File(DEFAULT_SAVE_LOCATION).listFiles();
				System.out.println("Saved games:");
				System.out.println(" 0: Enter file...");
				for (int i = 0; i < files.length; i++)
					if (files[i].getName().substring(files[i].getName().lastIndexOf('.')+1, files[i].getName().length()).equals(SAVE_EXTENSION))
						System.out.println(" " + (i+1) + ": " + DEFAULT_SAVE_LOCATION + files[i].getName());
				System.out.print("Choose a saved file to load: ");
				inStr = this.in.nextLine();
				try {
					inInt = Integer.parseInt(inStr);
				} catch (Exception e) {
					e.printStackTrace();
					inInt = -1;
				}
				if (inInt == 0) {
					System.out.print("Enter save file path and name: ");
					inStr = this.in.nextLine();
					File file = new File(inStr);
					if (file.exists() && file.getName().substring(file.getName().lastIndexOf('.')+1, file.getName().length()).equals(SAVE_EXTENSION)) {
						try {
							this.game = Game.load(file.getAbsolutePath());
							done = true;
						} catch (Exception e) {
							info = "Something went wrong loading the file.";
						}
					}
					else {
						info = "Invalid file entered. Please enter a valid file.";
					}
				}
				else if (0 < inInt && inInt <= files.length) {
					try {
						this.game = Game.load(files[inInt-1].getAbsolutePath());
						done = true;
					} catch (Exception e) {
						info = "Something went wrong loading the file.";
					}
				}
				else {
					info = "Invalid save file selection. Please enter a valid selection.";
				}
				break;
			case "2": //start over
				System.out.print("Are you sure? [y/n]: ");
				inStr = this.in.nextLine();
				if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
				else {
					if (inStr.toLowerCase().charAt(0) == 'y') players = new ArrayList<Player>();
					info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
				}
				break;
			case "3": //add player
				System.out.print("Enter player's name: ");
				inStr = this.in.nextLine();
				if (inStr.length() == 0 || inStr.length() > NAME_LENGTH_LONG) info = "Invalid name length. Names must contain at least one character and at most " + NAME_LENGTH_LONG + " characters.";
				else {
					players.add(new Player(inStr,0));
					info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
				}
				break;
			case "4": //remove player
				System.out.print("Enter player number: ");
				inStr = this.in.nextLine();
				try {
					inInt = Integer.parseInt(inStr);
				} catch (Exception e) {
					e.printStackTrace();
					inInt = -1;
				}
				if (inInt < 0 || inInt > players.size()) info = "Not a valid player number. Please enter a valid player number.";
				else {
					System.out.print("Are you sure? [y/n]: ");
					inStr = this.in.nextLine();
					if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
					else {
						if (inStr.toLowerCase().charAt(0) == 'y') {
							if (inInt == 0) players = new ArrayList<Player>();
							else players.remove(inInt-1);
						}
						info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
					}
				}
				break;
			case "5": //reset chips
				System.out.print("Are you sure? [y/n]: ");
				inStr = this.in.nextLine();
				if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
				else {
					if (inStr.toLowerCase().charAt(0) == 'y') players.stream().forEach(player -> player.takeChips(player.getChips()));
					else info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
				}
				break;
			case "6": //give/take chips
				System.out.print("Enter player number: ");
				inStr = this.in.nextLine();
				try {
					inInt = Integer.parseInt(inStr);
				} catch (Exception e) {
					e.printStackTrace();
					inInt = -1;
				}
				if (inInt < 0 || inInt > players.size()) info = "Not a valid player number. Please enter a valid player number.";
				else {
					int iplayer = inInt;
					System.out.print("Enter number of chips (negative to remove chips): ");
					inStr = this.in.nextLine();
					try {
						inInt = Integer.parseInt(inStr);
					} catch (Exception e) {
						e.printStackTrace();
						inInt = -1;
					}
					if (iplayer == 0) {
						for (Player player : players)
							if (inInt > 0) player.giveChips(inInt);
							else player.takeChips(Math.abs(inInt));
					}
					else {
						if (inInt > 0) players.get(iplayer-1).giveChips(inInt);
						else players.get(iplayer-1).takeChips(Math.abs(inInt));
					}
					info = "Enter the names and number of starting chips of the players. At least two players are required to start a game, and there must be at least one chip in play.";
				}
				break;
			case "7": //start game
				if (players.size() < 2) info = "Not enough players to start game. At least two players are required.";
				else if (players.stream().mapToInt(player -> player.getChips()).sum() == 0) info = "There must be at least one chip in play. Give the players some chips!";
				else {
					try {
						this.game = new Game(players.toArray(new Player[players.size()]), new StandardDice());
						done = true;
					} catch (SecurityException | IOException e) {
						e.printStackTrace();
						info = "Something went wrong trying to start the game.";
					}
				}
				break;
			case "debug": //quick start game with defaults
				players = new ArrayList<Player>();
				players.add(new Player("Aaron",50));
				players.add(new Player("Billy",50));
				players.add(new Player("Chuck",50));
				try {
					this.game = new Game(players.toArray(new Player[players.size()]), new StandardDice());
					done = true;
				} catch (SecurityException | IOException e) {
					e.printStackTrace();
					info = "Something went wrong trying to start the game.";
				}
			default:
				info = "Option invalid. Choose a valid option from the list below.";
				break;
			}
		}
	}
	public void update() {
		StringBuilder result = new StringBuilder();
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
		System.out.println(result.toString());
	}
	public String getInput(String info) {
		System.out.println("INFO: " + info);
		System.out.println("Options: [0: quit] [1: load] [2: save] [3: roll] [4: end turn]");
		System.out.print("Choose option: ");
		int inInt = this.in.nextInt();
		this.in.nextLine();
		File[] files = new File(DEFAULT_SAVE_LOCATION).listFiles();
		switch (inInt) {
		case 0: //quit
			System.out.print("Are you sure? [y/n]: ");
			String inStr = this.in.nextLine();
			if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
			else {
				if (inStr.toLowerCase().charAt(0) == 'y') this.quit = true;
				else info = "Choose an option below.";
			}
			break;
		case 1: //load
			System.out.println("Saved games:");
			System.out.println(" 0: Enter file...");
			for (int i = 0; i < files.length; i++)
				if (files[i].getName().substring(files[i].getName().lastIndexOf('.')+1, files[i].getName().length()).equals(SAVE_EXTENSION))
					System.out.println(" " + (i+1) + ": " + DEFAULT_SAVE_LOCATION + files[i].getName());
			System.out.print("Choose a saved file to load: ");
			inStr = this.in.nextLine();
			try {
				inInt = Integer.parseInt(inStr);
			} catch (Exception e) {
				e.printStackTrace();
				inInt = -1;
			}
			if (inInt == 0) {
				System.out.print("Enter save file path and name: ");
				inStr = this.in.nextLine();
				File file = new File(inStr);
				if (file.exists() && file.getName().substring(file.getName().lastIndexOf('.')+1, file.getName().length()).equals(SAVE_EXTENSION)) {
					try {
						this.game = Game.load(file.getAbsolutePath());
					} catch (Exception e) {
						info = "Something went wrong loading the file.";
					}
				}
				else {
					info = "Invalid file entered. Please enter a valid file.";
				}
			}
			else if (0 < inInt && inInt <= files.length) {
				try {
					this.game = Game.load(files[inInt-1].getAbsolutePath());
				} catch (Exception e) {
					info = "Something went wrong loading the file.";
				}
			}
			else {
				info = "Invalid save file selection. Please enter a valid selection.";
			}
			break;
		case 2: //save
			System.out.println("Saved games:");
			System.out.println(" 0: Enter file...");
			for (int i = 0; i < files.length; i++)
				if (files[i].getName().substring(files[i].getName().lastIndexOf('.')+1, files[i].getName().length()).equals(SAVE_EXTENSION))
					System.out.println(" " + (i+1) + ": " + DEFAULT_SAVE_LOCATION + files[i].getName());
			System.out.print("Choose a file to save to: ");
			inStr = this.in.nextLine();
			try {
				inInt = Integer.parseInt(inStr);
			} catch (Exception e) {
				e.printStackTrace();
				inInt = -1;
			}
			if (inInt == 0) {
				System.out.print("Enter save file path and name: ");
				inStr = this.in.nextLine();
				if (inStr.lastIndexOf('/') <= 0) inStr = DEFAULT_SAVE_LOCATION + inStr;
				if (inStr.lastIndexOf('.') < inStr.lastIndexOf('/')) inStr = inStr + "." + SAVE_EXTENSION;
				File file = new File(inStr);
				if (file.exists() && file.getName().substring(file.getName().lastIndexOf('.')+1, file.getName().length()).equals(SAVE_EXTENSION)) {
					System.out.print("File already exists. Overwrite? [y/n]: ");
					inStr = this.in.nextLine();
					if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
					else {
						if (inStr.toLowerCase().charAt(0) == 'y')
							try {
								Game.save(this.game, file.getAbsolutePath());
								info  = "Game saved to " + file.getAbsolutePath();
							} catch (Exception e) {
								info = "Something went wrong saving the file.";
							}
						else info = "Choose a valid option from the list below.";
					}
				}
				else {
					try {
						Game.save(this.game,file.getAbsolutePath());
					} catch (Exception e) {
						info = "Something went wrong saving the file.";
					}
				}
			}
			else if (0 < inInt && inInt <= files.length) {
				System.out.print("File already exists. Overwrite? [y/n]: ");
				inStr = this.in.nextLine();
				if (inStr.length() != 1 || !(inStr.toLowerCase().charAt(0) == 'y' || inStr.toLowerCase().charAt(0) == 'n')) info = "Invalid confirmation.";
				else {
					if (inStr.toLowerCase().charAt(0) == 'y')
						try {
							Game.save(this.game, files[inInt-1].getAbsolutePath());
						} catch (Exception e) {
							info = "Something went wrong saving the file.";
						}
					else info = "Choose a valid option from the list below.";
				}
			}
			else {
				info = "Invalid save file selection. Please enter a valid selection.";
			}
			break;
		case 3: //roll
			this.actRoll();
			break;
		case 4: //end turn
			this.actEnd();
			break;
		default:
			info = "Option invalid. Choose a valid option from the list below.";
			break;
		}
		return info;
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