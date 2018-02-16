package gui;

import input.AccNumFieldHandler;
import input.ButtonHandler;
import input.ExpressModeCheckboxHandler;
import input.AccountLoadTypeActionListener;
import input.SwitchIpCheckboxHandler;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class ServerSettingsPanel extends JPanel{

	public ServerSettingsPanel(ServerController gui){
		this.setLayout(new GridLayout(2, 1));
		this.setBorder(BorderFactory.createTitledBorder("Server Settings"));

		JPanel[] rows = new JPanel[2];
		
		for (int i=0; i<rows.length; i++){
			rows[i] = new JPanel();
			rows[i].setLayout(new FlowLayout(FlowLayout.LEADING));			
		}
		
		
		//account setting
		JLabel label = new JLabel("Number of accounts to send");
		JTextField field = new JTextField(5);
		field.getDocument().addDocumentListener(new AccNumFieldHandler(field));
		field.setEditable(true);
		field.setText("10");

		rows[0].add(label);
		rows[0].add(field);
		
		//switch ip
		JCheckBox checkbox = new JCheckBox("Switch IP");
		checkbox.setSelected(true);
		checkbox.addItemListener(new SwitchIpCheckboxHandler());
		rows[0].add(checkbox);

		//express mode
		JCheckBox expressMode = new JCheckBox("Express Mode");
		expressMode.setSelected(false);
		expressMode.addItemListener(new ExpressModeCheckboxHandler());
		rows[0].add(expressMode);
		
		//save logs
		JButton button = new JButton("Save logs");
		button.addActionListener(new ButtonHandler(gui));
		rows[0].add(button);
				
		//account load method
		JRadioButton accsNormally = new JRadioButton("Load accounts normally");
		accsNormally.setActionCommand("Load accounts normally");
		accsNormally.setSelected(true);
	    rows[1].add(accsNormally);
		
	    JRadioButton accsFromFileButton = new JRadioButton("Load accounts from file");
	    accsFromFileButton.setActionCommand("Load accounts from file");
	    rows[1].add(accsFromFileButton);
	    
	    JRadioButton accsWithMail = new JRadioButton("Load accounts with mail");
	    accsWithMail.setActionCommand("Load accounts with mail");
	    rows[1].add(accsWithMail);

	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(accsNormally);
	    group.add(accsFromFileButton);
	    group.add(accsWithMail);

	    //Register a listener for the radio buttons.
	    accsNormally.addActionListener(new AccountLoadTypeActionListener());
	    accsFromFileButton.addActionListener(new AccountLoadTypeActionListener());
	    accsWithMail.addActionListener(new AccountLoadTypeActionListener());	
		
		for (int i=0; i<rows.length; i++){
			this.add(rows[i]);		
		}
	}
}
