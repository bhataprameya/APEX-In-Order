/**
 * 
 * The DisplayStages class is used to show all the stages content.
 * It holds the values for all the memory locations.
 *
 */
public class DisplayStages {
	
	/**
	 * cycles 
	 */
	private int cycles;
	
	/**
	 * fetch stage content
	 */
	private String fetchStage;
	
	/**
	 * decode stage content
	 */
	private String decodeStage;
	
	/**
	 * ALU1 content
	 */
	private String exeOneStage;
	
	/**
	 * ALU2 content
	 */
	private String exeSecStage;
	
	/**
	 * Branch content
	 */
	private String branchStage;
	
	/**
	 * delay content
	 */
	private String delayStage;
	
	/**
	 * memory content
	 */
	private String memoryStage;
	
	/**
	 * write back content
	 */
	private String writebackStage;
	
	
	
	/**
	 * return cycles
	 * @return
	 * 		cycles
	 */
	public int getCycles() {
		return cycles;
	}
	
	/**
	 * set cycles
	 * @param cycles
	 * 			cycles
	 */
	public void setCycles(int cycles) {
		this.cycles = cycles;
	}
	
	/**
	 * return fetch stage content
	 * @return
	 * 			fetch stage
	 */
	public String getFetchStage() {
		return fetchStage;
	}
	
	
	/**
	 * set fetch stage
	 * @param fetchStage
	 * 			fetch stage content
	 */
	public void setFetchStage(String fetchStage) {
		this.fetchStage = fetchStage;
	}
	
	/**
	 * return decode stage content
	 * @return
	 * 		decode stage content
	 */
	public String getDecodeStage() {
		return decodeStage;
	}
	
	/**
	 * set decode stage content
	 * @param decodeStage
	 * 		 decode stage content
	 */
	public void setDecodeStage(String decodeStage) {
		this.decodeStage = decodeStage;
	}
	
	/**
	 * return ALU1 stage content
	 * @return
	 * 		ALU1 stage content
	 */
	public String getExeOneStage() {
		return exeOneStage;
	}
	
	/**
	 * set ALU1 stage content
	 * @param exeOneStage
	 * 			ALU1 content
	 */
	public void setExeOneStage(String exeOneStage) {
		this.exeOneStage = exeOneStage;
	}
	
	/**
	 * return ALU2 content
	 * @return
	 * 		ALU2 content
	 */
	public String getExeSecStage() {
		return exeSecStage;
	}
	
	/**
	 * set ALU2 stage content
	 * @param exeSecStage
	 * 		ALU2 content
	 */
	public void setExeSecStage(String exeSecStage) {
		this.exeSecStage = exeSecStage;
	}
	
	/**
	 * get branch stage content
	 * @return
	 * 		branch stage content
	 */
	public String getBranchStage() {
		return branchStage;
	}
	
	/**
	 * set branch stage content
	 * @param branchStage
	 * 		branch stage content
	 */
	public void setBranchStage(String branchStage) {
		this.branchStage = branchStage;
	}
	
	/**
	 * return delay content
	 * @return
	 * 		delay stage content
	 */
	public String getDelayStage() {
		return delayStage;
	}
	
	/**
	 * set delay stage content
	 * @param delayStage
	 * 		delay stage content
	 */
	public void setDelayStage(String delayStage) {
		this.delayStage = delayStage;
	}
	
	/**
	 * get memory stage content
	 * @return
	 * 		memory stage content
	 */
	public String getMemoryStage() {
		return memoryStage;
	}
	
	/**
	 * set memory stage content
	 * @param memoryStage
	 * 			memory stage content
	 */
	public void setMemoryStage(String memoryStage) {
		this.memoryStage = memoryStage;
	}
	
	/**
	 * get write back stage content
	 * @return
	 * 	      write back content
	 */
	public String getWritebackStage() {
		return writebackStage;
	}
	
	/**
	 * set write back stage content
	 * @param writebackStage
	 * 		write back stage content
	 */
	public void setWritebackStage(String writebackStage) {
		this.writebackStage = writebackStage;
	}	

}
