
public class SkunkPlayer implements Cloneable {
	private String name;
	private int chips;
	public SkunkPlayer(String name, int chips) {
		this.name = name;
		this.chips = chips;
	}
	public String getName() {return this.name;}
	public int getChips() {return this.chips;}
	public void setName(String name) {this.name = name;}
	public void setChips(int chips) {this.chips = chips;}
	public void addChips(int chips) {this.chips += chips;}
	public void removeChips(int chips) {this.chips -= chips;}
	public SkunkPlayer clone() {return new SkunkPlayer(this.name,this.chips);}
	public String toString() {return "[" + this.getName() + ":" + this.getChips() + "]";}
}
