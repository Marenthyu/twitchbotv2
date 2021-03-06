//money = (Integer.parseInt(currentEntry[2]) * (1-((matchtime-(Integer.parseInt(currentEntry[3])))/matchtime))) + Integer.parseInt(currentEntry[2])

package package1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;







import org.jibble.pircbot.*;

public class Bot extends PircBot {
	
	
	static String[][] accounts;

	String[][] commands;

	String[][] options;
	
	private static String currChan = "";
	PrintWriter out;
	
	BufferedWriter out2, out3, outq;
	
	static Charset charset = StandardCharsets.UTF_8;
	private Path path = Paths.get("commands.txt");

	private static Path path2 = Paths.get("accounts.txt");

	public static int time = 1;
	
	private Path quotespath;
	
	InputStream    fis, fisq;
	BufferedReader br, brq;
	BufferedReader reader;
	String         rline;
	String mods = "";
	public static String activepeople = "", peoplewon = "", peoplelost = "";
	static User[] us;
	
	boolean greet = false, quotes = false;

	static boolean verbose = false;

	boolean betsopen = false, slots = false;
	
	TimerTask task, task2;
	Timer timer = new Timer();
	
	
	public Bot () {
		
		this.setName("MarenBot");
		this.setLogin("MarenBot");
		currChan="#trampolinetales";
		setup();
		
	}
	
	public Bot(String Name, String Channel) {
		this.setName(Name);
		this.setLogin(Name);
		currChan=Channel;
		setup();
	}
	
