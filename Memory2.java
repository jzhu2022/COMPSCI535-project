public class Memory2 {
    private int level; // 0 for RAM, 1 for L1, 2 for L2 
    private int size;
    private int lines;
    private int words; // words per line, same for all types 
    private int sets;
    private int ways;  // for cache, ways per set

    private int clock;
    private int cycles;
    
    public int[][] mem;
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
            tags[i] = 0;
            for (int j = 0; j < words; j++) { 
                mem[i][j] = 0; // data is 0
            }
        }
        cycles = (clock = c - 1);

    }

    public Memory2(){}

    public int getCycles() {
        return cycles;
    }

    public int getWords() {
        return words;
    }

    private void updatePriorities(int line, int prio) {
        //System.out.println("prio: " + prio);
        int set = (line/ways) * ways;
        for (int i = set; i < set + ways; i++) {
            //System.out.print(priorities[i] + "      ");
            if (valid[i] && priorities[i] < prio)
                priorities[i]++;
        }  
        //System.out.println();
        priorities[line] = 0; 
    }

    private int inCache(int addr) { // returns line number of addr if it's there, -1 if not
        int set = (addr / words) % (sets); // divide by words to shift over, mod by sets to remove tag
        int tag = addr / (sets * words);   // shift over to get tag

        for (int i = set*ways; i < set*ways + ways; i++) {
            if (valid[i] && tags[i] == tag)
                return i;
        }
        return -1;
    }

    private int bringIntoCache(int addr) {
        int set = (addr / words) % (sets); 
        int tag = addr / (sets * words);
        
        int spot = -1, prio = -1; 
        for (int i = set*ways; i < set*ways + ways; i++) {
            
            if (!valid[i]) {
                spot = i;
                break;
            }
            if (priorities[i] > prio) {
                spot = i;
                prio = priorities[i];
            }
        }

        // need to write back
        // tag set offset
        // tag * (set+offset bits) + 
        if (valid[spot]) {
            if (dirty[spot]) {
                int eAddr = (tags[spot]*(sets) + (spot/ways)) * words;
                for (int i = 0; i <= next.getCycles(); i++) 
                    next.access(eAddr, mem[spot], stage, false);
            }
            //else  
            //  prio = ways; 
        
        }

        for (int i = 0; i <- next.getCycles(); i++) 
            mem[spot] = next.access(addr, new int[0] , stage, true);

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
        updatePriorities(spot, priorities[spot]);
        return mem[spot];
    }

    private void write(int addr, int[] data) {
        int spot = getSpot(addr);
        if (level == 1) 
            mem[spot][addr%words] = data[0]; // if it's L1, data will be array with changed word only 
        else
            mem[spot] = data;

        updatePriorities(spot, priorities[spot]);
        dirty[spot] = true;
    }

    public void display() {
        for (int i = 0; i < lines; i++) {
            if (level != 0) System.out.print("tag: " + tags[i] + "    ");
            for (int j = 0; j < words; j++) {
                System.out.print(mem[i][j] + " ");
            }
            if (level != 0) System.out.print("   v: " + valid[i] + (valid[i]?" ":"") + " d: " + dirty[i] + (dirty[i]?" ":"") + " priority: " + priorities[i]);
            
            System.out.println();
        }
    }

    public Integer[][] displayPart(int start, int end) {
        
        Integer[][] part = new Integer[end - start+1][words];
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < words; j++) {
                
                part[i-start][j] = mem[i][j];
            }
        }
        return part;
    }

    public int[] access(int addr, int[] data, int s, boolean isRead) { 
        System.out.println((level == 0 ? "DRAM":"L" + level) + (isRead ? " read " : " write " + data[0] + " " + data[1] + " at ") + addr + " clock: " + clock);
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
        } 
        if (clock == 0) {
            if (addr == currAddr && s == stage) {
                clock = cycles;
                return val;
            }
        }
        if (addr == currAddr && s == stage)
                clock--;
        
        int[] x = {-1};
        return x; // maybe think of something else to be "wait" so -1 can be read
    }


    public static void main(String[] args) {
        Memory2 DRAM = new Memory2(16, 5, 2, -1, 0, null); 
        int[] line = {1, 1};
        int[] line2 = {0, 1};
        int[] line3 = {1, 0};
        for (int i = 0; i < 5; i++) {
            //DRAM.access(0, line, 0, false);
        }

        Memory2 L2 = new Memory2(8, 3, 2, 2, 2, DRAM);
        Memory2 L1 = new Memory2(4, 1, 2, 2, 1, L2);
        for (int i = 0; i < 1; i++) {
            L1.access(1, line, 0, false); // overwrites
        }
        
        for (int i = 0; i < 1; i++) {
            L1.access(8, line2, 0, false); 
        }
        for (int i = 0; i < 1; i++) {
            L1.access(4, line3, 0, false); // should need to evict
        }
        for (int i = 0; i < 1; i++) {
            L1.access(1, line3, 0, false); // should need to evict
        }
        System.out.println("DRAM: ");
        DRAM.display();

        System.out.println("L2: ");
        L2.display();

        System.out.println("L1: ");
        L1.display();
        
    }
}
