package package1;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Container c;
	Button shutdown = new Button("Shutdown"),  addcom = new Button("Add new command"), removecmd = new Button("Remove Command");
	JTextArea output;
	
	List commandlist;
	Bot bot;
	JTextField command, modOnly, type, Stuff;
	
	
	public GUI(Bot bots) {
		
		setSize(1000,460);
        setTitle("MarenBot");
        c=getContentPane();
        setLayout(null);
        
        output = new JTextArea();
        output.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(output);
        removecmd.setBounds(825, 110, 155, 90);
        addcom.setBounds(825, 215, 155, 90);
        shutdown.setBounds(825, 320, 155, 90);
        
        bot=bots;
        commandlist = new List(bot.commands.length, false);
        for (int i=0;i<bot.commands.length;i++) {
        	commandlist.add(bot.commands[i][0]+" "+bot.commands[i][1]+" "+bot.commands[i][2]+" "+bot.commands[i][3]);
        }
      
        
        System.out.println("Redirecting Console Output to TextArea");
        PrintStream con = new PrintStream(new TextAreaOutputStream(output));
        System.setOut(con);
        
        scrollPane.setBounds(10, 10, 400, 400);
        
        commandlist.setBounds(420, 10, 400, 400);
        
        removecmd.addActionListener(this);
        shutdown.addActionListener(this);
        addcom.addActionListener(this);
        
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        c.add(removecmd);
        c.add(scrollPane);
        c.add(addcom);
        c.add(shutdown);
        c.add(commandlist);

        
        c.setBackground(Color.getHSBColor(26, 68, 66));
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==shutdown) {
			bot.shutdown();
		}
		if (e.getSource()==addcom) {
			String modonly = "0";
			String saymeth,stuff;
			Object[] options1 = {"Say something",
                    "Execute Method (unfinished function)",};
			String newcommand = JOptionPane.showInputDialog("Enter Command");
			if(JOptionPane.showConfirmDialog(null, "Shall it only be for mods?", "Please select", JOptionPane.YES_NO_OPTION)==1) {
				modonly = "0";
			} else {
				modonly = "1";
			}
			if (JOptionPane.showOptionDialog(null,
	                 "Say something or call a function?",
	                 "What shall it do?",
	                 JOptionPane.YES_NO_CANCEL_OPTION,
	                 JOptionPane.PLAIN_MESSAGE,
	                 null,
	                 options1,
	                 null)==1) {
				saymeth = "method";
				stuff = JOptionPane.showInputDialog("Which method shall it call?");
			} else {
				saymeth = "say";
				stuff = JOptionPane.showInputDialog("What shall it say? (to get the writer's name, use <sender>");
			}
			bot.addCommand(newcommand, modonly, saymeth, stuff);
			refreshCommands();
		}
		if (e.getSource()==removecmd) {
			try {
				bot.deleteCommand(bot.commands[commandlist.getSelectedIndex()][0]);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to remove command.");
			}
			this.refreshCommands();
			JOptionPane.showMessageDialog(null, "Command removed.");
		}
	}

	
	public void refreshCommands() {
		c.remove(commandlist);
		commandlist = new List(bot.commands.length, false);
		for (int i=0;i<bot.commands.length;i++) {
        	commandlist.add(bot.commands[i][0]+" "+bot.commands[i][1]+" "+bot.commands[i][2]+" "+bot.commands[i][3]);
        }
		commandlist.setBounds(420, 10, 400, 400);
		  try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		c.add(commandlist);
	}
}
