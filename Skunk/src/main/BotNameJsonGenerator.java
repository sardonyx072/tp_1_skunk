package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BotNameJsonGenerator {
	public static void main(String[] args) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(new File("./src/main/resources/botnames.txt")));
			writer = new BufferedWriter(new FileWriter(new File("./src/main/resources/botnames.json")));
			writer.write("{\"names\":[");
			String line;
			while((line = reader.readLine())!=null)
				writer.write("\n\t\""+line+"\",");
			writer.write("\n]}");
			System.out.println("Written successfully!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Something went wrong!");
		} finally {
			try {
				reader.close();
				writer.close();
				System.out.println("Closed successfully!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
