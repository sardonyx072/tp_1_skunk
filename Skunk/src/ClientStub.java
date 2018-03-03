
public class ClientStub {
	public static void main(String[] args) {
		Player[] players = new Player[] {new Player("Aaron",50),new Player("Billy",50),new Player("Chuck",50),new Player("David",50),new Player("Edgar",50),new Player("Frank",50)};
		Game game = new Game(players);
		while (!game.isEnded()) {
			if (game.getCurrentTurn().getScore() < 20)
				game.roll();
			else
				game.end();
		}
	}
}
