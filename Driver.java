public class Driver {
	public static void main(String[] args) {
		String filename;
		filename = args[0];
		Parser p1 = new Parser(filename);
		p1.parse();
	}
}