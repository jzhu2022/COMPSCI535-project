public class Set {
    private int ways;
    enum POLICY {LRU, FIFO};
    POLICY policy;
    private int index;
    private int lineSize;
    private CacheLine[] lines;
    private int max;

    public Set(int w, POLICY p, int l) {
        ways = w;
        policy = p;
        lineSize = l;
        lines = new CacheLine[ways];
        for (int i = 0; i < ways; i++) {
            lines[i] = new CacheLine(l, 0);
        }

    } 

    public CacheLine read(int tag) {
        int loc = find(tag);


        return null;
    }

    public int write(int tag, int word, int data) {
        

    }

    public CacheLine getLine(int tag) {
        return lines[find(tag)];
    }

    public int findNext() {
        int max = -1;
        int l = -1;
        for (int i = 0; i < ways; i++) {
            if (!lines[i].isValid()) return i;
            if (lines[i].getPriority() > max) {
                max = lines[i].getPriority();
                l = i;
            }
        }
        return l;
    }

    private int find(int tag) {
        for (int i = 0; i < lineSize; i++) {
            if (lines[i].isValid() && lines[i].getTag() == tag) {
                return i;
            }
        }
        return -1;
    }

    public void put(int tag, CacheLine c) {
        int loc = findEmpty();
        if (loc == -1) {
            loc = evict();
        }

        lines[loc] = c;
    }

    public int evict() {
        int loc = leaving();
        updatePriorities(sets[loc].priority);
        if (lines[loc].isDirty()) {

        }

    }


    private void updatePriorities(int p) {
        for (int i = 0; i < ways; i++) {
            if (lines[i].getPriority() < p) {
                lines[i].incrementP();
            }
        }
    }

    public CacheLine evict() {
        CacheLine c = lines[max];
        updatePriorities(lines[max].getPriority());
        lines[max] = null;
        return c;
    }

    public boolean needsEviction() {
        return lines[findNext()].isValid();
    }

}