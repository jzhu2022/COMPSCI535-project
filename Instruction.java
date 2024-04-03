public class Instruction {
    public int instruction;
    public int cond;
    public int type;
    public int opcode;
    public int destination;
    public int source1;
    public int source2;
    public int offset;
    public int immediate;

    public int result;

    public Instruction(int i) {
        instruction = i;
    }

    public String toString() {
        return "instruction:" + instruction + "\ncond:" + cond + "\ntype:" + type + "\nopcode:" + opcode + "\ndestination:" + destination + "\nsource1:" + source1 + "\nsource2:" + source2 + "\nimmediate:" + immediate;
    }
}
