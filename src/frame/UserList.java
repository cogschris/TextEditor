package frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import textdocument.TextDocumentManager;

public class UserList extends JFrame{
	
	JList list;
	
	private TextDocumentManager tdm;
	public UserList(TextDocumentManager tdm) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		list = new JList();
		this.add(new JLabel("Choose a user:"));
		this.add(list);
		this.tdm = tdm;
		JPanel buttonz = new JPanel();
		JButton mine = new JButton("My Files...");
		mine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					tdm.openFileOnline();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		JButton user = new JButton("Select User...");
		setVisible(false);
		
	}
	
	public void populate(String[] names) {
		setVisible(true);
	}
}
