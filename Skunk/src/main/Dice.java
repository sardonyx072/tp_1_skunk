package main;
import java.util.Arrays;

/**
 * 
 * Dice mimics a set of dice that can be rolled as a group and are treated as one summed value.
 * The Dice can be of any Die type (but not any Rollable, to prevent potentially infinite recursive dice groups),
 * including SimDie, RandomDie, and StandardDie.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class Dice implements Rollable {
	private Die[] dice;
	
	/**
	 * Constructor for Dice. Must be provided with a non-null and non-empty list of some type of dice, which
	 * can be SimDie, RandomDie, or StandardDie.
	 * 
	 * @param dice
	 */
	public Dice(Die[] dice) {
		if (dice == null)
			throw new NullPointerException("null dice list given to dice");
		else if (dice.length == 0)
			throw new IllegalArgumentException("no dice given to dice");
		this.dice = dice;
	}
	
	/**
	 * Return individual values of dice as an array.
	 */
	public int[] getValues() {return Arrays.asList(this.dice).stream().mapToInt(die -> die.getValue()).toArray();}
	
	/**
	 * Uses a lambda function on a stream created from the dice list to quickly getValue on every die and return the sum.
	 */
	public int getValue() {return Arrays.asList(this.dice).stream().mapToInt(die -> die.getValue()).sum();}
	
	/**
	 * Uses a lambda function on a stream created from the dice list to quickly roll every die and return the sum of all results.
	 */
	public int roll() {return Arrays.asList(this.dice).stream().mapToInt(die -> die.roll()).sum();}
	
	/**
	 * @return a string representation of the arrays that make up the dice
	 */
	public String flatten() {return this.getClass().getName() + Arrays.asList(this.dice).stream().map(die -> die.flatten()).reduce("", (die1, die2) -> die1 + " " + die2);}
	
	/**
	 * @return a string representation of the summation of die values
	 */
	public String toString() {return Arrays.asList(this.dice).stream().map(die -> Integer.toString(die.getValue())).reduce("",(die1,die2)->die1+"+"+die2).substring(1)+"="+this.getValue();}
}
