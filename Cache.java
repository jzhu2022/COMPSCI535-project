public class Cache extends Memory {
    private int numSets;
    private int ways = 4;
    private int lineSize = 2;
    private Set[] sets;

    public Cache(int s, int c, Memory n, int ns) {
        super(TYPE.L1, s, c, n);

        numSets = ns

        sets = new Set[numSets];
        for (int i = 0; i < ns; i++){
            sets[i] = new Set(ways, Set.POLICY.LRU, lineSize);
        }
    }

    public CacheLine read(int addr) {
        if getType().equals(TYPE.L1) {
            return access(addr).getData()
        } else {
            return access(addr);
        }
    }

    public void writeLine(CacheLine line) {
        access(line.getAddr());
        CacheLine c = new CacheLine(lineSize, addr / (numSets + lineSize));
        c.set
    }

    private CacheLine access(int addr) {
        int tag = addr / (numSets * lineSize);
        int index = (addr / lineSize) % (2^numSets); 

        if (sets[index].find(tag) == -1) {
            Line line = next.access(addr);
            CacheLine c = new CacheLine(line.getLineSize(), tag);
            c.setData(line.getData());

            if (sets[index].needsEviction()) {
                CacheLine e = sets[index].evicted();
                next.writeLine(e);
            }
            
            sets[index].write(c);
            return c;
        }
        else {
            return sets[index].getLine(tag);
        }
    }
}