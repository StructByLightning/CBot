package gui;

import input.ButtonHandler;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import client.ClientType;

public class ClientStarterPanel extends JPanel{
	private ServerController gui;
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	


	public ClientStarterPanel(ServerController gui){
		this.gui = gui;
	
		this.setLayout(new GridLayout(1, 1));

		JButton button;
		
		//Setup start checkin button
		button = new JButton(ClientType.CHECKIN.toString());
		button.setToolTipText("Checkin all accounts that haven't checked in today");
		buttons.add(button);
		
		//Setup start harvest button
		button = new JButton(ClientType.HARVEST.toString());
		button.setToolTipText("Harvest the 30 accounts with the most days logged");
		buttons.add(button);
		
		//Setup start get mail button
		button = new JButton(ClientType.GET_MAIL.toString());
		button.setToolTipText("Get mail from chars that have a flashing envelope");
		buttons.add(button);
		
		//Setup start login button
		button = new JButton(ClientType.LOGIN.toString());
		button.setToolTipText("Login for half an hour (login event)");
		buttons.add(button);
		
		//Setup start populate accs button
		button = new JButton(ClientType.POPULATE_ACCS.toString());
		button.setToolTipText("Create 8 randomized dwarves per account");
		buttons.add(button);
		
		for (int i=0; i<buttons.size(); i++){
			buttons.get(i).addActionListener(new ButtonHandler(gui));
			this.add(buttons.get(i));
		}
	

		this.setBorder(BorderFactory.createTitledBorder("Client Settings"));

	}
}
