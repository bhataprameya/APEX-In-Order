import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Simulator {
/* Class to simulate all the pipeline functionality and features and process them in-order
 * The involves dependency checking, forwarding for all the resistors and the flags
 */

	public static String FILENAME = "instructions.txt";

	//Content of each stage of the pipeline is stored in the form of array list
	ArrayList<DisplayStages> storeStagesList = new ArrayList<>();
	
	//Architectural Register are also created and stores in form of a array list
	ArrayList<ArchitecturalRegisterState> archRegStates = new ArrayList<>();
	
	//Memory is also created and stores in form of a array list
	ArrayList<MemoryRegisterState> memRegStates = new ArrayList<>();
	
	//Each instruction in the file is stored in the form of the HashMap
	private ArrayList<Instruction> instructions;
	private Map<String, Instruction> pipelineStatus= new HashMap<String, Instruction>();





	int PC;
	int cycles;
	int haltBreak;
	String decodeopcode;

	int zeroFlagSelect=-1;

	int cycle_counter;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
/* Program takes 3 type of arguments
 * 1- initialize: initializes the pipeline
 * 2- simulate n: Simulates the pipeline for the clock cycles given n.
 * 3- display: Displays the content of the pipeline for all stages o the sing stage based on input given along with the register contents and the first 100 memory locations used 
 */
		String input;

		Simulator simulator = new Simulator();

		Scanner scanner = new Scanner(System.in);


		System.out.println("------------------------------------------------");
		System.out.println("Welcome to APEX Simulator");
		System.out.println("------------------------------------------------");
		System.out.println("Enter Commnads");

		do 
		{
			System.out.print(": ");

			input = scanner.nextLine();

			String[] inputParts = input.split(" ");

			switch (inputParts[0]) {
			
			// Initializes the pipeline for simulation
			case "initialize":
				simulator.initialize();
				System.out.println("Initialization Done.");
				break;
				
				//Simulates the pipeline for n clock cycle 
			case "simulate":
				simulator.simulate(Integer.parseInt(inputParts[1]));
				System.out.println("Simulation Done.");
				break;
				
				//Displays the content of the pipeline for each or a single stage based on the input given
			case "display":
				
				if(inputParts.length == 1) 
				{
					simulator.showStagesContentAll();
				}
				else 
				{
					simulator.showStagesContent(inputParts[1]);
				}
				break;
			default :
				if(!input.equalsIgnoreCase("Quit"))
					System.out.println("Wrong commnad.");
				else
					System.out.println("Quitting...");
				break;
			}           
		} while(!input.equalsIgnoreCase("Quit"));
	}


//Function to empty the pipeline
	public Simulator() {

		pipelineStatus.put("Fetch", getEmpty());
		pipelineStatus.put("Decode", getEmpty());
		pipelineStatus.put("Execute1", getEmpty());
		pipelineStatus.put("Execute2", getEmpty());
		pipelineStatus.put("Branch", getEmpty());
		pipelineStatus.put("Delay", getEmpty());
		pipelineStatus.put("Memory", getEmpty());
		pipelineStatus.put("WriteBack",getEmpty());

	}


	public Instruction getEmpty() {
		return null;
	}

//Initilize function that resets counter and points the PC to first instruction in the file
	public void initialize() {

		pipelineStatus.clear();
		archRegStates.clear();
		memRegStates.clear();
		storeStagesList.clear();
		cycle_counter = 0;
		PC = 4000;
		setArchitectureRegisters();
		cycles =0;
		readInstructionFromFile();
	}


//Function that reads instruction from the file and stores it in the form of HashMap
	private void readInstructionFromFile() {
		try {

			BufferedReader reader = new BufferedReader(new FileReader(FILENAME));

			instructions = new ArrayList<>();
			int instructionNum = 4000;
			String newLine;


			for (int i=0; (newLine = reader.readLine()) != null;i++) {

				newLine = newLine.replace(",","");
				newLine = newLine.replace("#","");
				String[] instParts = newLine.split(" ");
				Instruction inst = new Instruction();
				inst.setInstrNumber(instructionNum);
				inst.setZeroFlag(-1);
				inst.setForward(false); 
				instructionNum += 4;
				instructions.add(inst);
				inst.setOperation(instParts[0]); 
				inst.setStage("Read");
				if (instParts.length == 1)
					continue;
				try {
					Integer.parseInt(instParts[1]);
					inst.setLiteral(Integer.parseInt(instParts[1])); 
				} catch (Exception e) {
					inst.setDestination(instParts[1]);
				} 

				if (instParts.length == 2) 
					continue;
				try {
					Integer.parseInt(instParts[2]);
					inst.setLiteral(Integer.parseInt(instParts[2])); 
				} catch (Exception e) {
					inst.setSource1(instParts[2]);
				}

				if (instParts.length == 3) 
					continue;
				try {
					Integer.parseInt(instParts[3]);
					inst.setLiteral(Integer.parseInt(instParts[3])); 
				} catch (Exception e) {
					inst.setSource2(instParts[3]);
				} 


			}

			reader.close();	

		} catch(Exception e) {

		}
	}

	// Function that initializes the resisters from R0 to R15 anf the memory from 0 to 999 each of 4 bytes
	public void setArchitectureRegisters() {

		for(int i=0; i < 16; i++) {
			archRegStates.add(new ArchitecturalRegisterState("R"+i, 0, false));

		} 
		archRegStates.add(new ArchitecturalRegisterState("X", 0, false));  //Initialize X Register
		archRegStates.add(new ArchitecturalRegisterState("ZF", -1, false));  //Initialize zero Register

		for(int i=0; i < 1000; i++) {
			memRegStates.add(new MemoryRegisterState("M"+i,-(Long.MAX_VALUE)));
		}		

	}

