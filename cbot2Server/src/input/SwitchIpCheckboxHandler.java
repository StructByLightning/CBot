package input;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import config.Config;

public class SwitchIpCheckboxHandler implements ItemListener{
	
	public SwitchIpCheckboxHandler(){
		super();
	}
	
	public void itemStateChanged(ItemEvent e) {
		String command = ((JCheckBox)e.getItemSelectable()).getActionCommand();
		//box was unchecked
		if (e.getStateChange() == ItemEvent.DESELECTED){
			if (command.equals("Switch IP")){
				Config.setSwitchIp(false);
			}
		} else {
			if (command.equals("Switch IP")){
				Config.setSwitchIp(true);
			}
		}
		
		
	}
	

}
