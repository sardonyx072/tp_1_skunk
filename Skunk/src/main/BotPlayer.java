package main;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class BotPlayer extends Player {
	private static final String TAG = " (AI)";
	private static String[] NAMES = null;
	static {
		try{NAMES = Files.readAllLines(Paths.get("main/resources/botnames.txt"),Charset.defaultCharset()).stream().toArray(String[]::new);} catch (Exception e) {NAMES = new String[] {"error"};}
	}
	protected int riskThreshold;
	public BotPlayer(int chips, int riskThreshold) {
		this(NAMES[(int)(Math.random()*NAMES.length)] + TAG,UUID.randomUUID(),chips,riskThreshold);
	}
	public BotPlayer(String name, UUID uuid, int chips, int riskThreshold) {
		super(name,uuid,chips);
		this.riskThreshold = riskThreshold;
	}
	public abstract String act(Game game);
	public int getThreshold() {return this.riskThreshold;}
	public void setThreshold(int riskThreshold) {this.riskThreshold = riskThreshold;}
}
