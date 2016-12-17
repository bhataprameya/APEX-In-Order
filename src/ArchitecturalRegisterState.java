/**
 * 
 * The ArchitecturalRegisterState class is used to hold all the registers.
 * It acts as register files. It contains values of all the registers.
 *
 */

public class ArchitecturalRegisterState {
	
	/**
	 * registerName :
	 * 				  holds register name
	 */
	private String registerName;
	
	/**
	 * value :
	 * 			holds register's value
	 */
	private int value;
	
	/**
	 * inOperation :
	 * 			to decide whether the register is in operation or not 
	 */
	private boolean inOperation;
	
	/**
	 * forwardValue :
	 * 			to decide whether instruction forwarding is required for register
	 */
	private boolean forwardValue;
	
	
	/**
	 * constructor
	 * @param regName
	 * 				register name
	 * @param value
	 * 				register vale
	 * @param underOperation
	 * 				register current status
	 * 			
	 */
	public ArchitecturalRegisterState(String regName,int value, boolean underOperation) {
		this.registerName=regName;
		this.inOperation = underOperation;
		this.value = value;
		
	}
	
	/**
	 * return register name
	 * @return
	 * 			register name
	 */
	public String getRegName() {
		return registerName;
	}
	

	/**
	 * return register value
	 * @return
	 * 		 register value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * set register name
	 * @param regName
	 * 		register name
	 */
	public void setRegName(String regName) {
		this.registerName = regName;
	}

	
	/**
	 * register is in operational currently
	 * @return 
	 * 			register operation status
	 */
	public boolean isUnderOperation() {
		return inOperation;
	}
	
	/**
	 * register value
	 * @param value
	 * 		register value
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	
	/**
	 * set register current operation status
	 * @param underOperation
	 * 		  register operational status
	 */
	public void setUnderOperation(boolean underOperation) {
		this.inOperation = underOperation;
	}

	/**
	 * instructions register forward value
	 * @return
	 * 		instructions register forward value
	 */
	public boolean isForwardValue() {
		return forwardValue;
	}

	/**
	 * set instructions register forward value
	 * @param forwardValue
	 * 			set register forward status
	 */
	public void setForwardValue(boolean forwardValue) {
		this.forwardValue = forwardValue;
	}
	
	
}

