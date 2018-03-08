package main;

import java.util.ArrayList;
import java.util.List;

public class CommandLineClient extends Client {
	public void getPlayers() {
		List<String> names = new ArrayList<String>();
		// opts 0-quit, 1-back/quit, 2-load match, 3-add player, 4-finish, (5)-remove player, (6)-remove all players
		// loop until finished
	}
	public void run() {
		this.getPlayers();
		while(!this.game.isEnded()) {
			
		}
	}
}


//1SKUNK
//1SK1DC
//2SKUNK

/*
game 0002: [ Aaron] {[ Billy]} [ Chuck]  [ David]  ||        [  Dice]
 chips: [    12] {[    34]} [    56]  [    78]  ||  mean:
---------- [------] {[------]} [------]  [------]  ||  mode:
 score: [    25] {[    35]}<[   112]> [    02]  ||  %skunk:
  turn:          {[+   41]}<[TARGET]>           ||  %skunkdeuce
 total:          {[=   76]}                     ||  %doubleskunk
---------- [------]  [------]  [------]  [------]
game 0001: 
 chips: [    12]  [    12]  [    12]  [    12]
 score: [    34]  [    34]  [    34]  [    34]
---------- [------]  [------]  [------]  [------]
*/

//public String toString() {
//final int MIN_GAME_WIDTH = 2, MAX_GAME_WIDTH = 4;
//final int MIN_NAME_WIDTH = 6, MAX_NAME_WIDTH = 16;
//final int MIN_CHIPS_WIDTH = 4, MAX_CHIPS_WIDTH = 14;
//final int MIN_SCORE_WIDTH = 4, MAX_SCORE_WIDTH = 14;
//
//assert MIN_GAME_WIDTH <= MAX_GAME_WIDTH;
//assert MIN_NAME_WIDTH <= MAX_NAME_WIDTH;
//assert MIN_CHIPS_WIDTH <= MAX_CHIPS_WIDTH;
//assert MIN_SCORE_WIDTH <= MAX_SCORE_WIDTH;
//
//int game_width = Math.max(MIN_GAME_WIDTH, Math.min(MAX_GAME_WIDTH, this.numGamesThisMatch));
//int name_width = Math.max(MIN_NAME_WIDTH,Math.min(MAX_NAME_WIDTH,Collections.max(this.scores.keySet().stream().map(p -> p.getName().length()).collect(Collectors.toList()))));
//int chips_width = Math.max(MIN_CHIPS_WIDTH,Math.min(MAX_NAME_WIDTH,Integer.toString(this.scores.keySet().stream().mapToInt(p -> p.getChips()).sum()).length()));
//int score_width = Math.max(MIN_SCORE_WIDTH,Math.min(MAX_NAME_WIDTH,Collections.max(this.scores.values())));
//StringBuilder str = new StringBuilder();
//}