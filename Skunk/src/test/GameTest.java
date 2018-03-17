package test;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import main.*;

public class GameTest {

	@Test
	public void getMaxPlayerTest() {
		int test=Game.getMaxPlayers();
		assertEquals(1023,test);
		
	}
	
	@Test
	public void getMaxStartingChipsTest() {
		
		int test=Game.getMaxStartingChips();
		assertEquals(2097152,test);

}
	@Test
	public void getIllegalCharsTest() {
		
		String test=Game.getIllegalChars();
		assertEquals("|",test);

}
	@Test
	public void getPlayersTest() {
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		Player player1 = new Player("Mitch");
		Player player2 = new Player("Eyad");
		
		game.addPlayer(player1);
		game.addPlayer(player2);
		
		Player[] test = game.getPlayers();
		
		
		Player[] playertest = {player1, player2};

		assertEquals(playertest, test);
	
	
	}
	@Test
	public void getScoresTest() {
		String name = "Mitch";
		Player player = new Player(name);
		
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		game.addPlayer(player);
		
		CircularLinkedHashMap<Player, Integer> score = game.getScores();
		
		
		Integer value = score.get(player);
		
		assertEquals(0, value.intValue());
		
	}
	
	@Test
	public void getChipsTest() {
		String name = "Mitch";
		Player player = new Player(name);
		
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		game.addPlayer(player);
		
		CircularLinkedHashMap<Player, Integer> chip = game.getChips();
		
		
		Integer value = chip.get(player);
		
		assertEquals(0, value.intValue());
		
	}

	@Test
	public void getStatesTest() {
		String name = "Mitch";
		Player player = new Player(name);
		
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		game.addPlayer(player);
		
		CircularLinkedHashMap<Player, Integer> state = game.getStates();
		
		
		Integer value = state.get(player);
		
		assertEquals(0, value.intValue());
		
	}
	
	@Test
	public void addPlayerTest() {
		
		Player player = new Player("Mitch");
		
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		game.addPlayer(player);
		
		Player[] test = game.getPlayers();
		
		Player playertest = test[0];
		
		
		assertEquals(player, playertest);
		
	}
	
	@Test
	public void getPlayersStillInTest() {
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		Player player1 = new Player("Mitch");
		Player player2 = new Player("Eyad");

		game.addPlayer(player1);
		game.addPlayer(player2);
		
		Player[] test = game.getPlayers();
		
		
		Player[] playertest = {player1, player2};

		assertEquals(playertest, test);
	
	
	}
	
	@Test
	public void hasWinnerTest() {
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		Player player1 = new Player("Mitch");
		Player player2 = new Player("Eyad");

		game.addPlayer(player1);
		game.addPlayer(player2);
		
		Player[] test = game.getPlayers();
		
		
		Player[] playertest = {player1, player2};
		
		game.getPlayersStillIn();

		assertEquals(playertest, test);
	
	
	}
	@Test
	public void removePlayerTest() {
		
		Player player = new Player("Mitch");
		
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		game.addPlayer(player);
		
		Player[] test = game.getPlayers();
		
		Player playertest = test[0];
		
		game.removePlayer(player);
		
		
		assertEquals(playertest, player);
		
	}
	
	@Test
	public void giveChipsTest() {
		
		Player player = new Player("Mitch");
		
		Dice dice = new StandardDice();
		
		Game game = new Game(dice);
		
		game.addPlayer(player);
		
		Player[] test = game.getPlayers();
		
		Player playertest = test[0];
		
		game.getChips();
		
		
		assertEquals(player, playertest);
		
	}
	
}