//Simulate function that simulates the pipeline for the execution of the instruction from file given
	private void simulate(int execycles) {

		
		cycle_counter += execycles;

		do
		{
			//Put instructions in the pipeline
			getThroughStages();
			
			//store the content of the pipeline for display purpose for future
			storeStages();
			
			//Function to perform the operations of WB
			if (pipelineStatus.get("WriteBack") != null) {

				Instruction instr=pipelineStatus.get("WriteBack");

				String dest = instr.getDestination();
				ArchitecturalRegisterState regState = getArchitecturalRegister(dest);

				if(regState !=null) {
					regState.setUnderOperation(false); 			//Instruction is no longer under operation
					regState.setForwardValue(false);     		// Value can be no longer forwarded since instruction comes out of pipeline
				}
				if(instr.isTakeValue()) {
					regState.setValue(instr.getResult()); 		// write the resister file with the result of the instruction if it has any
				}			
				if(instr.getOperation().equals("HALT")) {
					haltBreak=1;      							// Set the halt flag if it is encountered in WB for stopping the execution
				}

				if(instr.getOperation().equals("BAL") ) {
					ArchitecturalRegisterState registerX  = getArchitecturalRegister("X");
					registerX.setForwardValue(false);
					registerX.setValue(instr.getInstrNumber() + 4);								// set the value of  X register if the instruction is BAL
				}


				if(instr.getOperation().equals("ADD") ||instr.getOperation().equals("MUL")||instr.getOperation().equals("SUB")||instr.getOperation().equals("AND")||instr.getOperation().equals("OR")||instr.getOperation().equals("EX-OR")) {
					ArchitecturalRegisterState zeroFlagReg  = getArchitecturalRegister("ZF");
					zeroFlagReg.setValue(instr.getZeroFlag());							// Save the value of Zero Flag for the following use
				}


			}

			if (pipelineStatus.get("Memory") != null) {	
				Instruction instruct = pipelineStatus.get("Memory");

				switch (instruct.getOperation()) {
				//Computational logic to perform store operation
				case "STORE": {
					ArchitecturalRegisterState destination = getArchitecturalRegister(instruct.getSource1());
					ArchitecturalRegisterState source1  = getArchitecturalRegister(instruct.getDestination());

					//check if forwarding is needed from WB stage to MEM stage for STORE
					Instruction instruct3 = pipelineStatus.get("WriteBack");
					if(instruct3!= null && instruct3.getDestination()!=null && instruct3.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct3.getOperation().equals("ADD")||instruct3.getOperation().equals("MUL")||instruct3.getOperation().equals("SUB")||instruct3.getOperation().equals("AND")||instruct3.getOperation().equals("OR")||instruct3.getOperation().equals("EX-OR")||instruct3.getOperation().equals("LOAD"))) {
						destination.setValue(instruct3.getResult());
					}
					MemoryRegisterState memory = getMemoryRegister("M" + ((destination.getValue() + instruct.getLiteral()) / 4));
					memory.setValue( source1.getValue());					// Save the value to the Memory Location
					break;
				}

				//Handling LOAD in MEM stage
				case "LOAD": {
					ArchitecturalRegisterState destination  = getArchitecturalRegister(instruct.getDestination());
					destination.setUnderOperation(true);
					ArchitecturalRegisterState source1 = getArchitecturalRegister(instruct.getSource1());
					MemoryRegisterState memory = getMemoryRegister("M" + ((source1.getValue() + instruct.getLiteral()) / 4));
					instruct.setResult((int)memory.getValue());							//Get the value from the memory location
					instruct.setTakeValue(true);
					destination.setForwardValue(true);
					instruct.setForward(false);
					break;
				}

				}
			}

			//Perform all the arithmetic operation in the ALU2
			if (pipelineStatus.get("Execute2") != null) {		
				Instruction instruct = pipelineStatus.get("Execute2");
				Instruction instructEX1 = pipelineStatus.get("Execute1");
				ArchitecturalRegisterState destination = null;
				if (instruct.getDestination() != null) {
					destination = getArchitecturalRegister(instruct.getDestination());
					if(!instruct.getOperation().equalsIgnoreCase("STORE"))
						destination.setUnderOperation(true);
				}


				switch (instruct.getOperation()) {

				case "MOVC": {
					instruct.setResult(instruct.getLiteral());
					instruct.setTakeValue(true);
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);			//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					}
					break;
				} 

				case "ADD": {
					instruct.setResult(instruct.getSource1_Value()+instruct.getSource2_Value());
					instruct.setTakeValue(true);				
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);		//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					}
					if(instruct.getResult()==0)	{									
						instruct.setZeroFlag(0);
					}
					else {
						instruct.setZeroFlag(1);
					}
					break;
				}
				case "MUL": {
					instruct.setResult(instruct.getSource1_Value()*instruct.getSource2_Value());
					instruct.setTakeValue(true);
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);			//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					}
					if(instruct.getResult()==0)	{									
						instruct.setZeroFlag(0);
					}
					else {
						instruct.setZeroFlag(1);
					}
					break;
				}
				case "SUB": {
					instruct.setResult(instruct.getSource1_Value()-instruct.getSource2_Value());
					instruct.setTakeValue(true);
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);			//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					}
					if(instruct.getResult()==0)	{									
						instruct.setZeroFlag(0);
					}
					else {
						instruct.setZeroFlag(1);
					}
					break;
				}
				case "AND": {
					instruct.setResult(instruct.getSource1_Value() & instruct.getSource2_Value());
					instruct.setTakeValue(true);
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);			//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					}
					if(instruct.getResult()==0)	{									
						instruct.setZeroFlag(0);
					}
					else {
						instruct.setZeroFlag(1);
					}
					break;
				}
				case "OR": {
					instruct.setResult(instruct.getSource1_Value() | instruct.getSource2_Value());
					instruct.setTakeValue(true);
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);			//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					};
					if(instruct.getResult()==0)	{									
						instruct.setZeroFlag(0);
					}
					else {
						instruct.setZeroFlag(1);
					}
					break;
				}
				case "EX-OR": {
					instruct.setResult(instruct.getSource1_Value() ^ instruct.getSource2_Value());
					instruct.setTakeValue(true);
					if(instructEX1!= null) {
						if(!instruct.getDestination().equals(instructEX1.getDestination())) {
							destination.setForwardValue(true);			//Set that the value of the instruction is available for forwarding  
							instruct.setForward(false);
						} 
					} else {
						destination.setForwardValue(true);
						instruct.setForward(false);
					}
					if(instruct.getResult()==0)	{								
						instruct.setZeroFlag(0);
					}
					else {
						instruct.setZeroFlag(1);
					}
					break;
				}
				case "STORE":{
					Instruction instruct1 = pipelineStatus.get("Memory");
					Instruction instruct2 = pipelineStatus.get("WriteBack");

					//Set source 1 of the store instruction is available if it has to be obtained form forwarding

					ArchitecturalRegisterState source1 = getArchitecturalRegister(instruct.getDestination());
					if(instruct1!= null && instruct1.getDestination()!=null && instruct1.getDestination().equalsIgnoreCase(source1.getRegName()) && (instruct1.getOperation().equals("ADD")||instruct1.getOperation().equals("MUL")||instruct1.getOperation().equals("SUB")||instruct1.getOperation().equals("AND")||instruct1.getOperation().equals("OR")||instruct1.getOperation().equals("EX-OR")||instruct1.getOperation().equals("LOAD"))) {
						instruct.setSource1_Value(instruct1.getResult());	
					}
					else if(instruct2!= null && instruct2.getDestination()!=null && instruct2.getDestination().equalsIgnoreCase(source1.getRegName()) && (instruct2.getOperation().equals("ADD")||instruct2.getOperation().equals("MUL")||instruct2.getOperation().equals("SUB")||instruct2.getOperation().equals("AND")||instruct2.getOperation().equals("OR")||instruct2.getOperation().equals("EX-OR")||instruct2.getOperation().equals("LOAD"))) {
						instruct.setSource1_Value(instruct2.getResult());	
					}
				}

				}

			}

			if (pipelineStatus.get("Execute1") != null) {					
				Instruction instruct = pipelineStatus.get("Execute1");

				Instruction instruct1 = pipelineStatus.get("Execute2");
				Instruction instruct2 = pipelineStatus.get("Memory");
				Instruction instruct3 = pipelineStatus.get("WriteBack");
				Instruction instruct4 = pipelineStatus.get("Delay");

				ArchitecturalRegisterState destination = null;
				if (instruct.getDestination() != null) {
					destination = getArchitecturalRegister(instruct.getDestination());
					if(!instruct.getOperation().equalsIgnoreCase("STORE"))
						destination.setUnderOperation(true);



				}
				//Forwarding Logic for all the ALU instructions

				if((instruct.isForward()) && (instruct.getOperation().equals("ADD")||instruct.getOperation().equals("MUL")||instruct.getOperation().equals("SUB")||instruct.getOperation().equals("AND")||instruct.getOperation().equals("OR")||instruct.getOperation().equals("EX-OR")||instruct.getOperation().equals("LOAD")||instruct.getOperation().equals("STORE") || instruct.getOperation().equals("MOVC"))) {

					ArchitecturalRegisterState source1 = getArchitecturalRegister(instruct.getSource1());

					if(source1!=null)
					{

						//Check if Source 1 of the instruction can get its value from ALU2 through Forwarding; If yes take that value	
						if(instruct1!= null && instruct1.getDestination()!=null && instruct1.getDestination().equalsIgnoreCase(source1.getRegName()) && (instruct1.getOperation().equals("MOVC")||instruct1.getOperation().equals("ADD")||instruct1.getOperation().equals("MUL")||instruct1.getOperation().equals("SUB")||instruct1.getOperation().equals("AND")||instruct1.getOperation().equals("OR")||instruct1.getOperation().equals("EX-OR"))) {
							instruct.setSource1_Value(instruct1.getResult());
						}
						// else if check if Source 1 of the instruction can get its value from MEM through Forwarding; If yes take that value	
						else if(instruct2!= null && instruct2.getDestination()!=null && instruct2.getDestination().equalsIgnoreCase(source1.getRegName()) && (instruct2.getOperation().equals("MOVC")||instruct2.getOperation().equals("ADD")||instruct2.getOperation().equals("MUL")||instruct2.getOperation().equals("SUB")||instruct2.getOperation().equals("AND")||instruct2.getOperation().equals("OR")||instruct2.getOperation().equals("EX-OR"))) {
							instruct.setSource1_Value(instruct2.getResult());
						}
						// else if check if Source 1 of the instruction can get its value from WB through Forwarding; If yes take that value	
						else if(instruct3!= null && instruct3.getDestination()!=null && instruct3.getDestination().equalsIgnoreCase(source1.getRegName()) && (instruct3.getOperation().equals("MOVC")||instruct3.getOperation().equals("ADD")||instruct3.getOperation().equals("MUL")||instruct3.getOperation().equals("SUB")||instruct3.getOperation().equals("AND")||instruct3.getOperation().equals("OR")||instruct3.getOperation().equals("EX-OR")||instruct3.getOperation().equals("LOAD"))) {
							instruct.setSource1_Value(instruct3.getResult());
						}
						//Else if source 1 is X register 
						else if(source1.getRegName().equalsIgnoreCase("X") &&  source1.isForwardValue()){
							if(instruct4.getOperation().equalsIgnoreCase("BAL")){
								instruct.setSource1_Value(instruct4.getResult());
							}
							else if(instruct2.getOperation().equalsIgnoreCase("BAL")){
								instruct.setSource1_Value(instruct2.getResult());
							}
							else if(instruct3.getOperation().equalsIgnoreCase("BAL")){
								instruct.setSource1_Value(instruct3.getResult());
							}

						}
					}
					ArchitecturalRegisterState source2 = getArchitecturalRegister(instruct.getSource2());
					if(source2!=null){

						//Check if Source 2 of the instruction can get its value from ALU2 through Forwarding; If yes take that value	
						if(instruct1!= null && instruct1.getDestination()!=null && instruct1.getDestination().equalsIgnoreCase(source2.getRegName()) && (instruct1.getOperation().equals("MOVC")||instruct1.getOperation().equals("ADD")||instruct1.getOperation().equals("MUL")||instruct1.getOperation().equals("SUB")||instruct1.getOperation().equals("AND")||instruct1.getOperation().equals("OR")||instruct1.getOperation().equals("EX-OR"))) {
							instruct.setSource2_Value(instruct1.getResult());
						}

						// else if check if Source 2 of the instruction can get its value from MEM through Forwarding; If yes take that value	
						else if(instruct2!= null && instruct2.getDestination()!=null && instruct2.getDestination().equalsIgnoreCase(source2.getRegName()) && (instruct2.getOperation().equals("MOVC")||instruct2.getOperation().equals("ADD")||instruct2.getOperation().equals("MUL")||instruct2.getOperation().equals("SUB")||instruct2.getOperation().equals("AND")||instruct2.getOperation().equals("OR")||instruct2.getOperation().equals("EX-OR"))) {
							instruct.setSource2_Value(instruct2.getResult());
						}
						// else if check if Source 2 of the instruction can get its value from WB through Forwarding; If yes take that value	
						else if(instruct3!= null && instruct3.getDestination()!=null && instruct3.getDestination().equalsIgnoreCase(source2.getRegName()) && (instruct3.getOperation().equals("MOVC")||instruct3.getOperation().equals("ADD")||instruct3.getOperation().equals("MUL")||instruct3.getOperation().equals("SUB")||instruct3.getOperation().equals("AND")||instruct3.getOperation().equals("OR")||instruct3.getOperation().equals("EX-OR")||instruct3.getOperation().equals("LOAD")))  {
							instruct.setSource2_Value(instruct3.getResult());
						}				

						//Else if source 2 is X register 
						else if(source2.getRegName().equalsIgnoreCase("X") && source2.isForwardValue()){
							if(instruct4.getOperation().equalsIgnoreCase("BAL")){
								instruct.setSource2_Value(instruct4.getResult());
							}
							else if(instruct2.getOperation().equalsIgnoreCase("BAL")){
								instruct.setSource2_Value(instruct2.getResult());
							}
							else if(instruct3.getOperation().equalsIgnoreCase("BAL")){
								instruct.setSource2_Value(instruct3.getResult());
							}

						}

					}
				}
				//Do NOT make registers in STORE instruction under operation since all Register in STORE are source registers
				if(instruct!=null && instruct.getOperation().equalsIgnoreCase("STORE"))
					destination.setForwardValue(true);
				else destination.setForwardValue(false);
			}


			//Delay stage
			if (pipelineStatus.get("Delay") != null) {					
				Instruction instruct = pipelineStatus.get("Delay");

			}

			//ALL the Branch Instructions are performed here
			if (pipelineStatus.get("Branch") != null) {					
				Instruction instruct = pipelineStatus.get("Branch");
				switch (instruct.getOperation()) {

				case "BAL": {
					ArchitecturalRegisterState destination  = getArchitecturalRegister(instruct.getDestination());
					ArchitecturalRegisterState registerX  = getArchitecturalRegister("X");
					instruct.setTakeValue(false);

					Instruction instruct1 = pipelineStatus.get("Execute2");
					Instruction instruct2 = pipelineStatus.get("Memory");
					Instruction instruct3 = pipelineStatus.get("WriteBack");

					// check if the the Register value is forwarded from ALU2
					if(instruct1!= null && instruct1.getDestination()!=null && instruct1.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct1.getOperation().equals("MOVC") ||instruct1.getOperation().equals("ADD") ||instruct1.getOperation().equals("MUL")||instruct1.getOperation().equals("SUB")||instruct1.getOperation().equals("AND")||instruct1.getOperation().equals("OR")||instruct1.getOperation().equals("EX-OR"))) {
						destination.setValue(instruct1.getResult());	
					}
					//  else check if the the Register value is forwarded from MEM
					else if(instruct2!= null && instruct2.getDestination()!=null && instruct2.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct2.getOperation().equals("MOVC") ||instruct2.getOperation().equals("ADD")||instruct2.getOperation().equals("MUL")||instruct2.getOperation().equals("SUB")||instruct2.getOperation().equals("AND")||instruct2.getOperation().equals("OR")||instruct2.getOperation().equals("EX-OR"))) {
						destination.setValue(instruct2.getResult());
					}

					//  else check if the the Register value is forwarded from WB
					else if(instruct3!= null && instruct3.getDestination()!=null && instruct3.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct3.getOperation().equals("MOVC") ||instruct3.getOperation().equals("ADD")||instruct3.getOperation().equals("MUL")||instruct3.getOperation().equals("SUB")||instruct3.getOperation().equals("AND")||instruct3.getOperation().equals("OR")||instruct3.getOperation().equals("EX-OR"))) {
						destination.setValue(instruct3.getResult());
					}

					//Set the PC counter to the value of register + literal by mapping it to nearest multiple of 4 (since each instruction are 4 bytes). 
					//This will map the instruction address even if it is given in between the instruction to the starting of the instruction
					this.PC=(((destination.getValue() + instruct.getLiteral()) / 4) * 4);
					//initialize the x resister to next instruction address
					instruct.setResult(instruct.getInstrNumber() + 4);

					//Enable X register forwarding
					registerX.setForwardValue(true);
					pipelineStatus.put("Fetch", null); 			// Flush the fetch instruction
					pipelineStatus.put("Decode", null);			//Flush the decode instruction
					break;
				}
				case "JUMP": {


					ArchitecturalRegisterState destination  = getArchitecturalRegister(instruct.getDestination());

					Instruction instruct1 = pipelineStatus.get("Execute2");
					Instruction instruct2 = pipelineStatus.get("Memory");
					Instruction instruct3 = pipelineStatus.get("WriteBack");
					Instruction instruct4 = pipelineStatus.get("Delay");

					// check if the the Register value is forwarded from ALU2
					if(instruct1!= null && instruct1.getDestination()!=null && instruct1.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct1.getOperation().equals("MOVC") ||instruct1.getOperation().equals("ADD")||instruct1.getOperation().equals("MUL")||instruct1.getOperation().equals("SUB")||instruct1.getOperation().equals("AND")||instruct1.getOperation().equals("OR")||instruct1.getOperation().equals("EX-OR"))) {
						destination.setValue(instruct1.getResult());	
					}
					//  else check if the the Register value is forwarded from MEM
					else if(instruct2!= null && instruct2.getDestination()!=null && instruct2.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct2.getOperation().equals("MOVC") ||instruct2.getOperation().equals("ADD")||instruct2.getOperation().equals("MUL")||instruct2.getOperation().equals("SUB")||instruct2.getOperation().equals("AND")||instruct2.getOperation().equals("OR")||instruct2.getOperation().equals("EX-OR"))) {
						destination.setValue(instruct2.getResult());
					}

					//  else check if the the Register value is forwarded from WB
					else if(instruct3!= null && instruct3.getDestination()!=null && instruct3.getDestination().equalsIgnoreCase(destination.getRegName()) && (instruct3.getOperation().equals("MOVC") ||instruct3.getOperation().equals("ADD")||instruct3.getOperation().equals("MUL")||instruct3.getOperation().equals("SUB")||instruct3.getOperation().equals("AND")||instruct3.getOperation().equals("OR")||instruct3.getOperation().equals("EX-OR"))) {
						destination.setValue(instruct3.getResult());
					}
					//X register value Forwarding to branch
					else if(destination.getRegName().equalsIgnoreCase("X") && destination.isForwardValue()){
						if(instruct4!=null && instruct4.getOperation().equalsIgnoreCase("BAL")){
							destination.setValue(instruct4.getResult());
						}
						else if(instruct2!=null && instruct2.getOperation().equalsIgnoreCase("BAL")){
							destination.setValue(instruct2.getResult());
						}
						else if(instruct3!=null && instruct3.getOperation().equalsIgnoreCase("BAL")){
							destination.setValue(instruct3.getResult());
						}

					}

					//Set the PC counter to the value of register + literal by mapping it to nearest multiple of 4 (since each instruction are 4 bytes). 
					//This will map the instruction address even if it is given in between the instruction to the starting of the instruction
					this.PC=(((destination.getValue() + instruct.getLiteral())/4)*4);
					pipelineStatus.put("Fetch", null);				// Flush the fetch instruction
					pipelineStatus.put("Decode", null);				// Flush the decode instruction
					break;
				}
				//calculation for BZ
				case "BZ": {
					int zeroFlagValue=1;
					//Find the nearest arithmetic operation which has already set its zero flag and take its value
					switch (zeroFlagSelect)
					{
					case -1 :{
						ArchitecturalRegisterState zeroFlag  = getArchitecturalRegister("ZF");
						zeroFlagValue=zeroFlag.getValue();
						break;
					}

					case 1 : {
						Instruction instMEM=pipelineStatus.get("Memory");
						zeroFlagValue=instMEM.getZeroFlag();
						break;
					}

					case 2 :{
						Instruction instWB=pipelineStatus.get("WriteBack");
						zeroFlagValue=instWB.getZeroFlag();
						break;
					}
					}

					//If zeroflag is set  then set the PC counter to the address of BZ + the literal value and flush the Decode and fetch
					if(zeroFlagValue ==0) {
						this.PC=(instruct.getInstrNumber() + ((instruct.getLiteral()/4)*4));
						pipelineStatus.put("Fetch", null); 		// Flush the fetch instruction
						pipelineStatus.put("Decode", null);		//Flush the Decode instruction
					}
					break;
				}

				//Logic for BNZ
				case "BNZ": {

					int zeroFlagValue=1;
					//Find the nearest arithmetic operation which has already set its zero flag and take its value
					switch (zeroFlagSelect)
					{
					case -1 :{
						ArchitecturalRegisterState zeroFlag  = getArchitecturalRegister("ZF");
						zeroFlagValue=zeroFlag.getValue();
						break;
					}

					case 1 : {
						Instruction instMEM=pipelineStatus.get("Memory");
						zeroFlagValue=instMEM.getZeroFlag();
						break;
					}

					case 2 :{
						Instruction instWB=pipelineStatus.get("WriteBack");
						zeroFlagValue=instWB.getZeroFlag();
						break;
					}
					}


					if(zeroFlagValue ==1) {
						//If zeroflag is not  set  then set the PC counter to the address of BNZ + the literal value and flush the Decode and fetch
						this.PC=(instruct.getInstrNumber() + ((instruct.getLiteral()/4)*4));
						pipelineStatus.put("Fetch", null); 			// Flush the fetch instruction
						pipelineStatus.put("Decode", null);			// Flush the decode instruction
					}
					break;
				}
				}
			}


			if (pipelineStatus.get("Decode") != null) {
				Instruction inst=pipelineStatus.get("Decode");
				//For BZ and BNZ if the resister value is available directly or through forwarding then it can go to branch in next stage
				if(inst.getOperation().equals("BZ")||inst.getOperation().equals("BNZ"))
				{  
					//See if a instruction is present in ALU 1 which can set Zero flag in future then stall instruction 
					Instruction instALU1=pipelineStatus.get("Execute1");
					if(instALU1 != null && (instALU1.getOperation().equals("ADD")||instALU1.getOperation().equals("MUL")||instALU1.getOperation().equals("SUB")||instALU1.getOperation().equals("AND")||instALU1.getOperation().equals("OR")||instALU1.getOperation().equals("EX-OR")))
					{
						inst.setDecode_Dession("stalled");
						zeroFlagSelect=0;
					}

					
					else {
						// If not then see if a instruction is present in ALU 2 which can set Zero flag
						Instruction instALU2=pipelineStatus.get("Execute2");
						Instruction instMEM=pipelineStatus.get("Memory");
						//String instALU2.getOperation() = instALU2.getOperation();
						if(instALU2!=null && (instALU2.getOperation().equals("ADD")||instALU2.getOperation().equals("MUL")||instALU2.getOperation().equals("SUB")||instALU2.getOperation().equals("AND")||instALU2.getOperation().equals("OR")||instALU2.getOperation().equals("EX-OR")) )
						{
							inst.setDecode_Dession("ready");
							zeroFlagSelect=1;
						}

						// If not then see if a instruction is present in MEM which can set Zero flag, set zeroFlagSelect to 2
						else if(instMEM!=null  && (instMEM.getOperation().equals("ADD")||instMEM.getOperation().equals("MUL")||instMEM.getOperation().equals("SUB")||instMEM.getOperation().equals("AND")||instMEM.getOperation().equals("OR")||instMEM.getOperation().equals("EX-OR")) )
						{
							inst.setDecode_Dession("ready");
							zeroFlagSelect=2;
						}
						// Else take zero flag value from ZeroFlag(no forwarding)
						else{
							zeroFlagSelect=-1;
							inst.setDecode_Dession("ready");
						}

					}
				}
				// if the instruction is BAL or JUMP check if their register value is available through forwarding then pass it to branch in next stage  else stall
				else if(inst.getOperation().equals("BAL")||inst.getOperation().equals("JUMP")){
					ArchitecturalRegisterState destination = getArchitecturalRegister(inst.getDestination());
					if (!destination.isUnderOperation() || (destination.isUnderOperation() && destination.isForwardValue())) 
						inst.setDecode_Dession("ready");
					else inst.setDecode_Dession("stalled");
				}
				else {

					// Code for all other instruction to check if the dependency is there and the data is available through forwarding... then pass to next corresponding stage else Stall
					String source1 = inst.getSource1();
					//check dependency and forwarding for source 1 for Source 1
					if (source1 != null ) {

						if (!getArchitecturalRegister(source1).isUnderOperation() || (getArchitecturalRegister(source1).isUnderOperation() && getArchitecturalRegister(source1).isForwardValue())) {

							if((getArchitecturalRegister(source1).isUnderOperation() && getArchitecturalRegister(source1).isForwardValue()) == true) {
								inst.setForward(true);
							}
							else
							{
								//decode the value of the register source 1
								inst.setSource1_Value(getArchitecturalRegister(source1).getValue());
							}

							String source2 = inst.getSource2();
							//check dependency and forwarding for source 1 for Source 1
							if (source2 != null) {

								if (!getArchitecturalRegister(source2).isUnderOperation() || (getArchitecturalRegister(source2).isUnderOperation() && getArchitecturalRegister(source2).isForwardValue())) {

									if((getArchitecturalRegister(source2).isUnderOperation() && getArchitecturalRegister(source2).isForwardValue()) == true) {
										inst.setForward(true);
									}
									else
									{
										//decode the value of the register source 2
										inst.setSource2_Value(getArchitecturalRegister(source2).getValue());
									}

									inst.setDecode_Dession("ready");
								} else {
									inst.setDecode_Dession("stalled"); // to stall instruction in D/RF
								}
							} else {
								inst.setDecode_Dession("ready");
							}
						} 
						else {inst.setDecode_Dession("stalled");}
					} else {
						inst.setDecode_Dession("ready");
					}
				}
			}

			//increment cycle
			cycles++;

			//  do while loop only if the given cycle has ended or if halt is fond in the instruction
		}while(this.cycle_counter!=this.cycles && haltBreak!=1);
	}


	//Function to get the object of registers
	private ArchitecturalRegisterState getArchitecturalRegister(String regDest)
	{
		for(int i =0; i < archRegStates.size();i++) {
			if(archRegStates.get(i).getRegName().equals(regDest)) {
				return archRegStates.get(i);
			}
		}
		return null;
	}
	
	//Function to get the object of Memory
	private MemoryRegisterState getMemoryRegister(String regDest)
	{
		for(int i =0; i < memRegStates.size();i++) {
			if(memRegStates.get(i).getRegName().equals(regDest)) {
				return memRegStates.get(i);
			}
		}
		return null;
	}

	
