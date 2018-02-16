package input;

import gui.ServerController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.ClientType;
import server.BotStarter;
import config.Config;

public class ButtonHandler  implements ActionListener{
	private ServerController gui;
	
	public ButtonHandler(ServerController gui){
		super();
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("Save logs")){
			gui.getOutputPanel().saveLogs();
		} else {
			if (!Config.startedBot()){
				Config.start();
				new Thread(new BotStarter(gui, ClientType.valueOf(e.getActionCommand()))).start();
			}
		}
	}
}
