
public class SimDie extends Die {
	private int iCurrent;
	public SimDie (int[] values) {
		super(values);
		this.iCurrent = 0;
	}
	public int getValue() {return this.values[this.iCurrent];}
	public int roll() {
		this.iCurrent = (this.iCurrent+1 >= this.values.length ? 0 : this.iCurrent+1);
		return this.getValue();
	}
}
