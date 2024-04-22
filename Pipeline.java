import java.util.*;

public class Pipeline {
    private Stack<Integer> instructionCounter = new Stack();
    private Instruction[] inFlightInstructions = new Instruction[4];

    public Memory2 memory;
    
    private int currentInstructionIndex;
    
    public int[] registers = new int[16];
    private boolean[] pendingRegisters = new boolean[16];
    
    private int[] instructionRegisters = new int[16];

    byte condFlags = 7;

    public Pipeline(Memory2 memory) {
        currentInstructionIndex = 0;
        
        this.memory = memory;

        fillInstructionCache();
    }
    
    public void fillInstructionCache() {
        int iHALT = -201326592;
        for (int i = 0; i < instructionRegisters.length; i++) {
            instructionRegisters[i] = iHALT;
        }

        //addition
        instructionRegisters[0] = -306184190;
        instructionRegisters[1] = -305922046;
        instructionRegisters[2] = -536345600;
        //branching
        instructionRegisters[3] = -490209276;
        instructionRegisters[4] = 268435472;
        instructionRegisters[5] = -305659896;
        //store then load
        instructionRegisters[6] = -314048512;
        instructionRegisters[7] = -335544320;

        for (int i = 0; i < inFlightInstructions.length; i++) {
            inFlightInstructions[i] = new Instruction(-1073741824);//squash instructions in pipeline
            inFlightInstructions[i].cond = 6;
        }

        memory.display();
    }

    private void squashPipeline() {
        for (int i = 0; i < inFlightInstructions.length; i++) {
            inFlightInstructions[i].cond = 6;//squash instructions in pipeline
        }
    }

    private Instruction stall() {
        Instruction i = new Instruction(-1610612736);
        i.cond = 5;
        return i;
    }

    private Instruction fetch() {
        /*
        Data read = memory.access(currentInstructionIndex, null, 0, true);
        if (read.done) {
            currentInstructionIndex++;
            System.out.println(Arrays.toString(read.data));
            return new Instruction(read.data[0]);
        } else {
            return stall();
        }
        */
        return new Instruction(instructionRegisters[currentInstructionIndex++]);
    }

