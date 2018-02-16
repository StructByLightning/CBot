package gui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import accounts.AccountHandler;

public class AccsTabPanel extends JPanel{
	AccountPanel remAccs;
	AccountPanel usedAccs;
	
	public AccsTabPanel(ServerController controller){
		this.setLayout(new FlowLayout());

		remAccs = new AccountPanel(20, 15, "Remaining");
		usedAccs = new AccountPanel(20, 15, "Used");
		
		AccountHandler.setRemAccsOutput(remAccs);
		AccountHandler.setUsedAccsOutput(usedAccs);
		
		this.add(remAccs);
		this.add(usedAccs);
	}
	

}
