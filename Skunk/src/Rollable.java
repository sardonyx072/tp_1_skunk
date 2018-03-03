
/**
 * 
 * Declares the behavior of object that can be rolled to return an integer result.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public interface Rollable {
	
	/**
	 * Returns the numerical value of the previously rolled object.
	 * 
	 * @return
	 */
	public int getValue();
	
	/**
	 * Returns the numerical value of the object after rolling it.
	 * 
	 * @return
	 */
	public int roll();
}
