package gui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

public class ClientTabPanel extends JPanel{
	ClientStarterPanel csPanel;
	
	public ClientTabPanel(ServerController controller){

		this.setLayout(new FlowLayout());

		csPanel = new ClientStarterPanel(controller);

		this.add(csPanel);



	}
	
	
}
