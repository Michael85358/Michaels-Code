package fop.model.player;

import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreEntry implements Comparable<ScoreEntry> {

	private String name;
	private Date date;
	private int score;

	/**
	 * creates a score entry
	 * 
	 * @param name
	 * @param score
	 * @param date
	 */
	public ScoreEntry(String name, int score, Date date) {
		this.name = name;
		this.score = score;
		this.date = date;
	}

	/**
	 * creates a score entry via player
	 * 
	 * @param player
	 */
	public ScoreEntry(Player player) {
		this.name = player.getName();
		this.score = player.getScore();
		this.date = new Date();
	}

	/**
	 * compares the score
	 */
	@Override
	public int compareTo(ScoreEntry scoreEntry) {
		return Integer.compare(this.score, scoreEntry.score);
	}

	/**
	 * prints a new highscore entry
	 * 
	 * @param printWriter
	 */
	public void write(PrintWriter printWriter) {
		String line = name + ";" + date.getTime() + ";" + score;
		printWriter.println(line);
	}

	/**
	 * reads a score entry and checks if it is allowed
	 * 
	 * @param line
	 * @return
	 */
	public static ScoreEntry read(String line) {
		String regex = "(((\\w)| )+);(\\d+);(\\d+)";
		// Check the format
		if (!line.matches(regex)) {
			return null;
		}
		// Seperate the String
		int firstSemicolon = line.indexOf(';');
		int secondSemicolon = line.lastIndexOf(';');
		String name = line.substring(0, firstSemicolon);
		long date = Long.parseLong(line.substring(firstSemicolon + 1, secondSemicolon));
		int score = Integer.parseInt(line.substring(secondSemicolon + 1));
		// Check if numbers are valid
		if (date < 0 || score < 0) {
			return null;
		}
		// Constructs a ScoreEntry
		return new ScoreEntry(name, score, new Date(date));
	}

	/**
	 * Converts the ScoreEntry to a String in right format
	 * 
	 * @author <u>team 129</u>
	 */
	@Override
	public String toString() {
		return name + ";" + date.getTime() + ";" + score;
	}

	/**
	 * returns the current Date
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * returns the name
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * returns the score
	 * 
	 * @return
	 */
	public int getScore() {
		return this.score;
	}

}