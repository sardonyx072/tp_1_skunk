package main;
import java.io.IOException;
import java.util.Scanner;

public class SimpleCommandLnInterface {

	public static void main(String[] args) throws SecurityException, IOException {
		// TODO Auto-generated method stub
		Scanner scan= new Scanner(System.in);
		Player[] array = new Player[4];
		StandardDice dice = new StandardDice();
		
		for (int x=0; x<4; x++) {
			System.out.println("Enter player name:");
			String name = scan.nextLine();
			
			array[x] = new Player(name, 50);
			
		}
	
		Game game = new Game(array, dice);
		
		while ( game.isEnded() == false) {
		
			System.out.println("The current player:" + game.getCurrentPlayer().getName());
			System.out.println("Kitty:" + game.getKitty());
			System.out.println("Chips:" + game.getCurrentPlayer().getChips());
			System.out.println("The turn scores:" + game.getCurrentTurnScore());
			System.out.println("The game scores:" + game.getScores().get(game.getCurrentPlayer()));
			System.out.println("Target:" + game.getTarget());
			
			System.out.println("choose one two options: 1-Roll again  2-End your turn");
			
			int choice = scan.nextInt();
			if (choice == 1) game.turnOptRoll();
			
			else if (choice == 2) game.turnOptEnd(); 

		}
	}

}
