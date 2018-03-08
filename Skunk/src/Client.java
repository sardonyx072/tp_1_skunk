
public abstract class Client {
	protected Game game;
	
	public abstract void update(); //update UI
	public abstract String getInput(); //get input from user
}

// 1SKUNK
// 1SK1DC
// 2SKUNK

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
