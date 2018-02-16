package gui;

import javax.swing.JLabel;

import accounts.AccountHandler;

public class AccsRemainingLabel extends JLabel{
	
	public AccsRemainingLabel(){
		super("Accs:(0000/0000)");
		AccountHandler.setArLabel(this);
		
	}
	
	public void update(int numAccs, int totalAccs){
		String text = "Accs:(" + numAccs + "/" + totalAccs + ")";
		this.setText(text);
		

	}
}
