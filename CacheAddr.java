public class CacheAddr extends Addr {
    private boolean dirty;
    private boolean valid;
    private int tag;

    public CacheAddr(int i, int d) {
        super(i, d);
        dirty = false;
        valid = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isValid() {
        return valid;
    }

    public int getTag() {
        return tag;
    }

}