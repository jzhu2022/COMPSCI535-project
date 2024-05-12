import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
class UI extends JPanel implements ActionListener {
	static int useCache = -1;
    static int usePipeline = -1;
    static JLabel cacheLabel;
    static JLabel pipelineLabel;
    static JComboBox<String> cacheSelect;
    static JComboBox<String> pipelineSelect;
    static JButton continueButton;
    static boolean chosen = false;

	static JTextArea DRAMText, L2Text, L1Text;
    static JTextArea pipeText;
	static JTextArea registerText;
	static JLabel L1Hits, L2Hits, L1ConMisses, L2ConMisses, L1ColdMisses, L2ColdMisses;

	static JTextField t;
	static JTextField cycleCount;
	static JFrame frame;
	static JButton button;
	static JButton cycleButton;
	static JButton flushButton;
	static JButton runButton;

	static DefaultTableModel memTableModel;
	static DefaultTableModel regTableModel;

	static JTable mem;
	static JTable reg;

	static int DRAMSize = 16, L2Size = 8, L1Size = 4;
	static int wordsPerLine = 2;


	static int clock; // change type to Clock
	static final String[] STAGE_NAMES = {"Fetch", "Decode", "Execute", "Memory", "Writeback"};
	static final String[] REGISTER_NAMES = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12", "R13", "PS", "PC"};


	static Memory2 DRAM, L1, L2;
	private Pipeline pipe;
	public Instruction[] readOut = {new Instruction(-1073741824), new Instruction(-1073741824), new Instruction(-1073741824), new Instruction(-1073741824), new Instruction(-1073741824)};


	public UI() {
		super();
	}

	public void setMemory(Memory2 m) {
		pipe = new Pipeline(m);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//drawPipeline(g);
	}

	private void updatePipeline() {
		/*
		String[] instructions = {Integer.toBinaryString(readOut[0].instruction), 
								 Integer.toBinaryString(readOut[1].instruction), 
								 Integer.toBinaryString(readOut[2].instruction), 
								 Integer.toBinaryString(readOut[3].instruction), 
								 Integer.toBinaryString(readOut[4].instruction)}; // placeholder, get values from pipeline
		*/
		String str = "";
		for (int i = 0; i < 5; i++ ) {
			str += STAGE_NAMES[i] + ": " + Integer.toBinaryString(readOut[i] == null ? 0 : readOut[i].instruction) + "\n";
		}
		pipeText.setText(str);
		
	}

	private void updateMemory() { // change to update text areas
		DRAMText.setText(DRAM.toString());
		if (useCache == 1) {
			L1Text.setText(L1.toString());
			L2Text.setText(L2.toString());
		}
	}

	private void updateStats() {
		if (useCache == 1) {
			L1Hits.setText("L1 Cache Hits: " + L1.getHits());
			L2Hits.setText("L2 Cache Hits: " + L2.getHits());
			L1ConMisses.setText("L1 Cache Conflict Misses: " + L1.getCon());
			L2ConMisses.setText("L2 Cache Conflict Misses: " + L2.getCon());
			L1ConMisses.setText("L1 Cache Cold Misses: " + L1.getCold());
			L2ConMisses.setText("L2 Cache Cold Misses: " + L2.getCold());
		}
	}

	private void updateRegisters() {
		String str = "";
		for (int i = 0; i < pipe.registers.length; i++) {
			str += " " + REGISTER_NAMES[i] + ": " + pipe.registers[i] + "\n";
		}

		registerText.setText(str);		
	}

	// if the button is pressed
	public void actionPerformed(ActionEvent e) 
	{
		String s = e.getActionCommand();
		if (s.equals("Submit")) {
			// set the text of the label to the text of the field
			//label.setText(t.getText());
			for (int i = 0; i < Integer.valueOf(t.getText()); i++) {
				readOut = usePipeline == 1 ? pipe.cycle() : pipe.cycleNoPipeline();
				clock++;	
			}

			cycleCount.setText("" + clock);

			updateMemory();
			updatePipeline();
			updateRegisters();
			updateStats();
			//regTableModel.fireTableDataChanged();
			//remove(reg);
			//add(drawRegisters());

			//memTableModel.fireTableDataChanged();
			//remove(mem);
			//add(drawMemory(pipe.memory, 0, 1));


			// set the text of field to blank
			//t.setText(" ");
		} else if (s.equals("Cycle") && pipe.notEndOfProgram()) {
			readOut = usePipeline == 1 ? pipe.cycle() : pipe.cycleNoPipeline();
			clock++;

			cycleCount.setText("" + clock);


			//regTableModel.fireTableDataChanged();
			//remove(reg);
			//add(drawRegisters());

			//memTableModel.fireTableDataChanged();
			//remove(mem);
			//add(drawMemory(pipe.memory, 0, 1));

			updateMemory();
			updatePipeline();
			updateRegisters();
			updateStats();
		} else if (s.equals("Flush Cache")) {
			if (useCache == 1) {
				L1.evictAll();
				L2.evictAll();
				updateMemory();

			}
		} else if (s.equals("Run")) {
			while (pipe.notEndOfProgram()) {
				if (usePipeline == 1)  pipe.cycle();
				else pipe.cycleNoPipeline();
				clock++;
			}
			updateMemory();
			updatePipeline();
			updateRegisters();
			updateStats();
			cycleCount.setText("" + clock);

		}
	}

