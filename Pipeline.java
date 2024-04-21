import java.util.*;

public class Pipeline {
    private Stack<Integer> instructionCounter = new Stack();
    private Instruction[] inFlightInstructions = new Instruction[4];

    private Memory2 memory;

    private int currentInstructionIndex;

    public int[] registers = new int[16];
    private boolean[] pendingRegisters = new boolean[16];

    byte condFlags = 7;

    public Pipeline(Memory2 memory) {
        currentInstructionIndex = 0;
        
        this.memory = memory;

        fillInstructionCache();
    }
    
    public void fillInstructionCache() {
        //addition
        int[] iHALT = {-201326592, 0};
        for (int i = 0; i < 32; i += 2) {
            memory.access(i, iHALT, 0, false);
            //instructionCache[i].cond = 6;
        }

        int[] i1 = {-306184190, 0};
        int[] i2 = {-305922046, 0};
        int[] i3 = {-536345600, 0};
        memory.access(0, i1, 0, false);
        memory.access(2, i2, 0, false);
        memory.access(4, i3, 0, false);

        //branching
        int[] i4 = {-490209276, 0};
        int[] i5 = {268435472, 0};
        int[] i6 = {-305659896, 0};

        
        memory.access(6, i4, 0, false);
        memory.access(8, i5, 0, false);
        memory.access(10, i6, 0, false);

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

    private Instruction fetch() {
        Data read = memory.access(currentInstructionIndex++, null, 0, true);
        if (read.done) {
            System.out.println(Arrays.toString(read.data));
            return new Instruction(read.data[0]);
        } else {
            return stall();
        }
    }

    private Instruction stall() {
        Instruction i = new Instruction(-1610612736);
        i.cond = 5;
        return i;
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
            if (i.opcode < 3) {
                i.result = i.source1 + i.immediate;
            } else if (i.opcode < 6) {
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
                Data read = memory.access(i.destination, null, 3, true);
                if (read.done) {
                    registers[i.destination] = read.data[0];
                } else {
                    return stall();
                }
        
            } else if (i.opcode < 6) {
                int[] d = {registers[i.result]};
                int write = memory.access(i.destination, d, 3, false).data[0];
                if (write == -1) {  
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
            registers[i.destination] = i.result;
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

        inFlightInstructions[2] = execute(inFlightInstructions[1]);
        readOut[2] = inFlightInstructions[2];
        
        inFlightInstructions[1] = decode(inFlightInstructions[0]);
        readOut[1] = inFlightInstructions[1];

        if (inFlightInstructions[1].cond != 5) {
            inFlightInstructions[0] = fetch();
        }
        readOut[0] = inFlightInstructions[0];

        return readOut;
    }

    public static void main(String[] args) {
        Memory2 DRAM = new Memory2(2 * 32, 1, 2, -1, 0, null); 
        Pipeline p = new Pipeline(DRAM);


        //make sure pending registers are reset when squashing

        //while(p.notEndOfProgram()) {
        for (int i = 0; i < 5; i++) {
            int cheat = p.inFlightInstructions[3].instruction;
            p.cycle();
            System.out.println("fetching:  " + Integer.toBinaryString(p.inFlightInstructions[0].instruction) + " - " + p.inFlightInstructions[0].instruction);
            System.out.println("decoding:  " + Integer.toBinaryString(p.inFlightInstructions[1].instruction) + " - " + p.inFlightInstructions[1].instruction);
            System.out.println("executing: " + Integer.toBinaryString(p.inFlightInstructions[2].instruction) + " - " + p.inFlightInstructions[2].instruction);
            System.out.println("Memory:    " + Integer.toBinaryString(p.inFlightInstructions[3].instruction) + " - " + p.inFlightInstructions[3].instruction);
            System.out.println("Writeback: " + Integer.toBinaryString(cheat)  + " - " + cheat);
            System.out.println("Registers: " + p.registers[0] + ", " + p.registers[1] + ", " + p.registers[2] + "\n\n");
        }
    }
}
