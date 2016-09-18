package spellcheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import customui.OfficeButton;
import customui.OfficePanel;
import fileChooser.SingleTypeFileChooser;
import wordhelper.SpellChecker;

public class ConfigurationPanel extends OfficePanel {
	private static final long serialVersionUID = 2796044961643727103L;

	private final SpellChecker mSpellChecker;
	private final JLabel mWlFileLabel;
	private final OfficeButton mWlFileChooserButton;
	private final JLabel mKbFileLabel;
	private final OfficeButton mKbFileChooserButton;
	private final OfficeButton mCloseButton;
	
	{
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBorder(new TitledBorder("Configure"));
		
		mWlFileLabel = new JLabel("N/A"){
			private static final long serialVersionUID = 1L;
			@Override
			public void setText(String text) {
				super.setText(".wl: " + text);
			}
		};
		mWlFileChooserButton = new OfficeButton("Select WordList...");
		mWlFileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTypeFileChooser wlChooser = new SingleTypeFileChooser("Word List", ".wl");
				wlChooser.setDialogTitle("Select Wordlist...");
				int returnValue = wlChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	File file = wlChooser.getSelectedFile();
		        	if(file.exists()) {
		        		mSpellChecker.loadWordList(file);
		        		updateFields();
		        	} else {
		        		JOptionPane.showMessageDialog(null,
		        			    file.getName() + " was not found!",
		        			    "File Not Found",
		        			    JOptionPane.ERROR_MESSAGE);
		        	}
		        }
			}
		});
		
		mKbFileLabel = new JLabel("N/A"){
			private static final long serialVersionUID = 1L;
			@Override
			public void setText(String text) {
				super.setText(".kb: " + text);
			}
		};
		mKbFileChooserButton = new OfficeButton("Select Keyboard...");
		mKbFileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTypeFileChooser wlChooser = new SingleTypeFileChooser("Keyboard", ".kb");
				wlChooser.setDialogTitle("Select Keyboard...");
				int returnValue = wlChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	File file = wlChooser.getSelectedFile();
		        	if(file.exists()) {
		        		mSpellChecker.loadKeyboard(file);
		        		updateFields();
		        	} else {
		        		JOptionPane.showMessageDialog(null,
		        			    file.getName() + " was not found!",
		        			    "File Not Found",
		        			    JOptionPane.ERROR_MESSAGE);
		        	}
		        }
			}
		});
		
		mCloseButton = new OfficeButton("Close");
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ConfigurationPanel.this.getParent() != null)
				ConfigurationPanel.this.getParent().remove(ConfigurationPanel.this);
			}
		});
		
		add(mWlFileLabel);
		add(mWlFileChooserButton);
		add(Box.createVerticalStrut(20));
		add(mKbFileLabel);
		add(mKbFileChooserButton);
		add(Box.createVerticalGlue());
		add(mCloseButton);
	}
	
	public ConfigurationPanel(SpellChecker inSpellChecker) {
		mSpellChecker = inSpellChecker;
		/*Not necessary for assignment, but loads files in another thread for better load speed*/
		new Thread(() -> {
			mSpellChecker.loadWordList(new File("src/wordlist.wl"));
			mSpellChecker.loadKeyboard(new File("src/qwerty-us.kb"));
			updateFields();
		}).start();
	}

	private void updateFields() {
		mWlFileLabel.setText(mSpellChecker.getFileByType(SpellChecker.WORDLIST).getName());
		mKbFileLabel.setText(mSpellChecker.getFileByType(SpellChecker.KEYBOARD).getName());
	}
	
}
