public class Memory2 {
    private int level; // 0 for RAM, 1 for L1, 2 for L2 
    private int size;
    private int lines;
    private int words; // words per line, same for all types 
    private int sets;
    private int ways;  // for cache, ways per set

    private int clock;
    private int cycles;
    
    private int[][] mem;
    private int[] priorities;
    private int[] tags;
    private boolean[] valid;
    private boolean[] dirty;

    private int currAddr;
    private int stage; // 0 for fetch, 1 for decode, etc
    private int[] val;

    private Memory2 next;

    public Memory2(int s, int c, int o, int a, int l, Memory2 n) { // -1 for a if not cache
        level = l;
        next = n;
        
        size = s;
        words = o; 
        ways = a;
        lines = size / words;
        sets = lines / ways;

        mem = new int[lines][words]; // mem only stores words
        priorities = new int[lines]; // lines have the same priority, tag, valid, and dirty
        tags = new int[lines];
        valid = new boolean[lines];
        dirty = new boolean[lines];
        
        for (int i = 0; i < lines; i++) {
            priorities[i] = ways;
            valid[i] = false;
            dirty[i] = false;
            for (int j = 0; j < words; j++) { 
                mem[i][j] = 0; // data is 0
            }
        }
        cycles = (clock = c - 1);

    }

    private void updatePriorities(int line, int prio) {
        for (int i = line; i < ways; i++) {
            if (valid[i] && priorities[i] < prio)
                priorities[i]++;
        }   
    }

    private int inCache(int addr) { // returns line number of addr if it's there, -1 if not
        int set = (addr / words) % (sets); // divide by words to shift over, mod by sets to remove tag
        int tag = addr / (sets * words);   // shift over to get tag

        for (int i = set*ways; i < ways; i++) {
            if (tags[i] == tag)
                return i;
        }
        return -1;
    }

    private int bringIntoCache(int addr) {
        int set = (addr / words) % (sets); 
        int tag = addr / (sets * words);
        
        int spot = -1, prio = 101; 
        
        for (int i = set*ways; i < ways; i++) {
            if (!valid[i]) {
                spot = i;
                break;
            }
            if (priorities[i] < prio) {
                spot = i;
                prio = priorities[i];
            }
        }

        // need to write back
        if (valid[spot]) {
            if (dirty[spot])  
                next.access(addr, mem[spot], stage, false);
            else  
                prio = ways;
            
            updatePriorities(set*ways, prio);
        }

        mem[spot] = next.access(addr, new int[0] , stage, true);
        priorities[spot] = 0;
        tags[spot] = tag;
        valid[spot] = true;
        dirty[spot] = false;

        return spot;
    }

    private int getSpot(int addr) {
        int spot = addr/words;
        if (level != 0) {
            spot = inCache(addr);
            if (spot == -1) {
                spot = bringIntoCache(addr);
            }
        }
        return spot;
    }

    private int[] read(int addr) { // returns line
        int spot = getSpot(addr);
        return mem[spot];
    }

    private void write(int addr, int[] data) {
        int spot = getSpot(addr);
        if (level == 1) 
            mem[spot][addr%words] = data[0]; // if it's L1, data will be array with changed word only 
        else
            mem[spot] = data;

        dirty[spot] = true;
    }

    public void display() {
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < words; j++) {
                System.out.print(mem[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int[] access(int addr, int[] data, int s, boolean isRead) { 
        if (clock == cycles) {
            currAddr = addr;
            stage = s;
            if (isRead) {
                val = read(addr);
                if (level == 1) {
                    int v = val[addr % words];
                    val = new int[1];
                    val[0] = v; // if this is L1 read, return array with desired word only
                }
            }
            else {
                write(addr, data);
                val = new int[1];
                val[0] = 0;
            }
            clock--;
        }
        else if (clock == 0) {
            if (addr == currAddr && s == stage) {
                clock = cycles;
                return val;
            }
        }
        else {
            if (addr == currAddr && s == stage)
                clock--;
        }
        int[] x = {-1};
        return x; // maybe think of something else to be "wait" so -1 can be read
    }


    public static void main(String[] args) {
        Memory2 DRAM = new Memory2(16, 5, 2, -1, 0, null); 
        int[] line = {1, 1};
        for (int i = 0; i < 5; i++) {
            DRAM.access(0, line, 0, false);
        }
        int[] line2 = {0, 1};
        for (int i = 0; i < 5; i++) {
            DRAM.access(1, line2, 0, false); // overwrites
        }
        DRAM.display();
        
    }
}