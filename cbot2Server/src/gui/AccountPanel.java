package gui;

import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import accounts.Account;

public class AccountPanel extends JPanel{
	private TextPanel output;
	private JScrollPane sp;
	
	
	public AccountPanel(int x, int y, String name){
		this.setLayout(new FlowLayout());
		
		output =  new TextPanel(x, y);
		sp = new JScrollPane(output);
		this.add(sp);
		

		this.setBorder(BorderFactory.createTitledBorder(name));
	}
	
	public TextPanel getTextPanel(){
		return output;
	}


	public void update(ArrayList<Account> data){
		this.output.reset();
		
		for (int i=0; i<data.size(); i++){
			this.output.addText(data.get(i).getUsername());
		}
		
	}
	
}
