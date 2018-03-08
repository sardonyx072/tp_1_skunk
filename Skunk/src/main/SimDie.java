package main;

/**
 * 
 * SimDie models a rollable object with a deterministic outcome instead of a random one, making testing of other modules easier.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class SimDie extends Die {
	
	/**
	 * A pointer to the current value of the die.
	 */
	private int iCurrent;
	
	/**
	 * The Constructor for the SimDie. The value list for SimDie must be non-null and non-empty, but can be any whole number including
	 * negative. The initial value for the die is always the first value given. When rolled, the value list acts as a circular buffer,
	 * always moving one direction by one step for each time rolled.
	 * 
	 * @param values
	 */
	public SimDie (int[] values) {
		super(values);
		this.iCurrent = 0;
	}
	
	/**
	 * Return the previously rolled value.
	 */
	public int getValue() {return this.values[this.iCurrent];}
	
	/**
	 * Return the value of the die after it is rolled and the circular pointer advances.
	 */
	public int roll() {
		this.iCurrent = (this.iCurrent+1 >= this.values.length ? 0 : this.iCurrent+1);
		return this.getValue();
	}
}
