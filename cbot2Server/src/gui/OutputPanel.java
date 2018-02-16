package gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class OutputPanel extends JPanel{
	private TextPanel output;
	private JScrollPane sp;
	
	
	public OutputPanel(int x, int y){
		this.setLayout(new FlowLayout());
		
		output =  new TextPanel(x, y);
		sp = new JScrollPane(output);
		this.add(sp);
		

		this.setBorder(BorderFactory.createTitledBorder("Output"));
	}
	
	public TextPanel getTextPanel(){
		return output;
	}
}