//function to print the content of the registers and the first 100 memory location with valid valued
	private void printArchitecturalState()
	{
		int count=0;
		for(int i=0; i<archRegStates.size() - 2;i++) {
			System.out.println("R"+i+"::: " + archRegStates.get(i).getValue());

		}
		System.out.println("X"+"::: " + getArchitecturalRegister("X").getValue());
		System.out.println("Memory Address");
		for(int i=0; i < memRegStates.size() && count<=100; i++) {
			if(memRegStates.get(i).getValue() != -(Long.MAX_VALUE)){
				System.out.println("M"+(i*4)+"::: " + memRegStates.get(i).getValue());	
				count++;
			}
		}
	} 


	//Function to store the stage of the pipeline in each stage
	private void storeStages() {

		DisplayStages displayStages = new DisplayStages();

		displayStages.setCycles(cycles);

		Instruction instrInFetch = this.pipelineStatus.get("Fetch");

		if(instrInFetch == null) {
			displayStages.setFetchStage("-");
		} else {
			displayStages.setFetchStage(String.valueOf(instrInFetch));
		}
		Instruction instrInDecode = this.pipelineStatus.get("Decode");

		if(instrInDecode == null) {
			displayStages.setDecodeStage("-");
		} else {
			displayStages.setDecodeStage(String.valueOf(instrInDecode));
		}
		Instruction instrInExecute1 = this.pipelineStatus.get("Execute1");

		if(instrInExecute1 == null) {
			displayStages.setExeOneStage("-");
		} else {
			displayStages.setExeOneStage(String.valueOf(instrInExecute1));
		}

		Instruction instrInExecute2 = this.pipelineStatus.get("Execute2");

		if(instrInExecute2 == null) {
			displayStages.setExeSecStage("-");
		} else {
			displayStages.setExeSecStage(String.valueOf(instrInExecute2));
		}

		Instruction instrInBranch = this.pipelineStatus.get("Branch");


		if(instrInBranch == null) {
			displayStages.setBranchStage("-");
		} else {
			displayStages.setBranchStage(String.valueOf(instrInBranch));
		}
		Instruction instrInDelay = this.pipelineStatus.get("Delay");

		if(instrInDelay == null) {
			displayStages.setDelayStage("-");
		} else {
			displayStages.setDelayStage(String.valueOf(instrInDelay));
		}
		Instruction instrInMemory = this.pipelineStatus.get("Memory");

		if(instrInMemory == null) {
			displayStages.setMemoryStage("-");
		} else {
			displayStages.setMemoryStage(String.valueOf(instrInMemory));
		}Instruction instrInWriteBack = this.pipelineStatus.get("WriteBack");

		if(instrInWriteBack == null) {
			displayStages.setWritebackStage("-");
		} else {
			displayStages.setWritebackStage(String.valueOf(instrInWriteBack));
		}
		storeStagesList.add(displayStages);

	}

	
	//Function to pull the instruction from MEM stage to WB stage in the pipeline
	public void getToWB() {

		if(pipelineStatus.get("WriteBack")!= null) {
			Instruction anInstruction = pipelineStatus.get("WriteBack");

			pipelineStatus.put("WriteBack", null);
			anInstruction.setStage("Completed Instruction");		
		} 

		if (pipelineStatus.get("WriteBack") == null)  {

			Instruction anInstruction = pipelineStatus.get("Memory");
			if (anInstruction != null) {

				pipelineStatus.put("Memory", null);
				anInstruction.setStage("WriteBack");
				pipelineStatus.put("WriteBack", anInstruction);
			}

		}
	}
	//Function to pull the instruction from ALU2 or Delay  stage to MEM stage in the pipeline
	public void getToMEM() {

		if (pipelineStatus.get("Delay") != null) {

			Instruction anInstruction = pipelineStatus.get("Delay");
			if (anInstruction != null) {

				pipelineStatus.put("Delay", null);
				anInstruction.setStage("Memory");
				pipelineStatus.put("Memory", anInstruction);

			}

		}
		else if (pipelineStatus.get("Execute2") != null) {

			Instruction anInstruction = pipelineStatus.get("Execute2");
			if (anInstruction != null) {

				pipelineStatus.put("Execute2", null);
				anInstruction.setStage("Memory");
				pipelineStatus.put("Memory", anInstruction);

			}

		}
}

	//Function to pull the instruction from Branch to Delay
	public void getToDelay() {

		if (pipelineStatus.get("Branch") != null) {

			Instruction anInstruction = pipelineStatus.get("Branch");
			if (anInstruction != null) {

				pipelineStatus.put("Branch", null);
				anInstruction.setStage("Delay");
				pipelineStatus.put("Delay", anInstruction);

			}

		}
	}

	//Function to pull the instruction from Decode to Branch whenever it is a branch instruction
	public void getToBranch() {

		if (pipelineStatus.get("Decode") != null && (decodeopcode.equals("BZ")||decodeopcode.equals("BNZ")||decodeopcode.equals("JUMP")||decodeopcode.equals("BAL")||decodeopcode.equals("HALT"))) {
			Instruction anInstruction = pipelineStatus.get("Decode");

			if (anInstruction != null) {
				if (anInstruction.getDecode_Dession().equalsIgnoreCase("ready")) {
					pipelineStatus.put("Decode", null);
					anInstruction.setStage("Branch");
					pipelineStatus.put("Branch", anInstruction);

				}
			}
		}
	}


