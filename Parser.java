import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser{

    private int lineNumber;
    private int numOfWords;
    private String line;
    private String[] splitLine = null;
    private String cycle = "";
    private String command;
    private String data;
    private String address; 
    private String filename;
    
    //Constructor
    public Parser(String filename) {
        this.filename = filename;
    }
    
    //Method to parse the file's data
    public void parse(){
    //Read one line at a time and analyze it
    try (BufferedReader br = new BufferedReader(new FileReader(filename))){
        lineNumber = 0;
        
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
                System.out.println("\nLine " + lineNumber + ": " + cycle + " "
                                    + command + " comand: " + numOfWords + " words.");
                //Parse the data of the S-to-D command
                int wordNum = 0;
                boolean isReverse = false;
                boolean checkedFirstAddress = false;
                int k = 0;
                
                while (wordNum < numOfWords) {  
                    line = br.readLine();
                    lineNumber++;
                    splitLine = line.trim().split("\\s+");
                    address = splitLine[6]; 
                    data = splitLine[7];
       
                    if (checkedFirstAddress == false) {
                        if (Integer.parseInt(address, 16) == ((Integer.parseInt("40000818", 16)) + numOfWords*2 - 4)) {
                            isReverse = true;
                        }
                        checkedFirstAddress = true;
                    }
                    
                    if(isReverse == true)
                        k = 1;
                    else
                        k = 0;
                    //Check if our address is between 0x40000818 to 0x40000C14 
                    if((Integer.parseInt("40000818", 16)) <= (Integer.parseInt(address, 16))
                            &&  (Integer.parseInt(address, 16)) < ((Integer.parseInt("40000818", 16)) + numOfWords*2)) {
                            ArrayList<String> wordsAsArray = splitWords(data, 4);
                            for (int i = 0; i < wordsAsArray.size(); i++) {
                                
                                /* <Conditional statements that parse
                                 * each word using the word fields
                                 * chart> goes here 
                                 */
                                
                                //Split our split word into each split. Such as 1093 is 1,0,9,3 in an array
                                ArrayList<String> splitWord = splitWords(wordsAsArray.get(k), 1);
                                String bin = "";
                                //Convert hex to binary, and formats it with 0's
                                for (String x: splitWord)
                                {
                                    int hex = Integer.parseInt(x, 16);
                                    bin += String.format("%4s", Integer.toBinaryString(hex)).replace(' ', '0');
                                    
                                }
    
                                switch (address) {
                                    case "40000818": 
                                            //Word 0
                                            if (k == 0) {
                                                System.out.print("Line " + lineNumber + ": Word 0: " );                                         
                                                System.out.print("Rec_ctrl = " + Integer.parseInt(bin.substring(1,3)));
                                                if (bin.matches("^.00[01]+")) System.out.println(" (no recording)");
                                                else if (bin.matches("^.10[01]+")) System.out.println(" (no processing)");
                                                else if (bin.matches("^.11[01]+")) System.out.println(" (processing and recording)");
                                                else System.out.println(" (unknown)");
                                            }
                                            //Word 1
                                            if (k == 1) {
                                                System.out.print("Line " + lineNumber + ": Word 1: " );                                         
                                                System.out.print("Cmd_Type = " + Integer.parseInt(bin.substring(0,3)));
                                                if (bin.matches("^100[01]+")) System.out.println("type A");
                                                else if (bin.matches("^101[01]+")) System.out.println("type B");
                                                else if (bin.matches("^110[01]+")) System.out.println("type C");
                                                else System.out.println(" (unknown)");
                                            }
                                        break;
                                    case "40000820":
                                        //Word 4
                                        if (k == 0) {
                                            System.out.print("Line " + lineNumber + ": Word 4: " );                                         
                                            System.out.print("Rec_Raw = " + Integer.parseInt(bin.substring(15,16)));
                                            if (bin.matches("^.{15}0")) System.out.println(" (disable)");
                                            else if (bin.matches("^.{15}1")) System.out.println(" (enable)");
                                            else System.out.println(" (unknown)");
                                        }
                                        //Word 5
                                        if (k == 1) {
                                            System.out.print("Line " + lineNumber + ": Word 5: " );                                         
                                            System.out.println("Cmd_id = " +(Integer.parseInt(bin.substring(9,16), 2)));
                                        }
                                        break;
                                    case "4000082C":
                                        //Word 10
                                        if (k == 0) {
                                            System.out.print("Line " + lineNumber + ": Word 10: " );                                            
                                            System.out.println("Num_Responses = " + Integer.parseInt(bin.substring(0,5)));
                                        }
                                        break;
                                    default: 
                                        break;
                                }
                                
                                wordNum++;
                                //System.out.println("k: " + k + ",i: " + i + ",bin:" + bin);
                                if (isReverse == true)
                                    k--;
                                else
                                    k++;
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
                System.out.println("\nLine " + lineNumber + ": " + cycle + " "
                                    + command + " comand: " + numOfWords + " words.");
                
                /* *****************************************************
                *********Parse the data for D-to-S command*************
                ***************************************************** */
                
                int wordNum = 0;
                boolean isReverse = false;
                boolean checkedFirstAddress = false;
                int k = 0;
                
                while (wordNum < numOfWords) {  
                    line = br.readLine();
                    lineNumber++;
                    splitLine = line.trim().split("\\s+");
                    address = splitLine[6]; 
                    data = splitLine[7];
       
                    if (checkedFirstAddress == false) {
                        if (Integer.parseInt(address, 16) == ((Integer.parseInt("40000C20", 16)) + numOfWords*2 - 4)) {
                            isReverse = true;
                        }
                        checkedFirstAddress = true;
                    }
                    
                    if(isReverse == true)
                        k = 1;
                    else
                        k = 0;
                    //Check if our address is between 0x40000818 to 0x40000C14 
                    if((Integer.parseInt("40000C20", 16)) <= (Integer.parseInt(address, 16))
                            &&  (Integer.parseInt(address, 16)) < ((Integer.parseInt("4000101C", 16)) + numOfWords*2)) {
                            ArrayList<String> wordsAsArray = splitWords(data, 4);
                            for (int i = 0; i < wordsAsArray.size(); i++) {
                                
                                /* <Conditional statements that parse
                                 * each word using the word fields
                                 * chart> goes here 
                                 */
                                
                                //Split our split word into each split. Such as 1093 is 1,0,9,3 in an array
                                ArrayList<String> splitWord = splitWords(wordsAsArray.get(k), 1);
                                String bin = "";
                                //Convert hex to binary, and formats it with 0's
                                for (String x: splitWord)
                                {
                                    int hex = Integer.parseInt(x, 16);
                                    bin += String.format("%4s", Integer.toBinaryString(hex)).replace(' ', '0');
                                    
                                }
    
                                switch (address) {
                                    case "40000C20": 
                                            //Word 0
                                            if (k == 0) {
                                                System.out.print("Line " + lineNumber + ": Word 0: " );                                         
                                                System.out.print("Rec_ctrl = " + Integer.parseInt(bin.substring(1,3)));
                                                if (bin.matches("^.00[01]+")) System.out.println("(no recording)");
                                                else if (bin.matches("^.10[01]+")) System.out.println(" (no processing)");
                                                else if (bin.matches("^.11[01]+")) System.out.println(" (processing and recording)");
                                                else System.out.println(" (unknown)");
                                            }
                                            //Word 1
                                            if (k == 1) {
                                                System.out.print("Line " + lineNumber + ": Word 1: " );                                         
                                                System.out.print("Cmd_Type = " + Integer.parseInt(bin.substring(0,3)));
                                                if (bin.matches("^100[01]+")) System.out.println("type A");
                                                else if (bin.matches("^101[01]+")) System.out.println("type B");
                                                else if (bin.matches("^110[01]+")) System.out.println("type C");
                                                else System.out.println(" (unknown)");
                                            }
                                        break;
                                    case "40000C28":
                                        //Word 4
                                        if (k == 0) {
                                            System.out.print("Line " + lineNumber + ": Word 4: " );                                         
                                            System.out.print("Rec_Raw = " + Integer.parseInt(bin.substring(15,16)));
                                            if (bin.matches("^.{15}0")) System.out.println(" (disable)");
                                            else if (bin.matches("^.{15}1")) System.out.println(" (enable)");
                                            else System.out.println(" (unknown)");
                                        }
                                        //Word 5
                                        if (k == 1) {
                                            System.out.print("Line " + lineNumber + ": Word 5: " );                                         
                                            System.out.println("Cmd_id = " +(Integer.parseInt(bin.substring(9,16), 2)));
                                        }
                                        break;
                                    case "40000C34":
                                        //Word 10
                                        if (k == 0) {
                                            System.out.print("Line " + lineNumber + ": Word 10: " );                                            
                                            System.out.println("Num_Responses = " + Integer.parseInt(bin.substring(0,5), 2));
                                        }
                                        break;
                                    case "40000C3C":
                                    	//Word 15
                                    	if (k == 0)
                                    	{
                                    		 System.out.print("Line " + lineNumber + ": Word 15: " );  
                                    		 System.out.print("Reset_Enable = " + Integer.parseInt(bin.substring(13,14)));
                                    		 if(bin.matches("^[01]{13}1[01][01]")) System.out.println(" (Enable)");
                                    		 else if (bin.matches("^[01]{13}0[01][01]")) System.out.println(" (Disable)");
                                    		 else System.out.println("(unknown)");
                                    	}
                                    	break;
                                    case "40000C4C":
                                    	//Word 22
                                    	if (k == 0)
                                    	{
                                    		 System.out.print("Line " + lineNumber + ": Word 22: " );  
                                    		 System.out.print("Direction = " + Integer.parseInt(bin.substring(12,13)));
                                    		 if(bin.matches("^[01]{12}1[01]{3}")) System.out.println(" (left)");
                                    		 else if (bin.matches("^[01]{12}0[01]{3}")) System.out.println(" (right)");
                                    		 else System.out.println("(unknown)");
                                    	}
                                    	break;
                                    case "40000C60":
                                    	//Word 32
                                    	if (k == 0)
                                    	{
                                    		 System.out.print("Line " + lineNumber + ": Word 32: " );  
                                    		 System.out.println("Num_Samples = " + (Integer.parseInt(bin.substring(1,16), 2)));
                                    	}
                                    	break;
                                    case "40000C68":
                                    	//Word 37
                                    	if(k == 0)
                                    	{
                                    		System.out.print("Line " + lineNumber + ": Word 37: " );  
                                    		System.out.print("Parity = " + Integer.parseInt(bin.substring(0,1)));
                                    		if (bin.matches("^0[01]{15}")) System.out.println(" (even)");
                                    		else if (bin.matches("^1[01]{15}")) System.out.println(" (odd)");
                                    		else System.out.println("(unknown)");
                                    	}
                                    	break;
                                    case "40000C6C":
                                    	//Word 38
                                    	if (k == 0)
                                    	{
                                    		System.out.print("Line " + lineNumber + ": Word 38: " ); 
                                    		System.out.print("Test = " + Integer.parseInt(bin.substring(1,2)));
                                    		if (bin.matches("^[01]0[01]{14}")) System.out.println(" (disable)");
                                    		else if (bin.matches("^[01]1[01]{14}")) System.out.println(" (enable)");
                                    		else System.out.println("(unknown)");
                                    	}
                                    	break;
                                    case "40000C70":
                                    	//Word 40
                                    	if (k == 0)
                                    	{
                                    		System.out.print("Line " + lineNumber + ": Word 40: " ); 
                                    		System.out.print("Test = " + Integer.parseInt(bin.substring(8,9)));
                                    		if (bin.matches("^[01]{8}0[01]{7}")) System.out.println(" (disable)");
                                    		else if (bin.matches("^[01]{8}1[01]{7}")) System.out.println(" (enable)");
                                    		else System.out.println(" (unknown)");
                                    	}
                                    	if (k == 1)
                                    	{
                                    		System.out.print("Line " + lineNumber + ": Word 41: " );
                                    		System.out.println("Code = " +(Integer.parseInt(bin.substring(1,8), 2)));
                                    	}
                                    	break;
                                    default: 
                                        break;
                                }
                                
                                wordNum++;
                                //System.out.println("k: " + k + ",i: " + i + ",bin:" + bin);
                                if (isReverse == true)
                                    k--;
                                else
                                    k++;
                        }
                    }   
                }
            }
            
            //This line is only here so your console isn't thousands of
            //lines long while testing
             //if (lineNumber > 800) break reading;
        }
       }catch (Exception e){
         e.printStackTrace();
       }
}

    //Method that splits a string into separate strings of length 4
    private ArrayList<String> splitWords(String string, int pos){
        ArrayList<String> splitString = new ArrayList<String>();
        int index = 0;
        while (index < string.length()) {
        splitString.add(string.substring(index, Math.min(index + pos,string.length())));
        index += pos;
        }
        return splitString;
    }
}




