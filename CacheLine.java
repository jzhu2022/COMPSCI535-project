public class CacheLine extends Line {
    private boolean dirty;
    private boolean valid;
    private int tag;
    private int priority;


    public CacheLine(int l, int tag) {
        super(l);
        dirty = false;
        valid = false;
        this.tag = tag;
        priority = 100;

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

    public void incrementP() {
        priority++;
    }
}