import java.util.stream.IntStream;

/**
 * 
 * RandomDie models a die that produces a random result when rolled. This random value is achieved by using the values list
 * as a probability distribution (thus the values list should only hold non-negative values).
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public class RandomDie extends Die {
	
	/**
	 * The previously rolled number.
	 */
	private int value;
	
	/**
	 * The Constructor for RandomDie. The probability distribution gives must be non-null and non-empty and contain only 
	 * non-negative values. The initial value for the die is rolled randomly.
	 * 
	 * @param probabilities
	 */
	public RandomDie(int[] probabilities) {
		super(probabilities);
		/*TODO invalid probabilities*/
		this.roll(); //find better initial value
	}
	
	/**
	 * Returns the previously rolled number.
	 */
	public int getValue() {return this.value;}
	
	/**
	 * Rolls a value on the die by finding which weighted probability corresponds to a randomly generated number based on the total
	 * probabilities for all possible values.
	 */
	public int roll() {
		int s = (int)(Math.random()*IntStream.of(this.values).sum())+1;
		int sum = 0;
		this.value = 0;
		while (s > (sum+=this.values[this.value]))
			this.value++;
		return this.getValue();
	}
}
