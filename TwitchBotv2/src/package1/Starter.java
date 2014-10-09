package package1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class Starter {
	static BufferedWriter out4;
	static String[][] options;
	
	public static void main(String[] args) {
		
		try {
			
			refreshOptions();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//System.out.println("Getting option... Result: "+getOption("testoption"));
		String oauth = getOption("oauth");
		String name = getOption("name");
		String Channel = "#"+JOptionPane.showInputDialog("Please Input the Channel Name!", "marenthyu");
		Bot bot = new Bot(name,Channel);
		bot.setVerbose(false);
		
		while (!bot.isConnected()) {
			System.out.println("Bot not connected, trying to connect");
		try {
			bot.connect("irc.twitch.tv",6667,oauth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
		bot.joinChannel(Channel);
		bot.sendMessage(Channel, "MarenBot is now activated and ready to rock!");
		
		System.out.println("Bot start up complete, opening GUI");
		
		@SuppressWarnings("unused")
		GUI gui = new GUI(bot);


	}
	public static void refreshOptions() throws Exception {
		if (!new File("options.txt").exists()) {
			System.out.println("Options file didnt exist, initializing...");
			addOption("testoption","true");
			addOption("name",JOptionPane.showInputDialog("Please enter the bot's Name"));
			addOption("oauth",JOptionPane.showInputDialog("Please enter the bot's oauth"));
		
		} else {
			System.out.println("Options file found, coninuing reading...");
			}
		
			
		
		
		options = new String[Bot.countLines("options.txt")][2];
	
		InputStream    fis;
		BufferedReader br;
		String         line;

		fis = new FileInputStream("options.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = 0;
		while (((line = br.readLine()) != null)&&!line.equals(System.getProperty("line.separator"))) {
		
		    String[] parts = line.split(Character.toString('='));
		
		    for (int u=0;u<2;u++)
		    options[i][u] = parts[u];
		    i++;
		}

		// Done with the file
		br.close();
		br = null;
		fis = null;
//		System.out.println("Option 1: "+options[0][0]+" is set to "+options[0][1]);
}

	public static String getOption(String option) {
		int i=0;
		String ret = "Not found";
		for (i=0;i<options.length;i++) {
			if (options[i][0].equals(option)){ ret = options[i][1]; break;}
		}
		if (ret.equals("Not found")) return null;
		
		return options[i][1];
	}
	
	public void setOption(String option, String value)  {
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		

		try {
			fis = new FileInputStream("options.txt");
		
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = -1;
		String temp="";
		option = option.toLowerCase();
		//System.out.println("Option: "+option);
		
			while ((line = br.readLine()) != null) {
			//	System.out.println("Current line: "+line);
				String[] parts = line.split("=");
				i++;
				if (parts[0].equalsIgnoreCase(option)) {
				//	System.out.println("i = "+i);
					//System.out.println("Old Value: "+options[i][1]);
					temp = options[i][1];
					options[i][1] = value;
					break;
				}
				
			}
		
		

		// Done with the file
		br.close();
		br = null;
		fis = null;
		String content = new String(Files.readAllBytes(Paths.get("options.txt")));
//		System.out.println("New Value: "+options[i][1]);
		content = content.replaceAll(option+"="+temp, option+"="+options[i][1]);
		Files.write(Paths.get("options.txt"), content.getBytes());
		refreshOptions();} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addOption(String option, String value) {
		try {
			out4 = new BufferedWriter(new FileWriter("options.txt",true));
			out4.append(option+"="+value+System.getProperty("line.separator"));
			out4.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
