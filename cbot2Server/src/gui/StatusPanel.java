package gui;

import javax.swing.JPanel;

public class StatusPanel extends JPanel{
	AccsRemainingLabel arLabel;
	
	public StatusPanel(){
		arLabel = new AccsRemainingLabel();
		
		this.add(arLabel);
	}
	
}
