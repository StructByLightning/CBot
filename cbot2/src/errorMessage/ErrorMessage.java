package errorMessage;

import javax.swing.JOptionPane;

public class ErrorMessage {
	
	public static void throwError(String error){
		JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.PLAIN_MESSAGE);
	}
}
