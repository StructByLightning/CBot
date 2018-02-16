package input;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import config.Config;

public class AccNumFieldHandler implements DocumentListener{
	private JTextField field;

	public AccNumFieldHandler(JTextField field){
		this.field = field;
	}

	//all three varieties of update call this
	public void update(){
		String str = field.getText(); 
		if (str.length() <= 0){
			Config.setNumAccounts(0);
		} else if(str.matches("\\d+")) { 
			Config.setNumAccounts(Integer.parseInt(str));
		} 

	}
	
	public void insertUpdate(DocumentEvent arg0) {update();}
	public void removeUpdate(DocumentEvent arg0) { update();}
	public void changedUpdate(DocumentEvent arg0) {update();}
}

