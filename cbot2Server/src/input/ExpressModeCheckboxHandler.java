package input;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import config.Config;

public class ExpressModeCheckboxHandler implements ItemListener{
	
	public ExpressModeCheckboxHandler(){
		super();
	}
	
	public void itemStateChanged(ItemEvent e) {
		String command = ((JCheckBox)e.getItemSelectable()).getActionCommand();
		//box was unchecked
		if (e.getStateChange() == ItemEvent.DESELECTED){
			if (command.equals("Express Mode")){
				Config.setExpressMode(false);
			}
		} else {
			if (command.equals("Express Mode")){
				Config.setExpressMode(true);
			}
		}
		
		
	}
	

}
