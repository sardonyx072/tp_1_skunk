package main;

public abstract class BotPlayer extends Player {
	private static final String[] NAMES = new String[]{
			// scientists
			"Albert Einstein","Nikola Tesla","Marie Curie","Euclid","Thomas Edison","Bill Nye","Max Planck",
			// computer scientists
			"Linus Torvalds",
			// DnD 
			"Dave Arneson","Gary Gygax","Mordenkainen","Bigby","Vecna","Kas","Otiluke","Tasha","Melf",
			// Marvel/DC
			"Charles Xavier","Max Eisenhardt","Hank McCoy","Bruce Banner","Tony Stark","JARVIS","Loki","Ultron","Thanos","Dr. Octopus","Mr. Fantastic","Vision","Dr. Doom","Bruce Wayne",
			// star trek
			"Data","Lore","Locutus","Q",
	};
	protected int riskThreshold;
	public BotPlayer(int chips, int riskThreshold) {
		super(NAMES[(int)(Math.random()*NAMES.length)] + " (AI)",chips);
		this.riskThreshold = riskThreshold;
	}
	public abstract String act(Game game);
	public int getThreshold() {return this.riskThreshold;}
	public void setThreshold(int riskThreshold) {this.riskThreshold = riskThreshold;}
}
