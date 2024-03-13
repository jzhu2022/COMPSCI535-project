public class Cache extends Memory {
    private int numSets;
    private int ways = 4;
    private int lineSize = 2;
    private Sets[] sets;

    public Cache(int s, int c, int n, int ns) {
        super(s, c, n);

        sets = new Sets[ns];
        for (int i = 0; i < ns; i++){
            sets[i] = new Set(ways, POLICY.LRU, lineSize);
        }
    }

    public int read(int addr) {
        return access(addr).getData();
    }

    public int write(int addr, int data) {
        int tag = addr / (numSets + lineSize);
        int index = (addr/lineSize)%(2^numSets); 

        set[index].write(tag, data);
    }

    private CacheLine access(int addr) {
        int tag = addr / (numSets + lineSize);
        int index = (addr/lineSize)%(2^numSets); 

        if (set[index].find(tag) == -1) {
            Line line = next.access(addr);
            CacheLine c = new CacheLine(line.getLineSize(), tag);
            c.setData(line.getData());
            //sets[index].put(tag, c);
            int loc = sets[index].findNext();
            if sets[index]
            return c;
        }
        else {
            return set[index].getLine(tag);
        }
    }


}