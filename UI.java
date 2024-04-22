import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
class UI extends JPanel implements ActionListener {
	static JTextField t;
	static JFrame frame;
	static JButton button;
	static JButton cycleButton;
	static JLabel label;

	static DefaultTableModel memTableModel;
	static DefaultTableModel regTableModel;

	static JTable mem;
	static JTable reg;

	static int clock; // change type to Clock
	static int pipeline; // change type to Pipeline
	private int boxWidth = 300;//70
	private int boxSpace = 30;
	private int initX = 50;
	private int initY = 50;
	private final String[] STAGE_NAMES = {"Fetch", "Decode", "Execute", "Memory", "Writeback"};


	private Pipeline pipe;
	public Instruction[] readOut = {new Instruction(-1073741824), new Instruction(-1073741824), new Instruction(-1073741824), new Instruction(-1073741824), new Instruction(-1073741824)};


	public UI(Memory2 memory) {
		super();

		pipe = new Pipeline(memory);
	}
/*
	public void display() {
		frame = new JFrame("textfield");

		label = new JLabel("nothing entered");

		button = new JButton("Cycle Clock");

		//UI te = new UI();

		//button.addActionListener(te);

		t = new JTextField(16);

		JPanel p = new JPanel();

		p.add(t);
		p.add(button);
		p.add(label);

		frame.add(p);

		Graphics2D g = (Graphics2D) getGraphics();
		paintComponent(g);

		frame.setSize(600, 600);
		frame.setVisible(true);
	}
*/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawPipeline(g);
	}

	private void drawPipeline(Graphics g) {
		int y = getHeight() - initY - boxWidth;
		String[] instructions = {Integer.toBinaryString(readOut[0].instruction), Integer.toBinaryString(readOut[1].instruction), Integer.toBinaryString(readOut[2].instruction), Integer.toBinaryString(readOut[3].instruction), Integer.toBinaryString(readOut[4].instruction)}; // placeholder, get values from pipeline
		for (int i = 0; i < 5; i++ ){
			g.drawRect(initX + i*(boxWidth+boxSpace), y, boxWidth, boxWidth);
			if (i != 4) g.drawLine(initX + boxWidth + i*(boxWidth+boxSpace), y + boxWidth/2,initX + boxWidth + i*(boxWidth+boxSpace)+boxSpace, y + boxWidth/2);
			g.drawString(STAGE_NAMES[i], initX + i*(boxWidth+boxSpace) + (-3*STAGE_NAMES[i].length() + 35), y+boxWidth+15);
			g.drawString(instructions[i], initX + i*(boxWidth+boxSpace) + (-3*STAGE_NAMES[i].length() + 40), y+boxWidth/2);
		}
		
	}

	private JTable drawMemory(Memory2 m, int start, int end) {
		Integer[] a = new Integer[m.getWords()];
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}

		memTableModel = new DefaultTableModel(m.displayPart(start, end), a);
		mem = new JTable(memTableModel);

		return mem;
	}

	private JTable drawRegisters() {
		String[] columnNames = {"Register Addr", "Data"};

		Integer[][] data = new Integer[pipe.registers.length][2];

		for (int i = 0; i < pipe.registers.length; i++) {
			data[i][0] = i;
			data[i][1] = pipe.registers[i];
		}
		System.out.println(data[0][1]);

		regTableModel = new DefaultTableModel(data, columnNames);
		reg = new JTable(regTableModel);
		return reg;
	}

	// if the button is pressed
	public void actionPerformed(ActionEvent e) 
	{
		String s = e.getActionCommand();
		if (s.equals("submit")) {
			// set the text of the label to the text of the field
			label.setText(t.getText());

			// set the text of field to blank
			t.setText(" ");
		} else if (s.equals("cycle") && pipe.notEndOfProgram()) {
			readOut = pipe.cycle();

			regTableModel.fireTableDataChanged();
			remove(reg);
			add(drawRegisters());

			//memTableModel.fireTableDataChanged();
			//remove(mem);
			//cannot redraw memory without reference
			//add(drawMemory());
			//repaint();
		}
	}
	public static void main(String[] args) {

		Memory2 DRAM = new Memory2(2 * 32, 1, 2, -1, 0, null); 
		
		UI ui = new UI(DRAM);
		frame = new JFrame("CS535 Simulator");
		
		label = new JLabel("nothing entered");
		
		button = new JButton("submit");
		cycleButton = new JButton("cycle");
		
		button.addActionListener(ui);
		cycleButton.addActionListener(ui);

		t = new JTextField(16);
		
		JPanel p = new JPanel();

		ui.add(t);
		ui.add(button);
		ui.add(cycleButton);
		ui.add(label);


		ui.add(ui.drawMemory(DRAM, 0, 0));

		ui.add(ui.drawRegisters());

		frame.add(ui);

		ui.repaint();
		
		frame.setSize(600, 600);
		frame.setVisible(true);
	}
}