	public static void addStats(JPanel panel, JLabel hits, JLabel conMisses, JLabel coldMisses, Memory2 mem) {
		panel.add(hits);
		panel.add(conMisses);
		panel.add(coldMisses);
	}
	public static JPanel makeTextArea(String name, JTextArea memText, int level) {
		memText.setEditable(false);
		JPanel memPanel = new JPanel();
		memPanel.setLayout(new BorderLayout());
		memPanel.add(new JLabel(name), BorderLayout.PAGE_START);

		memPanel.add(new JScrollPane(memText), BorderLayout.CENTER);
		
		return memPanel;
	}
	public static void main(String[] args) throws Exception {
		cacheLabel = new JLabel("Cache? ");
        pipelineLabel = new JLabel("Pipeline? ");
        String[] yesNo = {"", "Yes", "No"}; 
        cacheSelect = new JComboBox<String>(yesNo);
        pipelineSelect = new JComboBox<String>(yesNo);
        cacheSelect.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                useCache = cacheSelect.getSelectedItem().equals("Yes") ? 1 : 0;
            }
        });

        pipelineSelect.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                usePipeline = pipelineSelect.getSelectedItem().equals("Yes") ? 1 : 0;
            }
        });

        continueButton = new JButton("Continue");
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (useCache != -1 && usePipeline != -1) chosen = true;
                 
            }
        });

 
		
		UI ui = new UI();
		frame = new JFrame("CS535 Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		button = new JButton("Submit");
		cycleButton = new JButton("Cycle");
		flushButton = new JButton("Flush Cache");
		runButton = new JButton("Run");
		
		button.addActionListener(ui);
		cycleButton.addActionListener(ui);
		flushButton.addActionListener(ui);
		runButton.addActionListener(ui);

		t = new JTextField(16);

		cycleCount = new JTextField(16);
		
		// JPanel p = new JPanel();
		ui.add(cacheLabel);
        ui.add(cacheSelect);
        ui.add(pipelineLabel);
        ui.add(pipelineSelect);
        ui.add(continueButton);

		frame.add(ui);
		frame.setSize(1000, 1000);
		frame.setVisible(true);

		while (!chosen){Thread.sleep(100);}

		ui.remove(cacheLabel);
		ui.remove(cacheSelect);
        ui.remove(pipelineLabel);
        ui.remove(pipelineSelect);
        ui.remove(continueButton);


		DRAM = new Memory2(DRAMSize, 10, wordsPerLine, -1, 0, null);
        L2 = new Memory2(L2Size, 5, wordsPerLine, 2, 2, DRAM);
        L1 = new Memory2(L1Size, 1, wordsPerLine, 2, 1, L2);

		ui.setMemory(useCache == 1 ? L1 : DRAM);

	
		JPanel left = new JPanel();
        JPanel topRight = new JPanel();
        JPanel bottomRight = new JPanel();

		left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		GridLayout leftLayout = new GridLayout(useCache==1 ? 4: 2, 1, 10, 10);
        
		left.setLayout(leftLayout);


		registerText = new JTextArea();
		left.add(makeTextArea("Registers", registerText, 0));

		if (useCache == 1) {
			L1Hits = new JLabel();
			L1ColdMisses = new JLabel();
			L1ConMisses = new JLabel();
			L2Hits = new JLabel();
			L2ColdMisses = new JLabel();
			L2ConMisses = new JLabel();
			L1Text = new JTextArea(L1.toString());
			L2Text = new JTextArea(L2.toString());
			left.add(makeTextArea(L1.getName(), L1Text, 1));
			left.add(makeTextArea(L2.getName(), L2Text, 2));
			
        }
		
		DRAMText = new JTextArea(DRAM.toString());
		left.add(makeTextArea(DRAM.getName(), DRAMText, 0));

		topRight.setLayout(new BorderLayout());

		pipeText = new JTextArea();
		pipeText.setEditable(false);
		ui.updatePipeline();
		topRight.add(new JLabel("Pipeline"), BorderLayout.PAGE_START);
		topRight.add(pipeText, BorderLayout.CENTER);

		bottomRight.setLayout(new GridLayout(2, 0));

		JPanel control = new JPanel();
		control.add(t);
		control.add(cycleCount);
		control.add(button);
		control.add(cycleButton);
		control.add(runButton);

		JPanel cache = new JPanel();
		if (useCache == 1) { 
			control.add(flushButton);
			cache.add(L1Hits);
			cache.add(L1ColdMisses);
			cache.add(L1ConMisses);
			cache.add(L2Hits);
			cache.add(L2ColdMisses);
			cache.add(L2ConMisses);
		}

		bottomRight.add(control);
		bottomRight.add(cache);

		ui.updateStats();

		JSplitPane right = new JSplitPane(SwingConstants.HORIZONTAL, topRight, bottomRight);
		JSplitPane whole = new JSplitPane(SwingConstants.VERTICAL, left, right);

		frame.remove(ui);
		frame.add(whole);
		frame.revalidate();

		/*
		ui.add(ui.drawMemory(L1, 0, 1));

		ui.add(ui.drawRegisters());

		

		ui.repaint();
		*/
		
	}
}
