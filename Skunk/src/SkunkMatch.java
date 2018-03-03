import java.util.ArrayList;
import java.util.List;


/**
 * 
 * This class is an experiment with game logic. It is to be abandoned in leiu of an actual implementation.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class SkunkMatch {
	private List<SkunkPlayer> players;
	private List<SkunkGame> games;
	public SkunkMatch(String[] names, int startingChips) {
		this.players = new ArrayList<SkunkPlayer>();
		for (String name : names) {this.addPlayer(name,startingChips);}
		this.games = new ArrayList<SkunkGame>();
	}
	public void addPlayer(String name, int startingChips) {this.players.add(new SkunkPlayer(name,startingChips));}
	public void addPlayer(String name, int startingChips, int position) {this.players.add(position, new SkunkPlayer(name, startingChips));}
	public void removePlayer(int position) {/*TODO*/}
	public void removePlayer(String name) {/*TODO*/}
	public List<SkunkPlayer> getPlayers() {
		List<SkunkPlayer> clone = new ArrayList<SkunkPlayer>();
		for (SkunkPlayer player : this.players) {clone.add(player.clone());}
		return clone;
	}
	public int getNumPlayers() {return this.players.size();}
	public int getNumGamesPlayed() {return this.games.size();}
	public void playGame(int target) {
		this.games.add(new SkunkGame(target,this.players));
		this.games.get(this.games.size()-1).play();
	}
}