	public void setup() {
		addMod("testaccount","");
		us = getUsers(currChan);		
		task = new TimerTask() {

			@Override
			public void run() {
				User[] us = Bot.us;
				
				for(User u:us) {
					if (Bot.activepeople.indexOf(u.getNick())>=0) {
						try {
							Bot.addFunds(u.getNick(),1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				Bot.activepeople = "";
				
			}
			
		};
		
		
		timer.schedule(task, 0, 60000);
		
		
		try {
			
			out2 = new BufferedWriter(new FileWriter("commands.txt",true));
			out3 = new BufferedWriter(new FileWriter("accounts.txt",true));
			
			refreshCommands();
			refreshAccounts();
			task2 = new TimerTask() {

				@Override
				public void run() {
					Bot.time++;
					try{
					runTimedMessages();} catch(Exception e) {
						e.printStackTrace();
					}
					
				}
				
			};
			timer.schedule(task2, 0, 1000);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		if (Starter.getOption("greet").equals("0")) {
			greet = true;
		}
		if (Starter.getOption("quotes").equals("0")) {
			quotes = true;
		}
		if (Starter.getOption("verbose").equals("0")) {
			verbose = true;
		}
	}
	
	
	public void onMessage(String channel, String sender,
            String login, String hostname, String message) {
		activepeople+=sender;
		System.out.println(sender+": "+message);
		if (quotes&&!message.startsWith("!")) {
		if(verbose)
		System.out.println("Saving Quote...");
		saveQuote(sender, message);
		if(verbose)
		System.out.println("Quote saved.");
		
		}
		if (message.equals("!shutdown")&&sender.equalsIgnoreCase("marenthyu")) {
			if (!Starter.getOption("silent").equals("0"))
				sendMessage(channel, "Shutting down!");
			System.exit(0);
		}
		
		Boolean cmdallowed = false;
		
		
		
		
		for (int i=0;i<commands.length;i++) {
			if (message.startsWith(commands[i][0])) {
				System.out.println("Command Recognised: "+commands[i][0]);
				switch(commands[i][1]) {
				
				//modOnly
				case "1": {
					
						try {
							if (ismod(sender)&&(getFunds(sender)>=Integer.parseInt(commands[i][3]))) {
								addFunds(sender,Integer.parseInt(commands[i][3])*-1);
							cmdallowed = true;
							} else sendMessage(currChan,"Sorry, "+sender+", but you are either no mod or don't have enough Funds to use this command. It costs "+commands[i][3]);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				break;}
				
				//not modOnly
				case "0": {
					//System.out.println("User doesn't have to be OP to use the command.");
					
					try {
						if ((getFunds(sender)>=Integer.parseInt(commands[i][3]))) {
							addFunds(sender,Integer.parseInt(commands[i][3])*-1);
						cmdallowed = true;
						} else sendMessage(currChan,"Sorry, "+sender+", but you don't have enough Funds to use this command. It costs "+commands[i][3]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;}
				
				}
				if (cmdallowed) {
					//System.out.println("Command allowed. Processing type.");
				switch(commands[i][2]) {
				case "say": {
					
					sendMessage(currChan,commands[i][4].replace("<sender>", sender));
				break;}
				
				case "method": {
					
					java.lang.reflect.Method method = null;
					try {
						System.out.println("Trying to call: "+commands[i][4]);
					  method = this.getClass().getMethod(commands[i][4], String.class, String.class);
					} catch (SecurityException e) {
					  e.printStackTrace();
					} catch (NoSuchMethodException e) {
					  e.printStackTrace();
					}
					try {
						  method.invoke(this, sender, message.replace(commands[i][0]+" ", ""));
						} catch (Exception e) {
							e.printStackTrace();
						} 
					
				break;}
				}}
				
			}
		}
		
		
		us = getUsers(currChan);
	}
	
	private boolean ismod(String sender) {
		if(mods.indexOf(sender)>=0) {
			return true;
		} else return false;
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		System.out.println("Got Private Message from "+sender+": "+message);
	}
	
	protected void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
		System.out.println(recipient+" just got opped!");
	}
	
	
	

	protected void onUnknown(String line){
		System.out.println("Unknown line received: "+line);
	}
	
	public void addMod (String Name, String s2) {
		onUserMode("", "", "", "", currChan+" +o "+Name);
	}
	
	protected void onJoin(String channel, String sender, String login, String hostname) {
		if (time>120) {
		System.out.println(sender+" joined the channel.");
		if (greet) {
		sendMessage(currChan, randomGreeting(sender));
		
		}}
		
	}
	
	public String randomGreeting(String sender) {
		String ret = "";
		
		int random = (int) (Math.random()*10);
		
		switch (random) {
		
		case 0: return "Hello "+sender+", sir";
		case 1: return "Good day "+sender+", sir";
		case 2: return "How's your day "+sender+"?";
		case 3: return "Feel Hugged "+sender;
		case 4: return "Welcome to the Stream "+sender+", sir";
		case 5: return "Enjoy your stay "+sender+", sir";
		case 6: return sender+" joined, give a warm hello!";
		case 7: return "Hello "+sender+", how has your day been so far? ";
		case 8: return "Sup "+sender+"";
		case 9: return sender+" joined - Hello, sir! ";
		case 10: return "Feeling well, "+sender+"? Enjoy your stay!";
		default: return "Hello "+sender+", sir";
		
		
		
		}
		
	
		
	}
	
	public void toggleGreet(String s, String s2) {
		greet = !greet;
		if (greet) {
			Starter.setOption("greet","0");
			sendMessage(currChan,"Greetings enabled.");
		} else {
			Starter.setOption("greet","1");
			sendMessage(currChan,"Greetings disabled.");
		}
		
		
	}
	public void toggleQuotes(String s, String s2) {
		quotes = !quotes;
		if (quotes) {
			Starter.setOption("quotes","0");
			sendMessage(currChan,"Quotes enabled.");
		} else {
			Starter.setOption("greet","1");
			sendMessage(currChan,"Quotes disabled.");
		}
		
		
	}
	
	protected void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		
		//mode includes: #marenthyu +o marenthyu
//		System.out.println("currChan: "+currChan);
		mode = mode.replace(currChan+" ", "");
//		System.out.println("After mode.replace: "+mode);
		String[] modeparts = mode.split(" ");
//		System.out.println("Modeparts[0]: "+modeparts[0]+", Modeparts[1]: "+modeparts[1]);
		switch(modeparts[0]) {
		
		case "+o": {
			System.out.println("Modding "+modeparts[1]);
			mods = mods+"#"+modeparts[1];
			
			break;
		}
		case "-o": {
			System.out.println("Unmodding "+modeparts[1]);
			mods = mods.replace("#"+modeparts[1], "");
			
			break;
		}
		default: {break;}
		
		}
		
		
	}
	
	public void shutdown() {
		if (!Starter.getOption("silent").equals("0"))
		sendMessage(currChan,"MarenBot shutting down!");
		System.exit(0);
	}
	
	//commands structure: 0 = command, 1 = mod only? (0/1), 2 = type (say/method), 3 = Stuff (method Name or thing to say)
	
	public void refreshCommands() throws Exception {
		new File("commands.txt").createNewFile();
			
		
		
		commands = new String[countLines("commands.txt")][5];
	
		InputStream    fis;
		BufferedReader br;
		String         line;

		fis = new FileInputStream("commands.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = 0;
		while (((line = br.readLine()) != null)&&!line.equals(System.getProperty("line.separator"))) {
		
		    String[] parts = line.split(Character.toString('#'));
		
		    for (int u=0;u<5;u++)
		    commands[i][u] = parts[u];
		    i++;
		}

		// Done with the file
		br.close();
		br = null;
		fis = null;
}
	

	public static void refreshAccounts() throws Exception {
	
			accounts = new String[countLines("accounts.txt")][2];
		
			InputStream    fis;
			BufferedReader br;
			String         line;
	
			fis = new FileInputStream("accounts.txt");
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			int i = 0;
			while ((line = br.readLine()) != null) {
				
			    String[] parts = line.split(":");
			    accounts[i][0] = parts[0];
			    accounts[i][1] = parts[1];
			    i++;
			}
	
			// Done with the file
			br.close();
			br = null;
			fis = null;
	}

	public void deleteCommand(String command) throws Exception {
		System.out.println("Deleting command "+command);
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		

		fis = new FileInputStream("commands.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = -1;
		String tem = "";
		
		while ((line = br.readLine()) != null) {
//			System.out.println("Current line: "+line);
			String[] parts = line.split("#");
			i++;
			if (parts[0].equalsIgnoreCase(command)) {
//				System.out.println("i = "+i);
				tem = commands[i][0]+"#"+commands[i][1]+"#"+commands[i][2]+"#"+commands[i][3]+"#"+commands[i][4];
				break;
			}
			
		}
		

		// Done with the file
		br.close();
		br = null;
		fis = null;
		String content = new String(Files.readAllBytes(path), charset);
//		System.out.println("tem: "+tem);
		content = content.replaceAll(System.getProperty("line.separator")+tem, "");
//		System.out.println("Writing to file: "+content);
		Files.write(path, content.getBytes(charset));
		refreshCommands();
	}
	
	public void addCommand(String command, String modOnly, String type,int cost, String Stuff) {
		System.out.println("Adding command: "+command);
		try {
			
			out2.append(command+"#"+modOnly+"#"+type+"#"+cost+"#"+Stuff+System.getProperty("line.separator"));
			out2.flush();
			refreshCommands();
		} catch (Exception e) {
			System.out.println("Error Adding command; stacktrace:");
			e.printStackTrace();
		}
		
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

	public void test(String testing) {
		sendMessage(currChan,testing);
		
		
	}
	
	
	
	
	
	//from old Bot version
	
	
	public boolean exists(String Name) throws Exception {
//		System.out.println("Testing if user exists....");
		boolean ret =false;
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		

		fis = new FileInputStream("accounts.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

		while ((line = br.readLine()) != null) {
			String[] parts = line.split(":");
			if (parts[0].equalsIgnoreCase(Name)) {
				ret=true;
				break;
			}
			
		}
		

		// Done with the file
		br.close();
		br = null;
		fis = null;
//		System.out.println("Result: "+ret);
		return ret;
	}
	
	public static void addFunds(String Name, int amount) throws Exception {
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		

		fis = new FileInputStream("accounts.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = -1;
		int temp=0;
		Name = Name.toLowerCase();
//		System.out.println("Name: "+Name);
		while ((line = br.readLine()) != null) {
//			System.out.println("Current line: "+line);
			String[] parts = line.split(":");
			i++;
			if (parts[0].equalsIgnoreCase(Name)) {
//				System.out.println("i = "+i);
				temp = Integer.parseInt(accounts[i][1]);
//				System.out.println("Old Amount: "+accounts[i][1]);
				accounts[i][1] = Integer.toString((Integer.parseInt(accounts[i][1]))+amount);
				break;
			}
			
		}
		

		// Done with the file
		br.close();
		br = null;
		fis = null;
		String content = new String(Files.readAllBytes(path2), charset);
//		System.out.println("New Amount: "+accounts[i][1]);
		content = content.replaceAll(Name+":"+temp, Name+":"+accounts[i][1]);
		Files.write(path2, content.getBytes(charset));
		refreshAccounts();
		if (verbose)
		System.out.println("Added "+amount+" to "+Name+"'s Account. New Balance: "+accounts[i][1]);
		
	}
	public void deleteAccount(String Name) throws Exception {
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		

		fis = new FileInputStream("accounts.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = -1;
		int temp=0;
		Name = Name.toLowerCase();
//		System.out.println("Name: "+Name);
		while ((line = br.readLine()) != null) {
//			System.out.println("Current line: "+line);
			String[] parts = line.split(":");
			i++;
			if (parts[0].equalsIgnoreCase(Name)) {
//				System.out.println("i = "+i);
				temp = Integer.parseInt(accounts[i][1]);
				break;
			}
			
		}
		

		// Done with the file
		br.close();
		br = null;
		fis = null;
		String content = new String(Files.readAllBytes(path2), charset);
		content = content.replaceAll(Name+":"+temp+System.getProperty("line.separator"), "");
		Files.write(path2, content.getBytes(charset));
		refreshAccounts();
		System.out.println("Deleted "+Name+"'s Account.");
		
		
	}
	public int getFunds(String Name) throws Exception {
		int ret=-1;
		
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		
		Name = Name.toLowerCase();
		
		fis = new FileInputStream("accounts.txt");
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

		while ((line = br.readLine()) != null) {
			String[] parts = line.split(":");
			if (parts[0].equalsIgnoreCase(Name)) {
				ret=Integer.parseInt(parts[1]);
				break;
			}
			
		}
		

		// Done with the file
		br.close();
		br = null;
		fis = null;
		if (ret==-1) {
			openAccount(Name);
			ret = 0;
		}
		return ret;
	}
	public void openAccount(String Name) {
		Name = Name.toLowerCase();
		System.out.println("Opening Account for "+Name);
		try {
			deleteAccount("testaccount");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			out3.append(Name+":"+"0"+System.getProperty("line.separator"));
			out3.flush();
			refreshAccounts();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		try {
			out3.append("testaccount:"+Integer.MAX_VALUE+System.getProperty("line.separator"));
			out3.flush();
			refreshAccounts();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	public String randomSlot() {
		String ret = "";
		int i = 0;
		i = (int) (Math.random()*10+1);
		switch (i) {
		case 1: {
			ret = "Kappa";
			break;
		}
		case 2: {
			ret = "DatSheffy";
			break;
		}
		case 3: {
			ret = "FrankerZ";
			break;
		}
		case 4:{
			ret="Kreygasm";
			break;
		}
		case 5:{
			ret="Shazam";
			break;
		}
		case 6:{
			ret="SSSsss";
			break;
		}
		case 7:{
			ret="RalpherZ";
			break;
		}
		case 8:{
			ret="PogChamp";
			break;
		}
		case 9: {
			ret = "Kappa";
			break;
		}
		case 10: {
			ret = "Kappa";
			break;
		}
		}
		return ret;
	}
	
	
	
	//partially copied.
	
	public void slots(String sender, String amountString) throws Exception {
		if (slots) {
		if (!exists(sender)) openAccount(sender);
		if ((getFunds(sender)>=Integer.parseInt(amountString))&&Integer.parseInt(amountString)>0) {
		
		addFunds(sender,Integer.parseInt(amountString)*-1);
		String slot1 = randomSlot();
		String slot2 = randomSlot();
		String slot3 = randomSlot();
		int points = 0;
		int matches = 0;
		if(slot1.equals(slot2)) matches++;
		if(slot1.equals(slot3)) matches++;
		if(slot2.equals(slot3)) matches++;
		
		points+=matches;
		
		if(slot1.equals("Kappa")) points++;
		if(slot2.equals("Kappa")) points++;
		if(slot3.equals("Kappa")) points++;
		
		int win = 0;
		
		win = points*Integer.parseInt(amountString);
		
		switch (points) {
		case 1: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - 1 point. At least, "+sender+", you won your bet back.");
			break;
		}
		case 2: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - 2 points! Lucky :) "+sender+", You get "+win);
			break;
		}
		case 3: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - 3 points, WOOT,"+sender+"! - You get "+win);
			break;
		}
		case 4: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - 4 points?! Nice! - but isn't this impossible? Tell Marenthyu please.");
			break;
		}
		case 5: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - 5 points?! Nice! - but isn't this impossible? Tell Marenthyu please.");
			break;
		}
		case 6: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - Ultimate Kappa Hype! CONGRATULATIONS,"+sender+"! - You get "+win);
			break;
		}
		case 0: {
			sendMessage(currChan,"| "+slot1+" | "+slot2+" | "+slot3+" | - Sorry,"+sender+", but you lost. No Matches and not a single Kappa");
			break;
		}
		}
		//TODO something wrong here
		addFunds(sender, win);}
		
	}
	}
	

	//new Written, not copied
	
	
	
	
	public void tF(String sender, String Name) {
		try {
			int balance = getFunds(Name);
			sendMessage(currChan,Name+"'s Funds: "+balance);
		} catch (Exception e) {
			sendMessage(currChan, "ERROR. Can't get the Balance of "+Name+". Usage: \"!getfunds NAME\" or \"!myfunds\"");
			e.printStackTrace();
		}
	}
	public void mF(String sender, String empty) {
		tF(sender,sender);
	}

	public void aF(String sender, String input) {
	try {
		String[] parts = input.split(" ");
		int amount = Integer.parseInt(parts[1]);
		addFunds(parts[0],amount);
		sendMessage(currChan,"Successfully added "+amount+" to "+parts[0]+"'s Account. New Balance: "+(this.getFunds(parts[0])));
	} catch (Exception e) {
		sendMessage(currChan,"Error while adding Funds. Usage: !addbucks NAME AMOUNT");
		e.printStackTrace();
	}
	
	}
	
	public void dA(String sender, String input) {
	try {
		String[] parts = input.split(" ");
		deleteAccount(parts[0]);
		sendMessage(currChan,"Successfully removed "+parts[0]+"'s Account.");
	} catch (Exception e) {
		sendMessage(currChan,"Error while deleting account. Usage: !delete NAME");
		e.printStackTrace();
	}
	
	}

	public void sQ(String sender, String name) {
	//	System.out.println("sQ called with "+name);
		if (name.equals("")) showQuote(sender);
		else showQuote(name);
	}
	
	public void pM(String sender, String otherargs)  {
		
		String parts[] = otherargs.split(" ");
		int amount=0;
		String receiver = parts[0];
		try {
		amount = Integer.parseInt(parts[1]);
				} catch(Exception e) {
					sendMessage(currChan, "Usage: !command RECEIVER AMOUNT");
				}
		try {
			if (amount>getFunds(sender)) {
				sendMessage(currChan, "You do not have enough Funds. Please try again.");
			} else {
				addFunds(sender, -amount);
				addFunds(receiver, amount);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void bG(String sender, String otherargs) {
		if (!betsopen) {
			sendMessage(currChan, "Bets are not open! Please wait for a Moderator to open them!");
			return;
		}
		String[] args = otherargs.split(" ");
		
		switch (args[0].toLowerCase()) {
		case "win": {
			System.out.println("peoplewonindex: "+peoplewon.indexOf(sender)+"; peoplelost index:"+peoplelost.indexOf(sender));
			if ((peoplewon.indexOf(sender)!=-1)||(peoplelost.indexOf(sender)!=-1))
				sendMessage(currChan,sender+", you have already bet! Try again next Game!");
			else {peoplewon+=sender+"#"; sendMessage(currChan,sender+", you succesfully bet for winning!");}
			break;
		}
		
		case "lose": {
			System.out.println("peoplewonindex: "+peoplewon.indexOf(sender)+"; peoplelost index:"+peoplelost.indexOf(sender));
			if ((peoplewon.indexOf(sender)!=-1)||(peoplelost.indexOf(sender)!=-1))
				sendMessage(currChan,sender+", you have already bet! Try again next Game!");

				
				else 	{peoplelost+=sender+"#"; sendMessage(currChan,sender+", you succesfully bet for losing!");}
			break;
		}
		
		default: {
			sendMessage(currChan, "Please try betting again: \"!bet win\" or \"!bet lose\"");
		}
		
		}
		
	}
	
	public void wG(String sender, String otherargs) {
		if (peoplewon.equals("")&&peoplelost.equals("")) {
			sendMessage(currChan, "Noone bet yet!");
			return;
		}
		String[] won = peoplewon.split("#");
		String[] lost = peoplelost.split("#");
		String congrats = "Congrats to ";
		
		for (String s:won) {
			try {
				congrats+=s+", ";
				addFunds(s, 500);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		for (String s:lost) {
			try {
				addFunds(s, 100);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		congrats+="you have won 500 Funds for choosing correctly! Everyone else that bet got 100.";
		sendMessage(currChan, congrats);
		peoplewon="";
		peoplelost="";
		
	}
	
	public void lG(String sender, String otherargs) {
		if (peoplewon.equals("")&&peoplelost.equals("")) {
			sendMessage(currChan, "Noone bet yet!");
			return;
		}
		String[] lost = peoplewon.split("#");
		String[] won = peoplelost.split("#");
		String congrats = "Congrats to ";
		
		for (String s:won) {
			try {
				congrats+=s+", ";
				addFunds(s, 500);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		for (String s:lost) {
			try {
				addFunds(s, 100);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		congrats+="you have won 500 Funds for choosing correctly! Everyone else that bet got 100.";
		sendMessage(currChan, congrats);
		peoplewon="";
		peoplelost="";
	}
	
	public void tB(String sender, String otherargs) {
		String[] args = otherargs.split(" ");
		try {
		switch (args[0]) {
		case "true" : {
			betsopen = true;
			sendMessage(currChan,"Bets are now open! Use !bet win/lose to bet on how the next Game will go!");
			break;
		}
		
		case "false" : {
			betsopen=false;
			sendMessage(currChan,"Bets are now closed!");
			break;
		}
		default : {
			betsopen=!betsopen;
			if (betsopen) {
				sendMessage(currChan,"Bets are now open! Use !bet win/lose to bet on how the next Game will go!");
				
			} else {
				sendMessage(currChan,"Bets are now closed!");
			}
			}
		}} catch(NullPointerException e) {
			betsopen=!betsopen;
			if (betsopen) {
				sendMessage(currChan,"Bets are now open! Use !bet win/lose to bet on how the next Game will go!");
				
			} else {
				sendMessage(currChan,"Bets are now closed!");
			}
		}
	}
	
	public boolean setQuotePath(String name) {
		String temp = path2.toAbsolutePath().toString().replace("accounts.txt", "")+"quotes\\";
		if (!new File(temp).exists()) new File(temp).mkdir();
		quotespath = Paths.get(temp+name+".txt").toAbsolutePath();
		boolean ret = new File(quotespath.toString()).exists();
		return ret;
		
	}
	
	public void saveQuote(String name, String quote) {
		if (!setQuotePath(name))
			try {
				//System.out.println("Trying to create "+quotespath.toString());
				new File(quotespath.toString()).createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		try {
			outq = new BufferedWriter(new FileWriter(quotespath.toString(),true));
			outq.append(quote+System.getProperty("line.separator"));
			outq.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void showQuote(String name) {
		//System.out.println("showQuote called");
		name = name.toLowerCase();
		 if (setQuotePath(name)) {
		//	 System.out.println("if true");
		int max = 0;
		try {
			max = countLines(quotespath.toString());
			//System.out.println("max set to "+max);
		} catch (IOException e) {
	
			e.printStackTrace();
		}
		
		
		int random = (int) ((Math.random()*(max-1))+1);
		//System.out.println("random: "+random);
		
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		
        try {
		fis = new FileInputStream(quotespath.toString());
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		int i = 1;
		while ((line = br.readLine()) != null) {
			if (i==random) {
				sendMessage(currChan, name+" once said: "+line );
				break;
			}
			i++;
			
		}
		

		// Done with the file
		br.close();
		br = null;
		fis = null; } catch(Exception e) {
			e.printStackTrace();
		}} else {
			//System.out.println("if false");
			sendMessage(currChan, "Either that person didn't speak yet or you didn't provide a Name!");
		}
		
	}
	

	public void addTimedMessage(String message, int interval) {
		int highest = 0;
		for (int i = 0; i<commands.length;i++) {
			if (commands[i][0].startsWith("!TIMER")) {
				if (highest<Integer.parseInt(commands[i][0].replace("!TIMER", ""))) {
					highest = Integer.parseInt(commands[i][0].replace("!TIMER", ""));
				}
			}
		}
		
		this.addCommand("!TIMER"+(highest+1), "1", "say", interval, message);
		
		
	}
	
	public void runTimedMessages() throws Exception {
		if (verbose)
		System.out.println("Current Time: "+time);
		for (int i = 0; i<commands.length;i++) {
			if (commands[i][0].startsWith("!TIMER")) {
				if(verbose)
				System.out.println("modulo: "+time%Integer.parseInt(commands[i][3]));
				
				if(time%Integer.parseInt(commands[i][3])==0) {
				onMessage(currChan, "testaccount", "testaccount", "", commands[i][0]);
				addFunds("testaccount", Integer.parseInt(commands[i][3]));
				}
			}
		}
		
	}

}