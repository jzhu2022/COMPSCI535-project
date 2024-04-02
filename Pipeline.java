import java.util.*;

public class Pipeline {
    public Instruction[] instructionCache = new Instruction[200];
    private Stack<Integer> instructionCounter = new Stack();
    private Instruction[] inFlightInstructions = new Instruction[4];

    private int currentInstructionIndex;

    public int[] registers = new int[16];
    private boolean[] pendingRegisters = new boolean[16];
    private int tempRegister;
    private int[] memory = new int[256];

    byte condFlags = 7;

    public Pipeline() {
        currentInstructionIndex = 0;
        tempRegister = -1;

        fillInstructionCache();
    }
    
    private void fillInstructionCache() {
        for (int i = 0; i < instructionCache.length; i++) {
            instructionCache[i] = new Instruction(-738197504);
        }
        for (int i = 0; i < inFlightInstructions.length; i++) {
            inFlightInstructions[i] = new Instruction(-1073741824);//squash instructions in pipeline
        }

    }
    private void squashPipeline() {
        for (int i = 0; i < inFlightInstructions.length; i++) {
            inFlightInstructions[i].cond = 6;//squash instructions in pipeline
        }
    }

    public Instruction fetch() {
        return instructionCache[currentInstructionIndex++];
    }

    public Instruction decode(Instruction i) {
        i.cond = i.instruction >>> 29;
        i.type = i.instruction << 3 >>> 29;
        i.opcode = i.instruction << 6 >>> 28;
        if (i.cond == 6) {
            return i;
        } else if (i.type == 0) {//integer arithmetic
            if (i.opcode == 10) {//comparison format 1
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.source2 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source2] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else if (i.opcode == 11) {//comparison format 2
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 14 >>> 28;
            } else if (i.opcode % 2 == 0) {//even arithmetic have the same format
                i.destination = i.instruction << 10 >>> 28;
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.source2 = i.instruction << 18 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source2] = true;
                i.offset = i.instruction << 22 >>> 22;
            } else {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            }
        } else if (i.type == 1) {
            if (i.opcode == 8) {
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.source2 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source2] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else if  (i.opcode == 9) {
                i.source1 = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 14 >>> 14;
            } else if (i.opcode % 2 == 0) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.source2 = i.instruction << 18 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source2] = true;
                i.offset = i.instruction << 22 >>> 22;
            } else {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            }
        } else if (i.type == 2) {
            if (i.opcode == 0 || i.opcode == 2) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.source2 = i.instruction << 18 >>> 28;
                if (pendingRegisters[i.source2]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source2] = true;
                i.offset = i.instruction << 22 >>> 22;
            } else if (i.opcode == 1 || i.opcode == 3) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else if (i.opcode == 4 || i.opcode == 5) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            } else {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.destination] = true;
                i.source1 = i.instruction << 14 >>> 28;
                if (pendingRegisters[i.source1]) {
                    return new Instruction(-1073741824);
                }
                pendingRegisters[i.source1] = true;
                i.immediate = i.instruction << 22 >>> 22;
            }
        } else if (i.type == 3) {
            i.destination = i.instruction << 10 >>> 28;
            if (pendingRegisters[i.destination]) {
                return new Instruction(-1073741824);
            }
            pendingRegisters[i.destination] = true;
            i.source1 = i.instruction << 14 >>> 28;
            if (pendingRegisters[i.source1]) {
                return new Instruction(-1073741824);
            }
            pendingRegisters[i.source1] = true;
            i.immediate = i.instruction << 22 >>> 22;
        } else if (i.type == 4) {
            if (i.opcode < 3) {
                i.immediate = i.instruction << 22 >>> 22;
            } else if (i.opcode == 3) {
                i.destination = i.instruction << 10 >>> 28;
                if (pendingRegisters[i.destination]) {
                    return new Instruction(-1073741824);
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

    public Instruction execute(Instruction i) {
        /*
        if (i.cond == 6) {
            return i;
        } else if (i.type == 0) {//integer arithmetic
            if (i.opcode == 10 && (pendingRegisters[i.source1] || pendingRegisters[i.source2])) {//comparison format 1
                return new Instruction(-1073741824);
            } else if (i.opcode == 11 && pendingRegisters[i.source1]) {//comparison format 2
            } else if (i.opcode % 2 == 0 && (pendingRegisters[i.destination] && pendingRegisters[i.source1] && pendingRegisters[i.source2])) {//even arithmetic have the same format
            } else if (pendingRegisters[i.destination] || pendingRegisters[i.source1]) {
            }
        } else if (i.type == 1) {
            if (i.opcode == 8 && (pendingRegisters[i.source1] || pendingRegisters[i.source2])) {
            } else if  (i.opcode == 9 && pendingRegisters[i.source1]) {
            } else if (i.opcode % 2 == 0 && (pendingRegisters[i.destination] || pendingRegisters[i.source1] || pendingRegisters[i.source2])) {
            } else if (pendingRegisters[i.destination] || pendingRegisters[i.source1]) {
            }
        } else if (i.type == 2) {
            if ((i.opcode == 0 || i.opcode == 2) && (pendingRegisters[i.destination] || pendingRegisters[i.source1] || pendingRegisters[i.source2]) {
            } else if ((i.opcode == 1 || i.opcode == 3) && (pendingRegisters[i.destination] || pendingRegisters[i.source1])) {
            } else if ((i.opcode == 4 || i.opcode == 5) && (pendingRegisters[i.destination] || pendingRegisters[i.source1])) {
            } else if (pendingRegisters[i.destination] || pendingRegisters[i.source1]){
            }
        } else if (i.type == 3 && (pendingRegisters[i.destination] || pendingRegisters[i.source1])) {
        } else if (i.type == 4 && i.opcode == 3 && pendingRegisters[i.destination]) {
        } else if (i.type == 5){
            //do nothing
        } else {
            System.out.println("bad typecode");
        }*/

        if (i.type == 0) {//integer arithmetic
            if (i.opcode == 10) {//comparison format 1
                setFlags(i.source1, i.source2);
            } else if (i.opcode == 11) {//comparison format 2
                setFlags(i.source1, i.immediate);
            }
            if (i.opcode == 0) {
                tempRegister = registers[i.source1] + registers[i.source2];
            } else if (i.opcode == 1) {
                tempRegister = registers[i.source1] + i.immediate;
            } else if (i.opcode == 2) {
                tempRegister = registers[i.source1] - registers[i.source2];
            } else if (i.opcode == 3) {
                tempRegister = registers[i.source1] - i.immediate;
            } else if (i.opcode == 4) {
                tempRegister = registers[i.source1] * registers[i.source2];
            } else if (i.opcode == 5) {
                tempRegister = registers[i.source1] * i.immediate;
            } else if (i.opcode == 6) {
                tempRegister = registers[i.source1] / registers[i.source2];
            } else if (i.opcode == 7) {
                tempRegister = registers[i.source1] / i.immediate;
            } else if (i.opcode == 8) {
                tempRegister = registers[i.source1] % registers[i.source2];
            } else if (i.opcode == 9) {
                tempRegister = registers[i.source1] % i.immediate;
            }
        } else if (i.type == 1) {
            if (i.opcode == 8) {//comparison format 1
                setFlags(i.source1, i.source2);
            } else if (i.opcode == 9) {//comparison format 2
                setFlags(i.source1, i.immediate);
            }
            if (i.opcode == 0) {
                tempRegister = registers[i.source1] + registers[i.source2];
            } else if (i.opcode == 1) {
                tempRegister = registers[i.source1] + i.immediate;
            } else if (i.opcode == 2) {
                tempRegister = registers[i.source1] - registers[i.source2];
            } else if (i.opcode == 3) {
                tempRegister = registers[i.source1] - i.immediate;
            } else if (i.opcode == 4) {
                tempRegister = registers[i.source1] * registers[i.source2];
            } else if (i.opcode == 5) {
                tempRegister = registers[i.source1] * i.immediate;
            } else if (i.opcode == 6) {
                tempRegister = registers[i.source1] / registers[i.source2];
            } else if (i.opcode == 7) {
                tempRegister = registers[i.source1] / i.immediate;
            }
        } else if (i.type == 2) {
            if (i.opcode == 8) {//comparison format 1
                setFlags(i.source1, i.source2);
            } else if (i.opcode == 9) {//comparison format 2
                setFlags(i.source1, i.immediate);
            }
            if (i.opcode == 0) {
                tempRegister = registers[i.source1] & registers[i.source2];
            } else if (i.opcode == 1) {
                tempRegister = registers[i.source1] & i.immediate;
            } else if (i.opcode == 2) {
                tempRegister = registers[i.source1] | registers[i.source2];
            } else if (i.opcode == 3) {
                tempRegister = registers[i.source1] | i.immediate;
            } else if (i.opcode == 4) {
                tempRegister = ~registers[i.source1];
            } else if (i.opcode == 5) {
                tempRegister = ~registers[i.source1];
            } else if (i.opcode == 6) {
                tempRegister = registers[i.source1] << i.immediate;
            } else if (i.opcode == 7) {
                tempRegister = registers[i.source1] >>> i.immediate;
            } else if (i.opcode == 8) {
                tempRegister = registers[i.source1] << i.immediate;
            } else if (i.opcode == 9) {
                tempRegister = registers[i.source1] >> i.immediate;
            }
        } else if (i.type == 3) {
            if (i.opcode < 3) {
                tempRegister = i.source1 + i.immediate;
            } else if (i.opcode < 6) {
                tempRegister = registers[i.source1 + i.immediate];
            } else if (i.opcode < 8) {
                if (i.opcode == 6) {
                    tempRegister = registers[i.source1];
                } else if (i.opcode == 7) {
                    tempRegister = i.immediate;
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

    public Instruction memory(Instruction i) {
        if (i.cond == 6) {
            return i;
        } else if (i.type == 3) {
            if (i.opcode < 3) {
                registers[i.destination] = memory[tempRegister];
            } else if (i.opcode < 6) {
                memory[i.destination] = registers[tempRegister];
            }
        }
        return i;
    }

    public Instruction writeback(Instruction i) {
        //write value out to register
        if (i.cond == 6) {
            return i;
        } else if (i.type == 0) {
            if (i.opcode >= 0 && i.opcode < 10) {
                registers[i.destination] = tempRegister;
                pendingRegisters[i.destination] = false;
            }
            if (i.opcode % 2 == 0) {
                pendingRegisters[i.source2] = false;
            }
            pendingRegisters[i.source1] = false;

        } else if (i.type == 1) {
            if (i.opcode >= 0 && i.opcode < 8) {
                registers[i.destination] = tempRegister;
                pendingRegisters[i.destination] = false;
            }
            if (i.opcode % 2 == 0) {
                pendingRegisters[i.source2] = false;
            }
            pendingRegisters[i.source1] = false;

        }  else if (i.type == 2) {
            pendingRegisters[i.destination] = false;
            registers[i.destination] = tempRegister;

            pendingRegisters[i.source1] = false;
            if (i.opcode < 4 && i.opcode % 2 == 0) {
                pendingRegisters[i.source2] = false;
            }
        } else if (i.type == 3) {
            registers[i.destination] = tempRegister;
            pendingRegisters[i.destination] = false;
            pendingRegisters[i.source1] = false;
        } else if (i.type == 4) {
            if ((i.cond == 0 && condFlags == 0) || (i.cond == 1 && condFlags == 1) || (i.cond == 2 && (condFlags == 0 || condFlags == 1)) || (i.cond == 3 && condFlags == 3) || (i.cond == 4 && (condFlags == 0 || condFlags == 3))) {
                instructionCounter.push(currentInstructionIndex);
                if (i.opcode < 3) {
                    currentInstructionIndex = i.immediate;
                } else if (i.opcode == 3) {
                    pendingRegisters[i.destination] = false;
                    currentInstructionIndex = registers[i.destination];
                }
            } else {
                squashPipeline();
            }
            condFlags = 7;
        }
        return i;
    }

    void cycle() {
        writeback(inFlightInstructions[3]);
        inFlightInstructions[3] = memory(inFlightInstructions[2]);
        inFlightInstructions[2] = execute(inFlightInstructions[1]);
        
        Instruction tmp = decode(inFlightInstructions[0]);
        if (tmp.cond != 6) {
            inFlightInstructions[1] = tmp;
            inFlightInstructions[0] = fetch();            
        }
    }

    public static void main(String[] args) {
        Instruction i1 = new Instruction(-306184190);
        Instruction i2 = new Instruction(-305922046);
        Instruction i3 = new Instruction(-536345600);

        
        Pipeline p = new Pipeline();
        
        p.instructionCache[0] = i1;
        p.instructionCache[1] = i2;
        p.instructionCache[2] = i3;

        for (int i = 0; i < 200; i++) {
            p.cycle();
        }
        /*
        p.decode(i1);
        p.execute(i1);
        p.writeback(i1);
        p.decode(i2);
        p.execute(i2);
        p.writeback(i2);
        p.decode(i3);
        p.execute(i3);
        p.writeback(i3);
        */

        System.out.println(p.registers[2]);
    }
}