    private Instruction decode(Instruction i) {
        i.cond = i.instruction >>> 29;
        i.type = i.instruction << 3 >>> 29;
        i.opcode = i.instruction << 6 >>> 28;
        if (i.cond == 6 || i.cond == 5) {
            return i;
        } else if (i.type == 0) {//integer arithmetic
            if (i.opcode == 10) {//comparison format 1
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                i.source2 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return stall();
                }
                pendingRegisters[i.source1] = true;
                pendingRegisters[i.source2] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else if (i.opcode == 11) {//comparison format 2
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 14 >>> 18;
            } else if (i.opcode % 2 == 0) {//even arithmetic have the same format
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                i.source2 = i.instruction << 18 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                pendingRegisters[i.source2] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            }
        } else if (i.type == 1) {
            if (i.opcode == 8) {
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                i.source2 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return stall();
                }
                pendingRegisters[i.source1] = true;
                pendingRegisters[i.source2] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else if  (i.opcode == 9) {
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 14 >>> 14;
            } else if (i.opcode % 2 == 0) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                i.source2 = i.instruction << 18 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                pendingRegisters[i.source2] = true;
                i.offset = i.instruction << 22 >>> 22;
            } else {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            }
        } else if (i.type == 2) {
            if (i.opcode == 0 || i.opcode == 2) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                i.source2 = i.instruction << 18 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                pendingRegisters[i.source2] = true;
                i.offset = i.instruction << 22 >>> 22;
            } else if (i.opcode == 1 || i.opcode == 3) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else if (i.opcode == 4 || i.opcode == 5) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            }
        } else if (i.type == 3) {
            if (i.opcode < 7) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    
                    return stall();
                }
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 18 >>> 18;
            } else if (i.opcode == 7) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                i.immediate = i.instruction << 14 >>> 14;
            }
        } else if (i.type == 4) {
            if (i.opcode < 3) {
                i.immediate = i.instruction << 22 >>> 22;
            } else if (i.opcode == 3) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return stall();
                }
                pendingRegisters[i.destination] = true;
                i.immediate = i.instruction << 14 >>> 14;
            }
        } else if (i.type == 5){
            //do nothing
        } else {
            System.out.println("bad typecode");
        }
        return i;
    }

    private void setFlags(int operand1, int operand2) {
        if(operand1 == operand2) {
            condFlags = 0;
        } else if (operand1 > operand2) {
            condFlags = 1;
        } else if (operand1 >= operand2) {
            condFlags = 2;
        } else if (operand1 < operand2) {
            condFlags = 3;
        } else if (operand1 <= operand2) {
            condFlags = 4;
        }
    }

    private Instruction execute(Instruction i) {
        i.result = 0;

        if (i.cond == 6 || i.cond == 5) {
            return i;
        } else if (i.type == 0) {//integer arithmetic
            if (i.opcode == 10) {//comparison format 1
                setFlags(i.source1, i.source2);
            } else if (i.opcode == 11) {//comparison format 2
                setFlags(i.source1, i.immediate);
            }
            if (i.opcode == 0) {
                i.result = registers[i.source1] + registers[i.source2];
            } else if (i.opcode == 1) {
                i.result = registers[i.source1] + i.immediate;
            } else if (i.opcode == 2) {
                i.result = registers[i.source1] - registers[i.source2];
            } else if (i.opcode == 3) {
                i.result = registers[i.source1] - i.immediate;
            } else if (i.opcode == 4) {
                i.result = registers[i.source1] * registers[i.source2];
            } else if (i.opcode == 5) {
                i.result = registers[i.source1] * i.immediate;
            } else if (i.opcode == 6) {
                i.result = registers[i.source1] / registers[i.source2];
            } else if (i.opcode == 7) {
                i.result = registers[i.source1] / i.immediate;
            } else if (i.opcode == 8) {
                i.result = registers[i.source1] % registers[i.source2];
            } else if (i.opcode == 9) {
                i.result = registers[i.source1] % i.immediate;
            }
        } else if (i.type == 1) {
            if (i.opcode == 8) {//comparison format 1
                setFlags(i.source1, i.source2);
            } else if (i.opcode == 9) {//comparison format 2
                setFlags(i.source1, i.immediate);
            }
            if (i.opcode == 0) {
                i.result = registers[i.source1] + registers[i.source2];
            } else if (i.opcode == 1) {
                i.result = registers[i.source1] + i.immediate;
            } else if (i.opcode == 2) {
                i.result = registers[i.source1] - registers[i.source2];
            } else if (i.opcode == 3) {
                i.result = registers[i.source1] - i.immediate;
            } else if (i.opcode == 4) {
                i.result = registers[i.source1] * registers[i.source2];
            } else if (i.opcode == 5) {
                i.result = registers[i.source1] * i.immediate;
            } else if (i.opcode == 6) {
                i.result = registers[i.source1] / registers[i.source2];
            } else if (i.opcode == 7) {
                i.result = registers[i.source1] / i.immediate;
            }
        } else if (i.type == 2) {
            if (i.opcode == 8) {//comparison format 1
                setFlags(i.source1, i.source2);
            } else if (i.opcode == 9) {//comparison format 2
                setFlags(i.source1, i.immediate);
            }
            if (i.opcode == 0) {
                i.result = registers[i.source1] & registers[i.source2];
            } else if (i.opcode == 1) {
                i.result = registers[i.source1] & i.immediate;
            } else if (i.opcode == 2) {
                i.result = registers[i.source1] | registers[i.source2];
            } else if (i.opcode == 3) {
                i.result = registers[i.source1] | i.immediate;
            } else if (i.opcode == 4) {
                i.result = ~registers[i.source1];
            } else if (i.opcode == 5) {
                i.result = ~registers[i.source1];
            } else if (i.opcode == 6) {
                i.result = registers[i.source1] << i.immediate;
            } else if (i.opcode == 7) {
                i.result = registers[i.source1] >>> i.immediate;
            } else if (i.opcode == 8) {
                i.result = registers[i.source1] << i.immediate;
            } else if (i.opcode == 9) {
                i.result = registers[i.source1] >> i.immediate;
            }
        } else if (i.type == 3) {
            if (i.opcode < 6) {
                i.result = registers[i.source1 + i.immediate];
            } else if (i.opcode < 8) {
                if (i.opcode == 6) {
                    i.result = registers[i.source1];
                } else if (i.opcode == 7) {
                    i.result = i.immediate;
                }
            }
        } else if (i.type == 4) {
            //do nothing
        } else if (i.type == 5) {
            //do nothing
        } else {
            System.out.println("bad typecode");
        }
        return i;
    }

    private Instruction memory(Instruction i) {
        if (i.cond == 6 || i.cond == 5) {
            return i;
        } else if (i.type == 3) {
            if (i.opcode < 3) {
                Data read = memory.access(i.result, null, 3, true);
                if (read.done) {
                    i.result = read.data[0];
                } else {
                    return stall();
                }
            } else if (i.opcode < 6) {
                int[] d = {registers[i.destination]};
                System.out.println(i.result);
                Data write = memory.access(i.result, d, 3, false);
                if (!write.done) {  
                    return stall();
                }
            }
        }
        return i;
    }

    private Instruction writeback(Instruction i) {
        //write value out to register
        if (i.cond == 6 || i.cond == 5) {
            return i;
        } else if (i.type == 0) {
            if (i.opcode >= 0 && i.opcode < 10) {
                registers[i.destination] = i.result;
                pendingRegisters[i.destination] = false;
            }
            if (i.opcode % 2 == 0) {
                pendingRegisters[i.source2] = false;
            }
            pendingRegisters[i.source1] = false;
        } else if (i.type == 1) {
            if (i.opcode >= 0 && i.opcode < 8) {
                registers[i.destination] = i.result;
                pendingRegisters[i.destination] = false;
            }
            if (i.opcode % 2 == 0) {
                pendingRegisters[i.source2] = false;
            }
            pendingRegisters[i.source1] = false;

        }  else if (i.type == 2) {
            pendingRegisters[i.destination] = false;
            registers[i.destination] = i.result;

            pendingRegisters[i.source1] = false;
            if (i.opcode < 4 && i.opcode % 2 == 0) {
                pendingRegisters[i.source2] = false;
            }
        } else if (i.type == 3) {
            if (i.opcode < 4 || i.opcode > 6) {
                registers[i.destination] = i.result;
            }
            pendingRegisters[i.destination] = false;
            pendingRegisters[i.source1] = false;
        } else if (i.type == 4) {
            if ((i.cond == 0 && condFlags == 0) || (i.cond == 1 && condFlags == 1) || (i.cond == 2 && (condFlags == 0 || condFlags == 1)) || (i.cond == 3 && condFlags == 3) || (i.cond == 4 && (condFlags == 0 || condFlags == 3))) {
                squashPipeline();
                instructionCounter.push(currentInstructionIndex);
                if (i.opcode < 3) {
                    currentInstructionIndex = i.immediate;
                } else if (i.opcode == 3) {
                    pendingRegisters[i.destination] = false;
                    currentInstructionIndex = registers[i.destination];
                }
            } else {
                condFlags = 7;
            }
        }
        return i;
    }

    public boolean notEndOfProgram() {
        return inFlightInstructions[0].instruction != -201326592 || inFlightInstructions[1].instruction != -201326592 || inFlightInstructions[2].instruction != -201326592 || inFlightInstructions[3].instruction != -201326592;
    }

    public Instruction[] cycle() {
        Instruction[] readOut = new Instruction[5];
        readOut[4] = inFlightInstructions[3];

        writeback(inFlightInstructions[3]);

        inFlightInstructions[3] = memory(inFlightInstructions[2]);
        readOut[3] = inFlightInstructions[3];

        //check if memory stage generated a stall
        if (!(inFlightInstructions[3].cond == 5 && inFlightInstructions[2].cond != 5)) {
            inFlightInstructions[2] = execute(inFlightInstructions[1]);
            readOut[2] = inFlightInstructions[2];

            inFlightInstructions[1] = decode(inFlightInstructions[0]);
            readOut[1] = inFlightInstructions[1];

            if (inFlightInstructions[1].cond != 5) {
                inFlightInstructions[0] = fetch();
            }
            readOut[0] = inFlightInstructions[0];
        }

        return readOut;
    }

    public static void main(String[] args) {
        Memory2 DRAM = new Memory2(16, 5, 2, -1, 0, null);
        Memory2 L2 = new Memory2(8, 3, 2, 2, 2, DRAM);
        Memory2 L1 = new Memory2(4, 1, 2, 2, 1, L2);

        Pipeline p = new Pipeline(L1);


        //make sure pending registers are reset when squashing

        while(p.notEndOfProgram()) {
        //for (int i = 0; i < 5; i++) {
            int cheat = p.inFlightInstructions[3].instruction;
            p.cycle();
            System.out.println("fetching:  " + Integer.toBinaryString(p.inFlightInstructions[0].instruction) + " - " + p.inFlightInstructions[0].instruction);
            System.out.println("decoding:  " + Integer.toBinaryString(p.inFlightInstructions[1].instruction) + " - " + p.inFlightInstructions[1].instruction);
            System.out.println("executing: " + Integer.toBinaryString(p.inFlightInstructions[2].instruction) + " - " + p.inFlightInstructions[2].instruction);
            System.out.println("Memory:    " + Integer.toBinaryString(p.inFlightInstructions[3].instruction) + " - " + p.inFlightInstructions[3].instruction);
            System.out.println("Writeback: " + Integer.toBinaryString(cheat)  + " - " + cheat);
            System.out.println("Registers: " + p.registers[0] + ", " + p.registers[1] + ", " + p.registers[2] + "\n\n");
        }
        p.memory.display();
    }
}
