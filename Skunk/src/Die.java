
/**
 * 
 * Die is a abstract or template type for all types of die. It is Rollable, but the behavior is defined in child classes
 * instead of here. All die also have a list of values that are how rolls are generated, but they are interpreted differently
 * in the child classes. See SimDie and RandomDie for these implementations.
 * 
 * @author Mitchell Hoffmann & Eyad Shesli
 *
 */
public abstract class Die implements Rollable {
	protected int[] values;
	
	/**
	 * Constructor for Die. The values list must be non-null and non-empty.
	 * 
	 * @param values
	 */
	public Die(int[] values) {
		if (values == null)
			throw new NullPointerException("null value array passed to Die");
		else if (values.length == 0)
			throw new IllegalArgumentException("empty value array passed to Die");
		this.values = values;
	}
}
