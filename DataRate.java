import java.util.ArrayList;

public class DataRate {
	
	private String secondsUnit;
	private String sizeUnit;
	private double totalMicroseconds;
	private double[] readStoD;
	private double[] readDtoS;
	private double[] writeStoD;
	private double[] writeDtoS;
	private ArrayList<String> commandType;
	private ArrayList<Double> speed;
	private ArrayList<Double> bits;
	
	public DataRate(){
		totalMicroseconds = 0;
		speed = new ArrayList<Double>();
		bits = new ArrayList<Double>();
		commandType = new ArrayList<String>();
		readStoD = new double[2];
		readDtoS = new double[2];
		writeStoD = new double[2];
		writeDtoS = new double[2];
	}
	
	public ArrayList<Double> getSpeed() {
		return speed;
	}
	public ArrayList<Double> getBits() {
		return bits;
	}
	public ArrayList<String> getCommandType() {
		return commandType;
	}

	public void addSeconds(String relTime) {
		speed.add(convertSeconds(relTime, Double.parseDouble(relTime.split("[us]|[ns]|[ms]")[0])));
	}
	public void addSize(String size) {
		bits.add(Double.parseDouble(size.split("D")[1]));
	}
	
	public void addType(String cycle, String type) {
		if (cycle.equals("Rd") && type.equals("S-to-D")) {
			commandType.add("readStoD");
		}
		else if (cycle.equals("Wr") && type.equals("S-to-D")) {
			commandType.add("writeStoD");
		}
		else if (cycle.equals("Rd") && type.equals("D-to-S")) {
			commandType.add("readDtoS");
		}
		else if (cycle.equals("Wr") && type.equals("D-to-S")) {
			commandType.add("writeDtoS");
		}
		
	}
	
	public double convertSeconds(String relTime, double temp) {
		double converted = 0;
		
		if (relTime.matches("^.+ns")){
			converted += temp / 1000;
			
		}
		else if (relTime.matches("^.+us")) {
			converted += temp;
		}
		else if (relTime.matches("^.+ms")){
			converted += temp * 100;
		}
		
		return converted;
	}
	public double[] total() {
		
		double[] total = new double[4];
		//speed          //bits
		readStoD[0] = 0; readStoD[1] = 0;
		readDtoS[0] = 0; readDtoS[1] = 0;
		writeStoD[0] = 0; writeStoD[1] = 0; 
		writeDtoS[0] = 0; writeDtoS[1] = 0;
		
		for (int i = 0; i < bits.size(); i++) {
			if(commandType.get(i).equals("readStoD")){
//				System.out.print("type: " + commandType.get(i));
//				System.out.print("    bits: " + bits.get(i));
//				System.out.println("    speed: " + speed.get(i));
				readStoD[0] += speed.get(i);
				readStoD[1] += bits.get(i);
			}
			else if(commandType.get(i).equals("writeStoD")){
				
				writeStoD[0] += speed.get(i);
				writeStoD[1] += bits.get(i);
			}
			else if(commandType.get(i).equals("readDtoS")){

				readDtoS[0] += speed.get(i);
				readDtoS[1] += bits.get(i);
			}
			else if(commandType.get(i).equals("writeDtoS")){	
				writeDtoS[0] += speed.get(i);
				writeDtoS[1] += bits.get(i);
			}
	
		}
			
			total[0] =  readStoD[1] / readStoD[0];
			total[1] =  writeStoD[1] / writeStoD[0];
			total[2] =  readDtoS[1] / readDtoS[0];
			total[3] =  writeDtoS[1] / writeDtoS[0];
	
		return total;	
	}	
}



	

