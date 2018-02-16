package input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;

import config.Config;

public class AccountLoadTypeActionListener implements ActionListener{
	
	public AccountLoadTypeActionListener(){
		super();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals("Load accounts normally")){
			Config.setLoadAccsType(Config.LOAD_ACCS_NORMALLY);
		} else if (command.equals("Load accounts from file")){
			Config.setLoadAccsType(Config.LOAD_ACCS_FROM_FILE);
		} else if (command.equals("Load accounts with mail")){
			Config.setLoadAccsType(Config.LOAD_ACCS_WITH_MAIL);
		}
	}
}
