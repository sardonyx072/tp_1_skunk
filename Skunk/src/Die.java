public class Die implements IDie {
	private int sides, value;
	public Die(int sides) {
		this.sides = sides;
		this.value = sides;
	}
	public int getValue() {return this.value;}
	public int roll() {return this.value = (int)(Math.random()*this.sides)+1;}
}
