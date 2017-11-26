import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Driver{

	public static void main(String[] args) {
		int lineNumber;
		int numOfWords;
		String line;
		String[] splitLine = null;
		String cycle = "";
		String command;
		String data;
		String address;
		
		//Read one line at a time and analyze it
		try (BufferedReader br = new BufferedReader(new FileReader("test_data.log"))){
			lineNumber = 0;
			reading:
			while((line = br.readLine())!= null) {
				lineNumber++;
		    	splitLine = line.trim().split("\\s+");
		    	
		    	//S-to-D command
		    	if(splitLine[6].equals("40000810")) {
		    		//Read the type of cycle
		    		if (splitLine[9].equals("Wr")) cycle = "Write";
		    		else if (splitLine[9].equals("Rd")) cycle = "Read";
		    		//Set the type of command
		    		command = "S-to-D";
		    		//Read the data
		    		data = splitLine[7];
		    		numOfWords = Integer.parseInt(data,16) / 2;
		    		//Output for the command
		    		System.out.println("Line " + lineNumber + ": " + cycle + " "
		    							+ command + " comand: " + numOfWords + " words.");
		    		//Parse the data of the S-to-D command
		    		int word = 0;
		    		while (word < numOfWords) {
		    			line = br.readLine();
		    			lineNumber++;
				    	splitLine = line.trim().split("\\s+");
		    			address = splitLine[6];
		    			data = splitLine[7];
		    			if((Integer.parseInt("40000818", 16) / 2) <= (Integer.parseInt(address, 16) / 2)
		    				&&	(Integer.parseInt(address, 16) / 2) <= ((Integer.parseInt("40000818", 16) / 2) + numOfWords)) {
		    				ArrayList<String> words = splitWords(data);
		    				for (int i = 0; i < words.size(); i++) {
		    					
		    					/* <Conditional statements that parse
		    					 * each word using the word fields
		    					 * chart> goes here	
		    					 */
		    					System.out.println("Line " + lineNumber + 
		    										": Word " + word + ": " );
		    					word++;
		    				}
		    			}	
		    		}
		    	}
		    		
	    		//D-to-S command 
	    		if(splitLine[6].equals("40000C18")) {
		    		//Read the type of cycle
		    		if (splitLine[9].equals("Wr")) cycle = "Write";
		    		else if (splitLine[9].equals("Rd")) cycle = "Read";
		    		//Set the type of command
		    		command = "D-to-S";
		    		//Read the data
		    		data = splitLine[7];
		    		numOfWords = Integer.parseInt(data,16) / 2;
		    		//Output for the command
		    		System.out.println("Line " + lineNumber + ": " + cycle + " "
		    							+ command + " comand: " + numOfWords + " words.");
		    		
		    		//Parse the data for D-to-S command
		    		/* <Conditional statements similar to the ones
		    		 * in the S-to-D command parsing> goes here 
		    		 */		    		
	    		}
	    		
	    		//This line is only here so your console isn't thousands of
	    		//lines long while testing
	    		//if (lineNumber > 300) break reading;
		    }
		   }catch (Exception e){
		     e.printStackTrace();
		   }
	}
	
	//Method that splits a string into separate strings of length 4
	public static ArrayList<String> splitWords(String string) {
		ArrayList<String> splitString = new ArrayList<String>();
		int index = 0;
		while (index < string.length()) {
		splitString.add(string.substring(index, Math.min(index + 4,string.length())));
		index += 4;
		}
		return splitString;
	}
}
