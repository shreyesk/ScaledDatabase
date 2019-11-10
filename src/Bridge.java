import java.util.Scanner;

public class Bridge {

	public static void main(String[] args) {
		DatabaseComplex dc = new DatabaseComplex("Info.txt");
		Scanner scan = new Scanner(System.in);
		while(true) {
			String line = scan.nextLine().toLowerCase();
			String[] components = line.split("\t");
			String output = dc.decide(components);
			System.out.println(output);
		}
	}	
}
