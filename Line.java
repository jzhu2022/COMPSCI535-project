public class Line {
    private int[] data;
    private int lineSize;
    private int addr;

    public Line(int l) {
        lineSize = l;
        data = new int[lineSize];
        for (int i = 0; i < lineSize; i++) {
            data[i] = 0;
        }
    }

    public int getWord(int w) {
        return data[w];
    }

    public void setWord(int data, int word) {
        data[w] = d;
    }

    public int[] getData() {return data;}
    public void setData(int[] d) {data = d;}
    public int getPriority() {return priority;}
    public int getLineSize() {return lineSize};
    public int getAddr() {return addr;}

}