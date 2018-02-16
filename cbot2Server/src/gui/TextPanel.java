package gui;

import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextPanel extends JTextArea{
	private ArrayList<String> contents = new ArrayList<String>();
	
	public TextPanel(int x, int y){
		super(y, x);
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setEditable(true);
		this.setText("");
		
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
		{
		public void adjustmentValueChanged(AdjustmentEvent ae)
		{
		ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
		}
		});

	}
	
	//appends text to the end and automatically scrolls
	public void addText(String text){
		contents.add(text);
		
		String displayed = "";
		for (int i=0; i<contents.size(); i++){
			displayed += contents.get(i) + "\n";
		}
		
		this.setText(displayed);
	}
	
	//wipes the contents
	public void reset(){
		this.contents.clear();
		this.setText("");
	}
	
	//saves the contents to a log file
	public void saveLogs(){
		System.out.println("Saving");
		BufferedWriter out;

        try {
        	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    		Date date = new Date();

            out = new BufferedWriter(new FileWriter("log.txt", true));

            //Write out the specified string to the file
            for (int i=0; i<contents.size(); i++){
	            out.write(dateFormat.format(date) + " ");
            	out.write(contents.get(i));
	            out.write("\n");
            }

            //flushes and closes the stream
            out.close();
        }catch(IOException e){
           

        }
	}
}
