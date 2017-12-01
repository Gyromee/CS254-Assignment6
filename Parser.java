import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
    private int wordNum;
    private boolean isReverse;
    private boolean checkedFirstAddress;
    private boolean reachedEndOfData;
    private int k;
    private String lowerRange;
    private String upperRange;
    private String previousRelTime;
    private DataRate dataRate;
    private boolean readSpeed;
    
    //Constructor
    public Parser(String filename) {
        this.filename = filename;
        reachedEndOfData = false;
        dataRate = new DataRate();
        readSpeed = false;
    }
    
    //Method to parse the file's data
    public void parse(){
    //Read one line at a time and analyze it
    try (BufferedReader br = new BufferedReader(new FileReader(filename))){
    	BufferedWriter wr = new BufferedWriter(new FileWriter("parsed_command_data.txt"));
    	lineNumber = 0;
        
        while((line = br.readLine())!= null) {
            
            splitLine = line.trim().split("\\s+");
             
            if (previousRelTime != null)
                dataRate.addSeconds(previousRelTime);
               
            
            if(readSpeed == true) {
             	previousRelTime = splitLine[2];
             	readSpeed = false;
            }
            else
            	previousRelTime = null;
        
            lineNumber++;
            
            //Catches S-to-D command
            if(splitLine[6].equals("40000810")) {
            	command = "S-to-D";
            	parseCommandData();
            	lowerRange = "40000818";
            	upperRange = "40000818";
            	dataRate.addSize(splitLine[8]);
 	            dataRate.addType(splitLine[9], "S-to-D");
            	//Output for the command
                wr.write("\nLine " + lineNumber + ": " + cycle + " "
                                    + command + " command: " + numOfWords + " words"); 
                if (numOfWords == 0) {
             	    wr.newLine();
             	
                }
                wr.newLine();
                readSpeed = true;
            }
            //Catches D-to-S
            if(splitLine[6].equals("40000C18")) {
            	command = "D-to-S";
            	parseCommandData();
            	lowerRange = "40000C20";
            	upperRange = "4000101C";
            	dataRate.addSize(splitLine[8]);
 	            dataRate.addType(splitLine[9], "D-to-S");
 	            
 	
            	//Output for the command
                wr.write("\nLine " + lineNumber + ": " + cycle + " "
                                    + command + " command: " + numOfWords + " words"); 
                if (numOfWords == 0) {
             	    wr.newLine();   
                }
                wr.newLine();
                readSpeed = true;      
            }
           
            //Parse the data of the command   
            while (wordNum < numOfWords) {  
            		
            		line = br.readLine();
                    lineNumber++;
                    splitLine = line.trim().split("\\s+");
                    address = splitLine[6]; 
                    data = splitLine[7];
                    
                    if (previousRelTime != null)
                    dataRate.addSeconds(previousRelTime);
                    
                    dataRate.addSize(splitLine[8]);
    	            dataRate.addType(splitLine[9], "S-to-D");
    	            
                    previousRelTime = splitLine[2];
                    
                    
                    //Checks if the first address in the command data is the largest 
                    if (checkedFirstAddress == false) {
                        //If the first command data hex address is equal to the greatest possible range
                    	//then we know the words are in reverse
                    	if (Integer.parseInt(address, 16) == ((Integer.parseInt(lowerRange, 16)) + numOfWords*2 - 4)) {
                            isReverse = true;
                            
                    	}
                    	checkedFirstAddress = true;
                    }
                    
                    if(isReverse == true)
                        k = 1;
                    else
                        k = 0;
                    //Check if our address is between within the given range for command data
                    if((Integer.parseInt(lowerRange, 16)) <= (Integer.parseInt(address, 16))
                            &&  (Integer.parseInt(address, 16)) < ((Integer.parseInt(upperRange, 16)) + numOfWords*2)) {
                            ArrayList<String> wordsAsArray = splitWords(data, 4);
                            for (int i = 0; i < wordsAsArray.size(); i++) {
                                
                                //Split our split word into each split. Such as 1093 is 1,0,9,3 in an array
                                ArrayList<String> splitWord = splitWords(wordsAsArray.get(k), 1);
                                String bin = "";
                                //Convert hex to binary, and formats it with 0's
                                for (String x: splitWord)
                                {
                                    int hex = Integer.parseInt(x, 16);
                                    bin += String.format("%4s", Integer.toBinaryString(hex)).replace(' ', '0');
                                    
                                }
                                
                                //Parse word 0 and word 1
                                if (address.equals( "40000818") || address.equals("40000C20")){
                                    //Word 0
                                    if (k == 0) {
                                        wr.write("Line " + lineNumber + ": Word 0: " );                                         
                                        wr.write("Rec_Ctrl = " + Integer.parseInt(bin.substring(1,3)));
                                        if (bin.matches("^.00[01]+")) {
                                        	wr.write(" (no recording)");
                                        	wr.newLine();
                                        }
                                        else if (bin.matches("^.10[01]+")) {
                                        	wr.write(" (no processing)");
                                        	wr.newLine();
                                        }
                                        else if (bin.matches("^.11[01]+")) {
                                        	wr.write(" (processing and recording)");
                                        	wr.newLine();
                                        }
                                        else {
                                        	wr.write(" (unknown)");
                                        	wr.newLine();
                                        }
                                    }
                                    //Word 1
                                    if (k == 1) {
                                        wr.write("Line " + lineNumber + ": Word 1: " );                                         
                                        wr.write("Cmd_Type = " + Integer.parseInt(bin.substring(0,3), 2));
                                        if (bin.matches("^100[01]+")) {
                                        	wr.write(" (Type C)");
                                        	wr.newLine();
                                        }
                                        else if (bin.matches("^101[01]+")) {
                                        	wr.write(" (Type B)");
                                        	wr.newLine();
                                        }
                                        else if (bin.matches("^110[01]+")) {
                                        	wr.write(" (Type C)");
                                        	wr.newLine();
                                        }
                                        else {
                                        	wr.write(" (unknown)");
                                        	wr.newLine();
                                        }
                                    }
                                }
                                //Parse word 4 and 5
                                if (address.equals("40000820") || address.equals("40000C28")){
	                                //Word 4
	                                if (k == 0) {
	                                    wr.write("Line " + lineNumber + ": Word 4: " );                                         
	                                    wr.write("Rec_Raw = " + Integer.parseInt(bin.substring(15,16)));
	                                    if (bin.matches("^.{15}0")) {
	                                    	wr.write(" (disable)");
	                                    	wr.newLine();
	                                    }
	                                    else if (bin.matches("^.{15}1")) {
	                                    	wr.write(" (enable)");
	                                    	wr.newLine();
	                                    }
	                                    else {
	                                    	wr.write(" (unknown)");
	                                    	wr.newLine();
	                                    }
	                                }
	                                //Word 5
                                	if (k == 1) {
                                		wr.write("Line " + lineNumber + ": Word 5: " );                                         
                                		wr.write("Cmd_ID = " +(Integer.parseInt(bin.substring(9,16), 2)));
                                		wr.newLine();
                                	}
                                }
                                //Parse Word 10
                                if (address.equals("4000082C") || address.equals("40000C34")){
                                	//Word 10
                                	if (k == 0) {
                                         wr.write("Line " + lineNumber + ": Word 10: " );                                            
                                         wr.write("Num_Responses = " + Integer.parseInt(bin.substring(0,5), 2));
                                         wr.newLine();
                                	}
                                }
                                //Parse word 15
                                if (address.equals("40000C3C")){
	                            	//Word 15
	                            	if (k == 0){
	                            		 wr.write("Line " + lineNumber + ": Word 15: " );  
	                            		 wr.write("Reset_Enable = " + Integer.parseInt(bin.substring(13,14)));
	                            		 if(bin.matches("^[01]{13}1[01][01]")) {
	                            			 wr.write(" (Enable)");
	                            			 wr.newLine();
	                            		 }
	                            		 else if (bin.matches("^[01]{13}0[01][01]")) {
	                            			 wr.write(" (Disable)");
	                            			 wr.newLine();
	                            		 }
	                            		 else {
	                            			 wr.write("(unknown)");
	                            			 wr.newLine();
	                            		 }
	                            	}
                                }
                                //Parse Word 22
                                if (address.equals("40000C4C")){
                                	//Word 22
                                	if (k == 0){
                                		 wr.write("Line " + lineNumber + ": Word 22: " );  
                                		 wr.write("Direction = " + Integer.parseInt(bin.substring(12,13)));
                                		 if(bin.matches("^[01]{12}1[01]{3}")) {
                                			 wr.write(" (Left)");
                                			 wr.newLine();
                                		 }
                                		 else if (bin.matches("^[01]{12}0[01]{3}")) {
                                			 wr.write(" (Right)");
                                			 wr.newLine();
                                		 }
                                		 else {
                                			 wr.write("(unknown)");
                                			 wr.newLine();
                                		 }
                                	}
                                }
                                //Parse Word 32
                                if (address.equals("40000C60")) {
                                	//Word 32
                                	if (k == 0){
                                		 wr.write("Line " + lineNumber + ": Word 32: " );  
                                		 wr.write("Num_Samples = " + (Integer.parseInt(bin.substring(1,16), 2)));
                                		 wr.newLine();
                                	}
                                }
                                //Parse Word 37
                                if (address.equals("40000C68")) {
                                	//Word 37
                                	if(k == 0){
                                		wr.write("Line " + lineNumber + ": Word 37: " );  
                                		wr.write("Parity = " + Integer.parseInt(bin.substring(0,1)));
                                		if (bin.matches("^0[01]{15}")) {
                                			wr.write(" (even)");
                                			wr.newLine();
                                		}
                                		
                                		else if (bin.matches("^1[01]{15}")) {
                                			wr.write(" (odd)");
                                			wr.newLine();
                                		}
                                		else {
                                			wr.write("(unknown)");
                                			wr.newLine();
                                		}
                                	}
                            	}
                                //Parse Word 38
                                if (address.equals("40000C6C")) {
                                	//Word 38
                                	if (k == 0){
                                		wr.write("Line " + lineNumber + ": Word 38: " ); 
                                		wr.write("Test = " + Integer.parseInt(bin.substring(1,2)));
                                		if (bin.matches("^[01]0[01]{14}")) {
                                			wr.write(" (disable)");
                                			wr.newLine();
                                		}
                                		else if (bin.matches("^[01]1[01]{14}")) {
                                			wr.write(" (enable)");
                                			wr.newLine();
                                		}
                                		else {
                                			wr.write("(unknown)");
                                			wr.newLine();
                                		}
                                	}
                                }
                                //Parse Word 40 and 41
                                if (address.equals("40000C70")) {
                                	//Word 40
                                	if (k == 0){
                                		wr.write("Line " + lineNumber + ": Word 40: " ); 
                                		wr.write("Ctrl_Enable = " + Integer.parseInt(bin.substring(8,9)));
                                		if (bin.matches("^[01]{8}0[01]{7}")) {
                                			wr.write(" (disable)");
                                			wr.newLine();
                                		}
                                		else if (bin.matches("^[01]{8}1[01]{7}")) {
                                			wr.write(" (enable)");
                                			wr.newLine();
                                		}
                                		else {
                                			wr.write(" (unknown)");
                                			wr.newLine();
                                		}
                                	}   
                                	//Word 41
                                	if (k == 1){
                                		wr.write("Line " + lineNumber + ": Word 41: " );
                                		wr.write("Code = " +(Integer.parseInt(bin.substring(1,8), 2)));
                                		wr.newLine();
                                	}
                                	
                                }
                               
                               
                                if (isReverse == true)
                                    k--;
                                else
                                    k++;
                                wordNum++;
                                if (wordNum == (numOfWords)) {
                        			reachedEndOfData = true;
                        			readSpeed = true;
                                }
                        }
                    }   
                   
                }
            if(reachedEndOfData) {
            	wr.newLine();
            	reachedEndOfData = false;
            }
        }
       wr.write("Read S-to-D: " + String.format("%.2f", dataRate.total()[0]) + " Megabits/sec");
       wr.newLine();
       wr.write("Read D-to-S: " + String.format("%.2f", dataRate.total()[2]) +" Megabits/sec");
       wr.newLine();
       wr.write("Write S-to-D: " + String.format("%.2f", dataRate.total()[1]) + " Megabits/sec");
       wr.newLine();
       wr.write("Write D-to-S: " + String.format("%.2f", dataRate.total()[3]) + " Megabits/sec");
       wr.close();
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
    
    //Method that parses command data
    public void parseCommandData() {
    	//Read the type of cycle
        if (splitLine[9].equals("Wr")) cycle = "Write";
        else if (splitLine[9].equals("Rd")) cycle = "Read";
        //Read the data
        data = splitLine[7];
        numOfWords = Integer.parseInt(data,16) / 2;

        wordNum = 0;
        isReverse = false;
        checkedFirstAddress = false;
        k = 0;
    }
  
}
