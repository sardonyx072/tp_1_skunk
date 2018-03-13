package main;

import java.util.UUID;

public abstract class BotPlayer extends Player {
	private static final String[] NAMES = new String[]{
			// scientists
			"Albert Einstein","Nikola Tesla","Marie Curie","Euclid","Thomas Edison","Max Planck","Avogadro","Isaac Newton","Galileo Galilei","Charles Darwin","Stephen Hawking","Michael Faraday","Nicolaus Copernicus",
			"Louis Pasteur","Leonardo da Vinci","Alexander Graham Bell","Francis Crick","James Watson","Archimedes","Rosalind Franklin","Niels Bohr","Gregor Mendel","George Washington Carver","James Clerk Maxwell",
			"Johannes Kepler","Richard Feynman","Ernest Rutherford","Robert Boyle","Rachel Carson","Alexander Fleming","Ada Lovelace","Guglielmo Marconi","Carl Sagan","Alessandro Volta","Pierre Curie","Antoine Lavoisier",
			"Tim Berners-Lee","Robert Hooke","Edwin Hubble","Mary Anning","William Thomson","Alfred Nobel","James Prescott Joule","Blaise Pascal","Enrico Fermi","Linus Pauling","John Dalton","Dmitri Mendeleev",
			"Antoine van Leeuwenhoek","Erwin Schroedinger","Paul Dirac","J.J. Thomson","Ernest Walton","Franz Mesmer","Phillipe Pinel","Rudolph Virchow","Irene Joliot-Curie",
			// philosophers
			"Sophocles","Aristotle","",
			// computer scientists
			"Linus Torvalds",
			// DnD 
			"Dave Arneson","Gary Gygax","Mordenkainen","Vecna","Kas","Otiluke","Strahd von Zarovich","Acererak",
			// Marvel/DC
			"Charles Xavier","Bruce Banner","Tony Stark","JARVIS","Loki","Ultron",
			// star trek
			"Data","Lore","Locutus","Q",
	};
	protected int riskThreshold;
	public BotPlayer(int chips, int riskThreshold) {
		this(NAMES[(int)(Math.random()*NAMES.length)] + " (AI)",UUID.randomUUID(),chips,riskThreshold);
	}
	public BotPlayer(String name, UUID uuid, int chips, int riskThreshold) {
		super(name,uuid,chips);
		this.riskThreshold = riskThreshold;
	}
	public abstract String act(Game game);
	public int getThreshold() {return this.riskThreshold;}
	public void setThreshold(int riskThreshold) {this.riskThreshold = riskThreshold;}
}