//Function to pull instruction from ALU1 to ALU2
	public void getToALU2() {

		if (pipelineStatus.get("Execute2") == null) {

			Instruction anInstruction = pipelineStatus.get("Execute1");
			if (anInstruction != null) {

				pipelineStatus.put("Execute1", null);
				anInstruction.setStage("Execute2");
				pipelineStatus.put("Execute2", anInstruction);
			}

		}
		}

//Function to pull the instruction from decode to ALU1 if it is a ALU operation
	public void getToALU1() {
		if (pipelineStatus.get("Decode") != null && !(decodeopcode.equals("BZ")||decodeopcode.equals("BNZ")||decodeopcode.equals("JUMP")||decodeopcode.equals("BAL")||decodeopcode.equals("HALT"))) {

			Instruction anInstruction = pipelineStatus.get("Decode");
			if (anInstruction != null) {

				if ("ready" == anInstruction.getDecode_Dession()) {
					pipelineStatus.put("Decode", null);
					anInstruction.setStage("Execute1");
					pipelineStatus.put("Execute1", anInstruction);
				}
			}

		}
}

//Function to pull the instruction from Fetch to decode 
	public void getToDecode() {

		if ( null == pipelineStatus.get("Decode")) {

			Instruction anInstruction = pipelineStatus.get("Fetch");
			if (anInstruction != null) {
				pipelineStatus.put("Fetch", null);
				anInstruction.setStage("Decode");
				pipelineStatus.put("Decode", anInstruction);
				decodeopcode=anInstruction.getOperation();
			}
		}

		}

	
	//Function to get the instruction from the PC counter and increment the PC counter
	public void getToFetch() {
		if(pipelineStatus.get("Fetch") == null)
		{

			int size = (instructions.size()*4) + 4000;

			if(this.PC < size) {

				Instruction inst1 = instructions.get(((this.PC - 4000)/4));
				pipelineStatus.put("Fetch", inst1);

				inst1.setStage("Fetch");
				this.PC += 4;
			}
		}
	}

	
	//Function to traverse the instruction in pipeline 
	//Each stage pulls instruction from the previous stage
	public void getThroughStages() {

		getToWB();
		getToMEM();
		getToDelay();
		getToBranch();
		getToALU2();
		getToALU1();
		getToDecode();
		getToFetch();
	}
	
	
	
	public void showStagesContentAll() {

		for(int display_counter = 0; display_counter < storeStagesList.size(); display_counter++) {

			System.out.println();
			DisplayStages displayStage = storeStagesList.get(display_counter);
			System.out.println("Cycles: " + displayStage.getCycles()+"\n");
			System.out.println("Fetch\t\t" + displayStage.getFetchStage());
			System.out.println("Decode\t\t" + displayStage.getDecodeStage());
			System.out.println("ALU1\t\t" + displayStage.getExeOneStage());
			System.out.println("ALU2\t\t" + displayStage.getExeSecStage());
			System.out.println("Branch\t\t" + displayStage.getBranchStage());
			System.out.println("Delay\t\t" + displayStage.getDelayStage());
			System.out.println("Memory\t\t" + displayStage.getMemoryStage());
			System.out.println("WriteBack\t" + displayStage.getWritebackStage());

			System.out.println("-------------------------------------------------------" + "\n");

		}

		printArchitecturalState();

	}
	
//Function to display the content og the pipeline
	public void showStagesContent(String input) {

		
			//Display a single stage content of the pipeline
			DisplayStages displayStage = storeStagesList.get(Integer.parseInt(input));
			System.out.println("Cycles: " + displayStage.getCycles()+"\n");
			System.out.println("Fetch\t\t" + displayStage.getFetchStage());
			System.out.println("Decode\t\t" + displayStage.getDecodeStage());
			System.out.println("ALU1\t\t" + displayStage.getExeOneStage());
			System.out.println("ALU2\t\t" + displayStage.getExeSecStage());
			System.out.println("Branch\t\t" + displayStage.getBranchStage());
			System.out.println("Delay\t\t" + displayStage.getDelayStage());
			System.out.println("Memory\t\t" + displayStage.getMemoryStage());
			System.out.println("WriteBack\t" + displayStage.getWritebackStage());

			System.out.println("-------------------------------------------------------" + "\n"); 
	}


}

