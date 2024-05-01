import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

//create hash map of labels(assembly lines that begin with a '.'): label:line #
//whenever a label is used reference that line #
public class Assembler {
    private Dictionary<String, Integer> symbols = new Hashtable<>();
    private int currentLine;

    public Assembler() {
        symbols = new Hashtable<>();
        currentLine = 0;
    }

    public void buildSymbolTable() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("assembly.txt"));
            String line = reader.readLine();

            while(line != null) {
                //remove comment from assembly
                if (line.indexOf("--") != -1) {
                    line = line.substring(0, line.indexOf("--"));
                }
                //check if label
                if (line.charAt(0) == '.') {
                    symbols.put(line, currentLine);
                } else {
                    //labels will not be counted as instructions, we do not want branches to branch back to were the assembler thinks a label is
                    currentLine++;
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assemble() {
        buildSymbolTable();

        currentLine = 0;

        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("assembly.txt"));
			String line = reader.readLine();

            BufferedWriter writer = new BufferedWriter(new FileWriter("program.txt", false));

			while(line != null) {   
                //remove comment from assembly
                if (line.indexOf("--") != -1) {
                    line = line.substring(0, line.indexOf("--"));
                }
                //build machine code
                int instruction = interpretAssemblyLine(line);
                if (instruction != 0) {
                    writer.write(String.valueOf(instruction));
                    writer.newLine();
                }
				//read next line
				line = reader.readLine();
                currentLine++;
			}

			reader.close();
            writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    public int interpretAssemblyLine(String line) {
        //labels do not have operands
        if (line.charAt(0) == '.') {
            return 0;
        }

        String mnemonic;
        String [] operands;
        if (line.indexOf(" ") != -1) {
            mnemonic = line.substring(0, line.indexOf(" "));
            operands = line.substring(mnemonic.length() + 1).split("\\s*,\\s*");
        } else {
            mnemonic = line;
            operands = null;
        }

        int instruction = 0;
        //condition code
        int condition = 7;
        instruction |= condition << 29;

        if (mnemonic.equals("add")) {
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("sub")) {
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 2;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 3;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("mul")) {
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 4;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 5;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("div")) {
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 6;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 7;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("mod")) {
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 8;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 9;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("cmp")) {
            //src1 register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            if (operands[1].charAt(0) == 'r') {
                //opcode
                int opcode = 10;
                instruction |= opcode << 22;
                //src2  
                instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            } else {
                //opcode
                int opcode = 11;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[1].substring(1));
            }
        } else if (mnemonic.equals("fadd")) {
            //type
            int type = 1;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("fsub")) {
            //type
            int type = 1;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 2;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 3;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("fmul")) {
            //type
            int type = 1;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 4;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 5;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("fdiv")) {
            //type
            int type = 1;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 6;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 7;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("fcmp")) {
            //type
            int type = 1;
            instruction |= type << 26;
            
            //src1 register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            if (operands[1].charAt(0) == 'r') {
                //opcode
                int opcode = 8;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            } else {
                //opcode
                int opcode = 9;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[1].substring(1));
            }
        } else if (mnemonic.equals("and")) {
            //type
            int type = 2;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("or")) {
            //type
            int type = 2;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            if (operands[2].charAt(0) == 'r') {
                //opcode
                int opcode = 2;
                instruction |= opcode << 22;
                //src2
                instruction |= Integer.valueOf(operands[2].substring(1)) << 10;
            } else {
                //opcode
                int opcode = 3;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[2].substring(1));
            }
        } else if (mnemonic.equals("not")) {
            //type
            int type = 4;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            //opcode
            int opcode = 4;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("lsl")) {
            //type
            int type = 2;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            //immediate
            instruction |= Integer.valueOf(operands[2].substring(1));
            //opcode
            int opcode = 6;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("lsr")) {
            //type
            int type = 2;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            //immediate
            instruction |= Integer.valueOf(operands[2].substring(1));
            //opcode
            int opcode = 7;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("asl")) {
            //type
            int type = 2;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            //immediate
            instruction |= Integer.valueOf(operands[2].substring(1));
            //opcode
            int opcode = 8;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("asr")) {
            //type
            int type = 2;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //src1
            instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            //immediate
            instruction |= Integer.valueOf(operands[2].substring(1));
            //opcode
            int opcode = 9;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("ldr")) {
            //type
            int type = 3;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //base
            instruction |= Integer.valueOf(operands[1].substring(1));
            //opcode
            int opcode = 0;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("str")) {
            //type
            int type = 3;
            instruction |= type << 26;
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            //base
            instruction |= Integer.valueOf(operands[1].substring(1));
            //opcode
            int opcode = 3;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("mov")) {  
            //type
            int type = 3;
            instruction |= type << 26;            
            //dest register
            instruction |= Integer.valueOf(operands[0].substring(1)) << 18;
            if (operands[1].charAt(0) == 'r') {
                //opcode
                int opcode = 6;
                instruction |= opcode << 22;
                //src1
                instruction |= Integer.valueOf(operands[1].substring(1)) << 14;
            } else {
                //opcode
                int opcode = 7;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[1].substring(1));
            }
        } else if (mnemonic.equals("b")) {
            //type
            int type = 4;
            instruction |= type << 26;
            if (operands[0].charAt(0) == '.') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //label
                instruction |= Integer.valueOf(symbols.get(operands[0]));
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[0]);
            }
        } else if (mnemonic.equals("beq")) {  
            condition = 0;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            if (operands[0].charAt(0) == '.') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //label
                instruction |= Integer.valueOf(symbols.get(operands[0]));
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[0]);
            }
        } else if (mnemonic.equals("bg")) {  
            condition = 1;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            if (operands[0].charAt(0) == '.') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //label
                instruction |= Integer.valueOf(symbols.get(operands[0]));
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[0]);
            }
        } else if (mnemonic.equals("bgeq")) {  
            condition = 2;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            if (operands[0].charAt(0) == '.') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //label
                instruction |= Integer.valueOf(symbols.get(operands[0]));
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[0]);
            }
        } else if (mnemonic.equals("bl")) {  
            condition = 3;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            if (operands[0].charAt(0) == '.') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //label
                instruction |= Integer.valueOf(symbols.get(operands[0]));
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[0]);
            }
        } else if (mnemonic.equals("bleq")) {  
            condition = 4;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            if (operands[0].charAt(0) == '.') {
                //opcode
                int opcode = 0;
                instruction |= opcode << 22;
                //label
                instruction |= Integer.valueOf(symbols.get(operands[0]));
            } else {
                //opcode
                int opcode = 1;
                instruction |= opcode << 22;
                //immediate
                instruction |= Integer.valueOf(operands[0]);
            }
        } else if (mnemonic.equals("BL")) {  
            condition = 4;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            //opcode
            int opcode = 2;
            instruction |= opcode << 22;
            //label
            instruction |= Integer.valueOf(symbols.get(operands[0]));
        } else if (mnemonic.equals("BX")) {  
            condition = 4;
            instruction &= condition << 29; 
            //type
            int type = 4;
            instruction |= type << 26;
            //opcode
            int opcode = 3;
            instruction |= opcode << 22;
        } else if (mnemonic.equals("halt")) {
            //type
            int type = 5;
            instruction |= type << 26;            
        } else {
            System.out.println("instruction at " + currentLine + " was not recognized");
        }

        return instruction;
    }
}