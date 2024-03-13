public class Set {
    private int ways;
    enum POLICY = {LRU, FIFO, NO};
    POLICY policy;
    private Lines[] lines;

    public Set(int w, POLICY p) {
        ways = w;
        policy = p;
        lines = new Lines[ways];
    } 

}