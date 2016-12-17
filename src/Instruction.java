/**
 * 
 * The Instruction class is used to hold the particular instruction content.
 * Instruction contain destination register, source one register, source2 register.
 * Some of the instructions contains destination register and literal values.
 * This class holds the flag for conditional codes. It also holds flag for 
 * instruction forwarding mechanism. It holds two flags for decoding stage to read values.
 * It has flag to hold the result of an instruction. 
 */
public class Instruction {

	/**
	 * instruction operation
	 */
	private String operation;
	
	/**
	 * instructions register source two 
	 */
	private String s2;
	
	/**
	 * instructions register source one value
	 */
	private int source1_Value;
	
	/**
	 * instructions register source two value
	 */
	private int source2_Value;
	
	/**
	 * instructions register literal value
	 */
	private int literal_value;
	
	/**
	 * instruction address
	 */
	private int instruction_Number;
	
	/**
	 * instruction stage
	 */
	private String stage;

	/**
	 * instruction ready/stalled indication code
	 */
	private String decode_Dession;
	
	/**
	 * instruction destination
	 */
	private String dest;
	
	/**
	 * current instruction
	 */
	private String curr_Instruction;
	
	/**
	 * instructions register source one 
	 */
	private String s1;
	
	/**
	 * instruction flag to write result into register files
	 */
	private boolean takeValue;
	
	/**
	 * instructions computed result
	 */
	private int result;
	
	/**
	 * instruction conditional code
	 */
	private int zeroFlag;
	
	/**
	 * instruction forward flag
	 */
	private boolean isForward;

	
	/**
	 * constructor
	 */
	public Instruction() {
		this.curr_Instruction = "";
	}

	/**
	 * return instruction source one value
	 * @return
	 * 		instruction source one value
	 */

	public int getSource1_Value() {
		return source1_Value;
	}

	/**
	 * set instruction source one value
	 * @param source1_Value
	 * 			instruction source one value
	 */
	public void setSource1_Value(int source1_Value) {
		this.source1_Value = source1_Value;
	}

	/**
	 * return instruction source two register value
	 * @return
	 * 		instruction source two register value
	 */
	public int getSource2_Value() {
		return source2_Value;
	}

	/**
	 * set instruction source 2 register value
	 * @param source2_Value
	 * 		instruction source 2 register value
	 */
	public void setSource2_Value(int source2_Value) {
		this.source2_Value = source2_Value;
	}


	/**
	 * return instruction address
	 * @return
	 * 		instruction address
	 */
	public int getInstrNumber() {
		return instruction_Number;
	}

	/**
	 * return instruction operation
	 * @return
	 * 		instruction operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * set instruction operation
	 * @param opcode
	 * 		instruction operation
	 */
	public void setOperation(String opcode) {
		this.operation = opcode;
		this.curr_Instruction += " " + this.operation;
	}

	/**
	 * set instruction address
	 * @param instrNumber
	 * 	instruction address
	 */
	public void setInstrNumber(int instrNumber) {
		this.instruction_Number = instrNumber;
		this.curr_Instruction += "" + this.instruction_Number + "" + ":";
	}

	/**
	 * return instruction status
	 * @return
	 * 	instruction status for ready/stalled
	 */
	public String getDecode_Dession() {
		return decode_Dession;
	}

	/**
	 * set instruction decode ready/stall status
	 * @param decode_Dession
	 * 			instructions ready/stall status
	 */
	public void setDecode_Dession(String decode_Dession) {
		this.decode_Dession = decode_Dession;
	}


	/**
	 * set instruction destination register value
	 * @param destinationReg
	 * 		destination register value
	 */
	public void setDestination(String destinationReg) {
		this.dest = destinationReg;
		this.curr_Instruction += " " + this.dest;
	}

	
	/**
	 * set instruction source one register
	 * @param sourceReg1
	 * 		instruction source one register
	 */
	public void setSource1(String sourceReg1) {
		this.s1 = sourceReg1;
		this.curr_Instruction += " " + this.s1;
	}

	/**
	 * return instruction destination register
	 * @return
	 * 		instruction destination register
	 */
	public String getDestination() {
		return dest;
	}

	/**
	 * return instruction register source one
	 * @return
	 * 		instruction register source one
	 */
	public String getSource1() {
		return s1;
	}

	/**
	 * get instruction source two register
	 * @return
	 * 		instruction source 2 register
	 */
	public String getSource2() {
		return s2;
	}

	/**
	 * return instruction literal
	 * @return
	 * 		  instruction literal
	 */
	public int getLiteral() {
		return literal_value;
	}

	/**
	 * set instruction source2 register
	 * @param sourceReg2
	 * 			instruction source2 register
	 */
	public void setSource2(String sourceReg2) {
		this.s2 = sourceReg2;
		this.curr_Instruction += " " + this.s2;
	}

	/**
	 * set instruction literal
	 * @param literal
	 * 		instruction literal value
	 */
	public void setLiteral(int literal) {
		this.literal_value = literal;
		this.curr_Instruction += " " + this.literal_value;
	}

	/**
	 * set instruction stage
	 * @param stage
	 * 		instruction stage
	 */
	public void setStage(String stage) {
		this.stage = stage;
	}

	/**
	 * get instruction stage
	 * @return
	 * 		instruction stage
	 */
	public String getStage() {
		return stage;
	}

	
	/**
	 * check instructions result written into register files
	 * @return
	 * 		instruction register file written flag
	 */
	public boolean isTakeValue() {
		return takeValue;
	}


	/**
	 * set instruction result into register file 
	 * @param takeValue
	 * 			instructions register final result flag
	 */
	public void setTakeValue(boolean takeValue) {
		this.takeValue = takeValue;
	}

	/**
	 * get instruction result
	 * @return
	 * 	instruction result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * set instructions result
	 * @param result
	 * 		instruction result
	 */
	public void setResult(int result) {
		this.result = result;
	}

	/**
	 * check if instructions is forwarded 
	 * @return
	 * 		forward flag
	 */
	public boolean isForward() {
		return isForward;
	}


	/**
	 *  instruction forward flag
	 * @param isForward
	 * 			forward flag
	 */
	public void setForward(boolean isForward) {
		this.isForward = isForward;
	}

	/**
	 * return zero flag
	 * @return
	 * 		conditional code flag
	 */
	public int getZeroFlag() {
		return zeroFlag;
	}

	/**
	 * set conditional code flag
	 * @param zeroFlag
	 * 		conditional code flag
	 */
	public void setZeroFlag(int zeroFlag) {
		this.zeroFlag = zeroFlag;
	}

	/**
	 * represent object in string format
	 */
	@Override
	public String toString() {
		return this.curr_Instruction;
	}

}
