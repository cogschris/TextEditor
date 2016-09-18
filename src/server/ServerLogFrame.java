package server;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerLogFrame extends JFrame implements Log{
private static final long serialVersionUID = 3628430017326735514L;
	
	private JButton mToggleButton = null;
	private final JTextArea mTextArea;
	
	{
		setTitle("Server");
		setSize(640,480);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		mTextArea = new JTextArea(){
			private static final long serialVersionUID = -966600308583399692L;

			@Override
			public void append(String str) {
				super.append(str+"\n");
			}
		};
		mTextArea.setLineWrap(true);
		mTextArea.setWrapStyleWord(true);
		JScrollPane jsp = new JScrollPane(mTextArea);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(jsp);
	}
	
	public ServerLogFrame(JButton inToggleButton) {
		mToggleButton = inToggleButton;
		add(mToggleButton,"South");
	}
	
	public void log(String string) {
		if(string == null) return;
		mTextArea.append(string);
	}
}
