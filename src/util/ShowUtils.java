package util;

import javax.swing.JOptionPane;

public class ShowUtils {
	public static void warningMessage(String msg){
		JOptionPane.showMessageDialog(null, msg, "警告", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void errorMessage(String msg){
		JOptionPane.showMessageDialog(null, msg, "错误", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void infoMessage(String msg){
		JOptionPane.showMessageDialog(null, msg);
	}
}
