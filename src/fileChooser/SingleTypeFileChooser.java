package fileChooser;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SingleTypeFileChooser extends JFileChooser {
	private static final long serialVersionUID = 6911123903417346378L;
	
	public SingleTypeFileChooser(String inDescription, String inType) {
		setAcceptAllFileFilterUsed(false);
		setFileFilter(new FileNameExtensionFilter(inDescription, inType));
	}
}
