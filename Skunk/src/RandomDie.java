import java.util.stream.IntStream;

public class RandomDie extends Die {
	private int value;
	public RandomDie(int[] probabilities) {
		super(probabilities);
		/*TODO invalid probabilities*/
		this.roll(); //find better initial value
	}
	public int getValue() {return this.value;}
	public int roll() {
		int s = (int)(Math.random()*IntStream.of(this.values).sum())+1;
		int sum = 0;
		this.value = 0;
		while (s > (sum+=this.values[this.value]))
			this.value++;
		return this.getValue();
	}
}
