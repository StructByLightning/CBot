package errorMessage;

import javax.swing.JOptionPane;

public class ErrorMessage {
	
	public static void throwError(String error){
		JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void throwFatalError(String error){
		JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.PLAIN_MESSAGE);
		System.exit(0);
	}
}
