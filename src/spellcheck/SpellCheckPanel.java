package spellcheck;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.MatchResult;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import customui.OfficeButton;
import customui.OfficeComboBoxUI;
import customui.OfficePanel;
import wordhelper.SpellChecker;

public class SpellCheckPanel extends OfficePanel {
	private static final long serialVersionUID = 1203713931331563215L;

	private final SpellChecker mSpellChecker;
	private Scanner mScanner;
	private MatchResult mMatchResult;
	private JTextComponent mTextComponent;
	
	private final JLabel mSpellingLabel;
	private final OfficeButton mIgnoreButton;
	private final OfficeButton mAddButton;
	private final JComboBox<String> mChangeOptions;
	private final OfficeButton mChangeButton;
	private final OfficeButton mCloseButton;
	
	{ 
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Spell Check"));
		
		mSpellingLabel = new JLabel("N/A"){
			private static final long serialVersionUID = -438929501552222784L;
			{setFont(getFont().deriveFont(16.0f));}
			@Override
			public void setText(String text) {
				super.setText("Spelling: " + text);
			}
		};
		
		mIgnoreButton = new OfficeButton("Ignore");
		mIgnoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				next();
			}
		});
		
		mAddButton = new OfficeButton("Add");
		mAddButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mSpellChecker.addWordToDictionary(mTextComponent.getText().substring(mMatchResult.start(), mMatchResult.end()));
					next();
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(null,
						    "Word failed to save!\n Please check configurations.",
						    "File Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		mChangeOptions = new JComboBox<String>();
		mChangeOptions.setUI(new OfficeComboBoxUI());
		mChangeOptions.setMaximumRowCount(10);
		mChangeButton = new OfficeButton("Change");
		mChangeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				focusSpellingError();
				mTextComponent.setEditable(true);
				mTextComponent.replaceSelection(mChangeOptions.getSelectedItem().toString());
				mTextComponent.setEditable(false);
				next();
			}
		});
		
		mCloseButton = new OfficeButton("Close");
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		OfficePanel titlePanel = new OfficePanel();
		titlePanel.add(mSpellingLabel);
		
		OfficePanel addIgnorePanel = new OfficePanel();
		addIgnorePanel.add(mIgnoreButton);
		addIgnorePanel.add(mAddButton);
		
		OfficePanel changePanel = new OfficePanel();
		changePanel.add(mChangeOptions);
		changePanel.add(mChangeButton);
		
		OfficePanel optionsPanel = new OfficePanel();
		optionsPanel.setLayout(new BorderLayout());
		optionsPanel.add(addIgnorePanel, "North");
		optionsPanel.add(changePanel, "Center");
		
		OfficePanel footerPanel = new OfficePanel();
		footerPanel.setLayout(new BorderLayout());
		footerPanel.add(mCloseButton, "South");
		
		add(titlePanel);
		add(optionsPanel);
		add(Box.createVerticalGlue());
		add(footerPanel);
		
	}
	
	SpellCheckPanel(SpellChecker inSpellChecker) {
		mSpellChecker = inSpellChecker;
	}
	
	public void runSpellCheck(JTextComponent inTextComponent) {
		mTextComponent = inTextComponent;
		mTextComponent.setEditable(false);
		mScanner = new Scanner(mTextComponent.getText());
		mScanner.useDelimiter("([^A-Za-z])");
		next();
	}
	
	private void next() {
		String word = null;
		while(mScanner.hasNext() && !mSpellChecker.isSpellingError(word = mScanner.next())) word = null;
		if(word == null) {
			close();
			JOptionPane.showMessageDialog(null,"The SpellCheck is Complete.");
			return;
		}
		word = word.toLowerCase();
		mMatchResult = mScanner.match();
		mSpellingLabel.setText(word);
		mChangeOptions.removeAllItems();
		int i = 0;
		for(String suggestion : mSpellChecker.getSpellingSuggestions(word, 10)) {
			mChangeOptions.addItem(suggestion);
			if (++i == 10) break;
		}
		if(mChangeOptions.getSelectedIndex() == -1) mChangeButton.setEnabled(false);
		else mChangeButton.setEnabled(true);
		focusSpellingError();
	}
	
	private void focusSpellingError() {
		mTextComponent.requestFocus();
		mTextComponent.setCaretPosition(mMatchResult.start());
		mTextComponent.setSelectionStart(mMatchResult.start());
		mTextComponent.setSelectionEnd(mMatchResult.end());
	}
	
	private void close() {
		mTextComponent.setEditable(true);
		Container parent = getParent();
		if(parent != null) {
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
		}
	}

}
