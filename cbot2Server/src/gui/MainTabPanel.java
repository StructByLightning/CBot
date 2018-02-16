package gui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MainTabPanel extends JPanel{
	OutputPanel opPanel;
	StatusPanel sPanel;
	
	public MainTabPanel(ServerController controller){

		this.setLayout(new FlowLayout());

		opPanel = new OutputPanel(36, 15);
		sPanel = new StatusPanel();
		
		this.add(opPanel);
		this.add(sPanel);
		

	}
	
	public OutputPanel getOpPanel(){
		return this.opPanel;
	}
}
