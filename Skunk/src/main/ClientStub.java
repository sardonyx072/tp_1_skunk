package main;
import java.io.IOException;

public class ClientStub {
	public static void main(String[] args) throws SecurityException, IOException {
		Player[] players = new Player[] {new Player("Aaron",50),new Player("Billy",50),new Player("Chuck",50),new Player("David",50),new Player("Edgar",50),new Player("Frank",50)};
		Game game = new Game(players, new StandardDice());
		game.setActive(true);
		while (game.isActive()) {
			if (game.getCurrentTurnScore() < 20)
				game.actRoll();
			else
				game.actEnd();
		}
	}
}
