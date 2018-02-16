package gui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class SettingsTabPanel extends JPanel{
	ServerSettingsPanel ssPanel;
	

	public SettingsTabPanel(ServerController controller){

		this.setLayout(new FlowLayout());

		ssPanel = new ServerSettingsPanel(controller);
		
	
		
	


		this.add(ssPanel);

	}

}
