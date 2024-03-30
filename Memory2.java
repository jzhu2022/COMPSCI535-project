public class Memory2 {
    private int size;
    private int l;

    private int clock;
    private int cycles;
    
    private int[][][] mem;

    private int currAddr;
    private boolean currAcc;
    private int val;

    private Memory2 next;

    public Memory2(int s, int c) {
        size = s;
        l = Math.sqrt(size); // some relation to size
        mem = new int[l][l][4];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                mem[i][j][1] = 0; // data, valid and dirty all 0
                mem[i][j][2] = 0;
                mem[i][j][3] = 0;
            }
        }
        cycles = (clock = c);

    }

    public int read(int addr) {
        int[] line = mem[addr/l][addr%l];
        return mem[addr/l][addr%l];
    }

    public void write(int addr, int data) {
        mem[addr/l][addr%l] = data;
    }

    public int access(int addr, int data, boolean type) { // true if read
        if (clock == cycles) {
            currAddr = addr;
            currAcc = type;
            if (type)
                val = read(addr);
            else {
                write(addr, data);
                val = 0;
            }
        }
        else if (clock == 0) {
            if (addr == currAddr && type == currAcc) {
                clock = cycles;
                return val;
            }
        }
        else {
            if (addr == currAddr && type == currAcc)
                clock--;
        }
        return -1;
    }
}