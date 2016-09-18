package spellcheck;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import wordhelper.SpellChecker;

public class SpellCheckHelper {
	
	private final SpellChecker mSpellChecker;
	private final SpellCheckPanel mSpellCheckPanel;
	private final ConfigurationPanel mConfigurationPanel;
	
	{
		mSpellChecker = new SpellChecker();
		mSpellCheckPanel = new SpellCheckPanel(mSpellChecker);
		mConfigurationPanel = new ConfigurationPanel(mSpellChecker);
	}
	
	public JPanel getConfigurePanel() {
		return mConfigurationPanel;
	}
	
	public JPanel getSpellCheckPanel() {
		if(!mSpellChecker.hasWordList() || !mSpellChecker.hasKeyboard()) {
			JOptionPane.showMessageDialog(null, "Please configure the spellchecker.");
			return mConfigurationPanel;
		}
		return mSpellCheckPanel;
	}
	
}
