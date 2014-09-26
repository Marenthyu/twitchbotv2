package package1;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Starter {
	
	public static void main(String[] args) {
		
		
		String Channel = "#"+JOptionPane.showInputDialog("Please Input the Channel Name!", "marenthyu");
		Bot bot = new Bot("MarenBot",Channel);
		bot.setVerbose(true);
		
		while (!bot.isConnected()) {
			System.out.println("Bot not connected, trying to connect");
		try {
			
			bot.connect("irc.twitch.tv",6667,"oauth:69gony47an72cj2mxvkixrgd11cpj9r");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
		bot.joinChannel(Channel);
		bot.sendMessage(Channel, "MarenBot is now activated and ready to rock!");
		
		System.out.println("Bot start up complete, opening GUI");
		
		GUI gui = new GUI(bot);


	}

}
