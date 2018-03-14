package main;

/**
 * 
 * A model for two fair/balanced 6-sided dice.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class StandardDice extends Dice {
	public StandardDice() {
		super(new StandardDie[] {new StandardDie(),new StandardDie()});
	}
	public String flatten() {return this.getClass().getName();}
}
