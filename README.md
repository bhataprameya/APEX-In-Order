# APEX-In-Order
Pipeline Stages and functionalities implemented
This is an in-order pipeline with 7 stage namely: Fetch, Decode, ALU1, ALU2, Branch, Delay, Memory & Write back 
Below are the functions implemented in each stage of this pipeline:
Fetch: In this stage the instruction is fetched based on the address in the PC counter. And the address in the PC counter is incremented by 4 so that it points to the next instruction.
Decode (D/RF): In this stage we are checking if the sources of the instruction in this stage are completely independent to get the values directly from the Resistors or if it need to be forwarded.
If the source are independent and there is no dependency then we decode the content of the register and save it in instruction level for further use
There are 2 cases in this stage:
a) Stalled: This is a state where the source of the instruction is dependent on any other instruction in the pipeline and its value has not been forwarded yet.
In this case the instruction need to wait in this stage for the next cycle an repeat the check for dependency again in that cycle.
Note: In case of STORE we are checking this case only for the second source resistor in this stage.
b) Ready: This is a state when the source resisters of the instructions in D/RF stage are either independent or it value is available through forwarding.
This state basically says that instruction can be mover further in the pipeline in next cycle.
ALU1: Only the operations like MOVC, ADD, MUL, SUB, OR, AND, EX-OR, LOAD, STORE comes through this stage.
Following operations are performed in this stage:
a) The destination register is set to under operation so that if any instruction in Decode checks for the dependency then it will be true
b) If the instruction has to get its value from forwarding then get the value through forwarding.
ALU2: In this stage the actual ALU operation is performed and the result is produced. Other than this we also enable the forwarding from this stage for any instruction which requires its computed value.
Note: For STORE we check if the value of the first source is available then we set it.
Branch: only the operations other than arithmetic like BZ, BNZ, BAL, JUMP, HALT go through the Branch stage.
a) If the condition is true for the respective branch statement and the branch is taken then the new PC value is calculated and it is overwritten in the PC counter. Along with this Fetch and Decode stage are flushed.
Note: In case of BAL instruction the X register is also calculated and its value is enabled for forwarding
b) If the branch is not taken then there is no operation performed.
Delay: There is no specific operation performed in this stage.
Memory:
a) If the instruction is STORE and the source 1 value is available for forwarding then we take it.
b) LOAD and STORE operations are performed and the computer result of LOAD is enabled for forwarding.
Writeback: In this stage following operations are performed:
a) All the registers writes are done
b) Forwarding of that instruction and under operation of its sources are disabled.
c) If the instruction is HALT then the Execution is stopped at this point
Instruction
Instruction class holds all the information about the instruction.
Registers R0 – R15 and X
ArchitecturalRegisterState class holds all the information about the register files and X register.
Memory
MemoryRegisterState class holds all the information about the memory locations. The 1000 memory locations each of 4 bytes wide
Display Stage
DisplayStage class holds all the information about the content of each pipeline cycle.
Stall
- In Decode R/F stage, whenever the source registers has dependency and value has yet to be forwarded then stalling of an instruction takes place.
Forwarding Logic
- Whenever the result is computed, a forwarding flag is set.
- Whenever there is a dependency encountered in decode stage, then it will check for the forwarding of the source registers to nearest instruction destination for matching registers.
- If forwarding is enabled for the above source then will move the instruction to the next stage in upcoming cycle otherwise the instruction is stalled till forwarding is enabled.






Commands for the Simulator:

initialize: It initializes the simulator with the given instructions.It initializes PC content, cycles, register files and memory locations. It reads the given instructions for the simulator from the file.

simulate <n>: It simulates the number of instructions for n cycles by setting the state of each stage in the pipeline.

display : It displays the contents of all the stages. It displays the contents of register files and first 100 memory locations whose values have been changed.

display <n> : It displays the content of each stage for the given cycle.

Quit: It stops the simulator.
