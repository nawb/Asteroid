import java.io.*;
import java.util.*;
public class HighScore {
	String name;
	int score;
	public HighScore(String name, int sc) {
		this.name = name;
		this.score = sc;
	}
	public String printScore() {
		return "   "+name+" "+String.format("%02d", score);
	}
}