public class Memory {
    enum TYPE {L1, L2, DRAM};
    private TYPE type;
    private int size;
    private int cycles;
    private Memory next;
    private int[][] mem;
    private int clock;
    private int currAddr;
    private String rw;


    public Memory(TYPE t, int s, int c, Memory n) {
        type = t;
        size = s;
        cycles = c;
        clock = cycles;
        next = n;
        mem = new int[4][size];
    }

    public int access(String t, int addr) {
        int v;
        if (clock == cycles) {
            currAddr = addr;
            rw = t;
        }
        else {
            if (currAddr != addr || rw != t)
                return -1; // not the current access
        }
    
        
        if (clock == 0) {
            clock = cycles;
            
            if (t == "r") 
                v = read(addr);
            else {
                write(addr);
                v = 0;
            }
        
            return v;
        }
        else {
            clock--;
            return -1; // placeholder for "wait"
        }
    }

    private int read(int addr) {
        int v;
        if (addr == 0 && type != TYPE.DRAM) { // if value isn't in current level of memory
            v = next.read(addr);
        }
        else {
            v = mem[0][0]; // get the value if it's there
        }
        return v;
    }

    private void write(int addr) {
        if (addr == 0 && type != TYPE.DRAM) { // if value isn't in current level of memory
            next.write(addr);
        }
        else {
            mem[0][0] = 0; // addr will be contain the value to write as well
        }
    }

}