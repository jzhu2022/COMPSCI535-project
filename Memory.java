import java.util.Scanner;

public abstract class Memory {
    private int size;
    private int cycles;
    private Memory next;
    private int clock;
    private int currAddr;
    private String rw;


    public Memory(int s, int c, Memory n) {
        size = s;
        cycles = c;
        clock = cycles;
        next = n;
    }

    public int access(String t, int addr, int data) {
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
                v = read(addr.getIndex());
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

    private abstract int read(int addr) {}

    private abstract void write(int addr, Data data) {}


	public static void main(String[] args)
	{
        Memory DRAM = new Memory(TYPE.DRAM, 1600, 3, null);
        Memory L2 = new Memory(TYPE.L2, 1600, 3, DRAM);
        Memory L1 = new Memory(TYPE.L1, 1600, 0, L2);

        Scanner scanner = new Scanner(System.in);
        System.out.println("enter a command");
        String command[] = {""};

        while (!command[0].equals("exit")) {
            command = scanner.nextLine().split(" ");
            if (command[0].charAt(0) == 'W') {
                int value = Integer.parseInt(command[1]);
                int address = Integer.parseInt(command[2]);
                int stage = Integer.parseInt(command[3]);
                L1.write(address);
            } else if (command[0].charAt(0) == 'R') {
                int address = Integer.parseInt(command[1]);
                int stage = Integer.parseInt(command[2]);
                L1.read(address);
            } else if (command[0].charAt(0) == 'V') {
                int level = Integer.parseInt(command[1]);
                int line = Integer.parseInt(command[2]);
            }
        }
        scanner.close();
    }
}