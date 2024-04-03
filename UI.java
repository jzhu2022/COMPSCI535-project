import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
class UI extends JPanel implements ActionListener {
	static JTextField t;
	static JFrame frame;
	static JButton button;
	static JLabel label;
	static int clock; // change type to Clock
	static int pipeline; // change type to Pipeline
	private int boxWidth = 70;
	private int boxSpace = 30;
	private int initX = 50;
	private int initY = 50;
	private final String[] STAGE_NAMES = {"Fetch", "Decode", "Execute", "Memory", "Writeback"};

	public UI() {
		
	}

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

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawPipeline(g);
	
	
		
	}

	private void drawPipeline(Graphics g) {
		String[] instructions = {"add", "sub", "add", "sub", "mul"}; // placeholder, get values from pipeline
		for (int i = 0; i < 5; i++ ){
			g.drawRect(initX + i*(boxWidth+boxSpace), initY, boxWidth, boxWidth);
			if (i != 4) g.drawLine(initX + boxWidth + i*(boxWidth+boxSpace), initY + boxWidth/2,initX + boxWidth + i*(boxWidth+boxSpace)+boxSpace, initY + boxWidth/2);
			g.drawString(STAGE_NAMES[i], initX + i*(boxWidth+boxSpace) + (-3*STAGE_NAMES[i].length() + 35), initY+boxWidth+15);
			g.drawString(instructions[i], initX + i*(boxWidth+boxSpace) + (-3*STAGE_NAMES[i].length() + 40), initY+boxWidth/2);
		}
		
	}

	private JTable drawMemory(Memory2 m, int start, int end) {
		Integer[] a = new Integer[m.getWords()];
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		JTable mem = new JTable(m.displayPart(start, end), a);
		return mem;

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
		}
	}
	public static void main(String[] args) {
		UI ui = new UI();
		frame = new JFrame("");

		label = new JLabel("nothing entered");

		button = new JButton("submit");


		button.addActionListener(ui);

		t = new JTextField(16);

		JPanel p = new JPanel();

		//ui.add(t);
		//ui.add(button);
		//ui.add(label);

		Memory2 m = new Memory2(2 * 32, 1, 2, -1, 0, null); 
		ui.add(ui.drawMemory(m, 0, 0));
		frame.add(ui);
		ui.repaint();

		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	
}
