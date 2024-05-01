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

    private Data wait = new Data(false, new int[1]);
    private Data done = new Data(true, new int[1]);

    private int currAddr;
    private int stage; // 0 for fetch, 1 for decode, etc
    private int[] val;

    private Memory2 next;

    private int coldMisses;
    private int conMisses;
    private int hits;

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

        coldMisses = 0;
        conMisses = 0;
        hits = 0;

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

    private Data bringIntoCache(int addr) {
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
                //System.out.println("evicting");
                //System.out.println(mem[spot][0] + " " + mem[spot][1]);

                int[] newLine = new int[words];
                
                
                    for (int j = 0; j < words; j++) {
                        newLine[j] = mem[spot][j];
                    }
                
                conMisses++;
                int eAddr = (tags[spot]*(sets) + (spot/ways)) * words;
                for (int i = 0; i <= next.getCycles(); i++) 
                    next.access(eAddr, newLine, stage, false);
            }
            //else  
            //  prio = ways; 
        } else {coldMisses++;}

         
        Data nextLevel = next.access(addr, new int[0] , stage, true);
        if (!nextLevel.done) return wait;

        tags[spot] = tag;
        valid[spot] = true;
        dirty[spot] = false;
        for (int i = 0; i < words; i++) {
            mem[spot][i] = nextLevel.data[i];
        }

        int[] spotArr =  {spot};
        done.data = spotArr;
        return done;
    }

    private Data getSpot(int addr) {
        int spot = addr/words;
        if (level != 0) {
            
            spot = inCache(addr);
            if (spot == -1) {
                Data s = bringIntoCache(addr);
                if (!s.done) return wait;
                spot = s.data[0];
            } else {hits++;}
        }

        int[] spotArr = {spot};
        done.data = spotArr;
        return done;
    }

    private Data read(int addr) { // returns line
        Data s = getSpot(addr);
        if (!s.done) return wait;

        int spot = s.data[0];
        if (spot < mem.length) {
            updatePriorities(spot, priorities[spot]);
            done.data = mem[spot];
        }
        return done;
    }

    private Data write(int addr, int[] data) {
        
        Data s = getSpot(addr);
        if (!s.done) return wait;

        int spot = s.data[0];
        if (data.length == 1) {
            if (spot < mem.length)
                mem[spot][addr%words] = data[0]; // if it's L1, data will be array with changed word only 
        } else {
            for (int i = 0; i < words; i++) {
                mem[spot][i] = data[i];
            }
        }

        if (spot < mem.length) {
            updatePriorities(spot, priorities[spot]);
            dirty[spot] = true;
        }
        return done;
    }

    public void evictAll() {
        if (next != null) {
            for (int i = 0; i < lines; i++) {
                int[] newLine = new int[words];
                int[] clearLine = new int[words];

                for (int j = 0; j < words; j++) {
                    newLine[j] = mem[i][j];
                    tags[j] = 0;
                    dirty[j] = false;
                    valid[j] = false;
                    priorities[j] = words;
                    clearLine[j] = 0;
                }
                
                int eAddr = (tags[i]*(sets) + (i/ways)) * words;
                for (int k = 0; k <= next.getCycles(); k++) 
                    next.access(eAddr, newLine, stage, false);

                mem[i] = clearLine;
            }
        }
    }

    public void display() {
        for (int i = 0; i < lines; i++) {
            if (level != 0) System.out.print("tag: " + tags[i] + "    ");
            for (int j = 0; j < words; j++) {
                System.out.print((level == 0 ? words * i + j : (i/ways) * words + tags[i]*sets*words + j) + ": " + mem[i][j] + "      ");
            }
            if (level != 0) System.out.print("   v: " + valid[i] + (valid[i]?" ":"") + " d: " + dirty[i] + (dirty[i]?" ":"") + " priority: " + priorities[i]);
            
            System.out.println();
        }
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < lines; i++) {
            if (level != 0) str += "tag: " + tags[i] + "    ";
            for (int j = 0; j < words; j++) {
                str += (level == 0 ? words * i + j : (i/ways) * words + tags[i]*sets*words + j) + ": " + mem[i][j] + "      ";
            }
            if (level != 0) str += "   v: " + valid[i] + (valid[i]?" ":"") + " d: " + dirty[i] + (dirty[i]?" ":"") + " priority: " + priorities[i];
            
            str += "\n";
        }
        return str;
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

    public void printPart(int start, int end) {
        
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < words; j++) {
                
                System.out.print(mem[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Data access(int addr, int[] data, int s, boolean isRead) { 
        //System.out.println((level == 0 ? "DRAM":"L" + level) + (isRead ? " read " : " write " + data[0] + " " + data[1] + " at ") + addr + " clock: " + clock);
        Data a;
        if (clock == cycles) {
        
            currAddr = addr;
            stage = s;
        }
        if (currAddr == addr && stage == s) { 
            clock--;
            if (isRead) {
                a = read(addr);
                 
                if (!a.done) return wait;
                val = a.data;
                if (level == 1) {
                    int v = a.data[addr % words];
                    val = new int[1];
                    val[0] = v; // if this is L1 read, return array with desired word only
                }
                
            }
            else {
                a = write(addr, data);
                if (!a.done) return wait;
            }
        
            done.data = val;
        
            
            if (clock <= 0) {
                clock = cycles;
                return done;
            }
            
        }
        
        return wait;
    }

    public int getCold() {return coldMisses;}
    public int getCon() {return conMisses;}
    public int getHits() {return hits;}

    public static void main(String[] args) {
        Memory2 DRAM = new Memory2(16, 5, 2, -1, 0, null); 
        int[] line2 = {2, 3};
        int[] line = {0, 1};
        int[] line3 = {4, 5};
        //for (int i = 0; i < 5; i++) {
            //DRAM.access(0, line, 0, false);}

        Memory2 L2 = new Memory2(8, 3, 2, 2, 2, DRAM);
        Memory2 L1 = new Memory2(4, 1, 2, 2, 1, L2);
        
        //while (!(L1.access(0, line, 0, false)).done) {}

        while (!(L1.access(0, line, 0, false)).done) {}
        while (!(L1.access(1, line, 0, false)).done) {}
        while (!(L1.access(2, line2, 0, false)).done) {}
        while (!(L1.access(3, line2, 0, false)).done) {}
        while (!(L1.access(4, line3, 0, false)).done) {}
        while (!(L1.access(5, line3, 0, false)).done) {}
        
        /*
         0: 3    1: 6
         2: 5    3: 0
         4: 1    5: 0
         6: 3    7: 0
         8: 1    9: 0
         */

        System.out.println("DRAM: ");
        DRAM.display();

        System.out.println("L2: ");
        L2.display();

        System.out.println("L1: ");
        L1.display();
        
    }
}
