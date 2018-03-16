package main;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class BotPlayer extends Player {
	private static final String TAG = " (AI)";
	private static String[] NAMES = null;
	static {try{NAMES = Files.readAllLines(Paths.get("src/main/resources/botnames.txt"),Charset.defaultCharset()).stream().toArray(String[]::new);} catch (Exception e) {NAMES = new String[] {"error"};}}
	protected int riskThreshold;
	public BotPlayer(int riskThreshold) {
		this(NAMES[(int)(Math.random()*NAMES.length)],UUID.randomUUID(),riskThreshold);
	}
	public BotPlayer(String name, UUID uuid, int riskThreshold) {
		super(name.substring(name.length()-TAG.length()).equals(TAG) ? name.substring(0, name.length()-TAG.length()) : name,uuid);
		this.riskThreshold = riskThreshold;
	}
	public abstract String act(Game game);
	public int getThreshold() {return this.riskThreshold;}
	public void setThreshold(int riskThreshold) {this.riskThreshold = riskThreshold;}
	@Override
	public String getName() {return super.getName() + TAG;}
}
