
/**
 * 
 * The MemoryRegisterState class is used to hold all the memory locations.
 * It contains all the values of the memory locations. 
 */

public class MemoryRegisterState {

	/**
	 * memory name
	 */
	private String registerName;
	
	/**
	 * memory value
	 */
	private long value;
	
	
	/**
	 * constructor
	 * @param regName
	 * 				memory location 
	 * @param value
	 * 				memory name
	 */
	public MemoryRegisterState(String regName,long value) {
		this.registerName=regName;
		this.value = value;
	}

	/**
	 * return memory location
	 * @return
	 * 		memory location
	 */
	public String getRegName() {
		return registerName;
	}

	/**
	 * return memory's value
	 * @return
	 * 		memory's value
	 */
	public long getValue() {
		return value;
	}
	
	/**
	 * set memory location
	 * @param regName
	 * 		memory location
	 */
	public void setRegName(String regName) {
		this.registerName = regName;
	}
	
	/**
	 * set memory's value
	 * @param value
	 * 			memory value
	 */
	public void setValue(long value) {
		this.value = value;
	}
}
