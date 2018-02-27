public abstract class Die implements Rollable {
	protected int[] values;
	public Die(int[] values) {
		if (values == null)
			throw new NullPointerException("null value array passed to Die");
		else if (values.length == 0)
			throw new IllegalArgumentException("empty value array passed to Die");
		this.values = values;
	}
}
