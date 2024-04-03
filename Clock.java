import java.util.Scanner;
public class Clock {

    private int cycles;
    private Pipeline pipeline;
    
    public Clock() {
        cycles = 0;
    }

    public void makePipeline(Pipeline p) {
        pipeline = p;
    }
    public void cycle() {
        pipeline.cycle();
        cycles++;
    }

    public int getCycles() {
        return cycles;
    }

    public static void main(String[] args) {
        Memory2 DRAM = new Memory2((int)Math.pow(2,13), 10, 16, -1, 0, null);
        Memory2 L2 = new Memory2((int)Math.pow(2,8), 5, 16, 4, 2, DRAM);
        Memory2 L1 = new Memory2((int)Math.pow(2,6), 1, 16, 2, 1, L2);

        Pipeline p = new Pipeline(L1);
        Clock c = new Clock();
        c.makePipeline(p);

        Scanner scanner = new Scanner(System.in);
        String[] input = {""};
        while (!input[0].equals("exit")) {
            input = scanner.nextLine().split(" ");

            if (input[0].equals("cycle")) c.cycle();

            else if (input[0].equals("display")) {
                String level = input[1];
                if (level.equals("DRAM")) 
                    DRAM.printPart(Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                else if (level.equals("L1")) 
                    L1.printPart(Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                else if (level.equals("L2")) 
                    L2.printPart(Integer.parseInt(input[2]), Integer.parseInt(input[3]));
            }
        }
        

        scanner.close();
    }
}
