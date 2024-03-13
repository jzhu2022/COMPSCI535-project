//test
import java.util.Scanner;

public class Memory {
    enum TYPE {L1, L2, DRAM};
    private TYPE type;
    private int size;
    private int cycles;
    private Memory next;
    private int[][] mem;
    private int clock;
    private int currAddr;
    private String rw;


    public Memory(TYPE t, int s, int c, Memory n) {
        type = t;
        size = s;
        cycles = c;
        clock = cycles;
        next = n;
        // tag | word | dirty | valid
        mem = new int[size][4];
        for (int i = 0; i < size; i++) {
            memo[i][3] = 0;
        }
    }

    public int access(String t, int addr) {
        int v;
        if (clock == cycles) {
            currAddr = addr;
            rw = t;
        }
        else {
            if (currAddr != addr || rw != t)
                return -1; // not the current access
        }

        if (clock == 0) {
            clock = cycles;
            
            if (t == "r") 
                v = read(addr);
            else {
                write(addr);
                v = 0;
            }
        
            return v;
        }
        else {
            clock--;
            return -1; // placeholder for "wait"
        }
    }

    private abstract Line read(int addr) {}

    private abstract void writeLine(Line line) {}


	public static void main(String[] args)
	{
        Memory DRAM = new Memory(TYPE.DRAM, 1600, 3, null);
        Memory L2 = new Memory(TYPE.L2, 1600, 3, DRAM);
        Memory L1 = new Memory(TYPE.L1, 32768, 0, L2);

        Scanner scanner = new Scanner(System.in);
        System.out.println("enter a command");
        String command[] = {""};

        while (!command[0].equals("exit")) {
            command = scanner.nextLine().split(" ");
            if (command[0].charAt(0) == 'W') {
                int value = Integer.parseInt(command[1]);
                int address = Integer.parseInt(command[2]);
                int stage = Integer.parseInt(command[3]);
                L1.access('w', address);
            } else if (command[0].charAt(0) == 'R') {
                int address = Integer.parseInt(command[1]);
                int stage = Integer.parseInt(command[2]);
                L1.access('r', address);
            } else if (command[0].charAt(0) == 'V') {
                int level = Integer.parseInt(command[1]);
                int line = Integer.parseInt(command[2]);
            }
        }
        scanner.close();
    }
}