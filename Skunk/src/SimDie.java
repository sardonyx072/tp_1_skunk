
public class SimDie implements IDie {
	private int[] values;
	private int iCurrent;
	public SimDie (int[] values) {
		this.values = values;
		this.iCurrent = 0;
	}
	public int getValue() {
		return this.values[this.iCurrent];
	}
	public int roll() {
		this.iCurrent++;
		if (this.iCurrent >= this.values.length) {this.iCurrent = 0;}
		return this.getValue();
	}
}
