package fScript;

import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class WriteFile {
	private String filename;
	private String[] text;
	
	public WriteFile (String[] text, String filename){
		this.filename = filename;
		this.text = text;
	}
	
	public void writeFile(){
		BufferedWriter out;

        try {
            
            out = new BufferedWriter(new FileWriter(filename, true));

            //Write out the specified string to the file
            for (int i=0; i<text.length; i++){
	            out.write(text[i]);
	            out.write("\n");
            }

            //flushes and closes the stream
            out.close();
        }catch(IOException e){
           

        }
	}
}
