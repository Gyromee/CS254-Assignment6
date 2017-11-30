import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
		System.out.println("Please enter file name");
		Scanner s = new Scanner(System.in);
		String filename = s.nextLine();
		Parser p1 = new Parser(filename);
		p1.parse();
		
		s.close();
		
	}
}