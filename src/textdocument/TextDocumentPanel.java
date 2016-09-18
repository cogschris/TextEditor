package textdocument;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import client.Client;
import client.ClientThread;
import customui.OfficePanel;
import customui.OfficeScrollBarUI;
import customui.ThemeColors;
import library.ImageLibrary;
import server.Commands;
import server.Commands.Command;
import spellcheck.SpellCheckHelper;
import spellcheck.SpellCheckPanel;

public class TextDocumentPanel extends OfficePanel {

	private static final long serialVersionUID = -3927634294406617454L;
	
	private final JScrollPane mScrollPane;
	private final JTextPane mTextPane;
	private File mFile;
	private boolean isOnline;
	private String owner;
	
	private final TextDocumentHistoryHelper mTextDocumentHistoryHelper;
	private final JMenu mEditMenu;
	private final JMenuItem mUndoItem;
	private final JMenuItem mRedoItem;
	private final JMenuItem mCutItem;
	private final JMenuItem mCopyItem;
	private final JMenuItem mPasteItem;
	private final JMenuItem mSelectAllItem;
	
	private final SpellCheckHelper mSpellCheckHelper;
	private final JMenu mSpellCheckMenu;
	private final JMenuItem mRunItem;
	private final JMenuItem mConfigureItem;
	
	private final JMenu mUsers;
	private final JMenuItem addUser;
	private final JMenuItem removeUser;
	private ClientThread ct;
	
	
	{
		setLayout(new BorderLayout());
		mScrollPane = new JScrollPane();
		mScrollPane.getVerticalScrollBar().setUI(new OfficeScrollBarUI());
		mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mTextPane = new JTextPane();
		mTextPane.setSelectionColor(ThemeColors.MainColor);
		mScrollPane.getViewport().add(mTextPane);
		add(mScrollPane,"Center");
		
		mTextDocumentHistoryHelper = new TextDocumentHistoryHelper(mTextPane.getDocument());
		
		mEditMenu = new JMenu("Edit");
		mEditMenu.setMnemonic('E');
		
		mUndoItem = mTextDocumentHistoryHelper.getUndoMenuItem();
		mRedoItem = mTextDocumentHistoryHelper.getRedoMenuItem();
		
		mCutItem = new JMenuItem("Cut");
		mCutItem.setMnemonic('C');
		mCutItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/cut.png")));
		mCutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
		mCutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.cut();
			}
		});
		
		mCopyItem = new JMenuItem("Copy");
		mCopyItem.setMnemonic('C');
		mCopyItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/copy.png")));
		mCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
		mCopyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.copy();
			}
		});
		
		mPasteItem = new JMenuItem("Paste");
		mPasteItem.setMnemonic('P');
		mPasteItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/paste.png")));
		mPasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
		mPasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.paste();
			}
		});
		
		mSelectAllItem = new JMenuItem("Select All");
		mSelectAllItem.setMnemonic('S');
		mSelectAllItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/select.png")));
		mSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
		mSelectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.selectAll();
			}
		});
		
		mEditMenu.add(mUndoItem);
		mEditMenu.add(mRedoItem);
		mEditMenu.add(new JSeparator());
		mEditMenu.add(mCutItem);
		mEditMenu.add(mCopyItem);
		mEditMenu.add(mPasteItem);
		mEditMenu.add(new JSeparator());
		mEditMenu.add(mSelectAllItem);
		
		mSpellCheckHelper = new SpellCheckHelper();
		mSpellCheckMenu = new JMenu("SpellCheck");
		mSpellCheckMenu.setMnemonic('S');
		
		mRunItem = new JMenuItem("Run");
		mRunItem.setMnemonic('R');
		mRunItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/run.png")));
		mRunItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		mRunItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel spellCheckPanel = mSpellCheckHelper.getSpellCheckPanel();
				TextDocumentPanel.this.add(spellCheckPanel,"East");
				if(spellCheckPanel instanceof SpellCheckPanel)
					((SpellCheckPanel) spellCheckPanel).runSpellCheck(mTextPane);
				revalidate();
				repaint();
			}
		});
		
		mConfigureItem = new JMenuItem("Configure");
		mConfigureItem.setMnemonic('C');
		mConfigureItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/configure.png")));
		mConfigureItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel.this.add(mSpellCheckHelper.getConfigurePanel(),"East");
				revalidate();
				repaint();
			}
		});
		
		mUsers = new JMenu("Users");
		addUser = new JMenuItem("Add");
		addUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				//
				panel.add(new JLabel("Add User:"));
				JTextField text = new JTextField(20);
				panel.add(text);
				JPanel panel2 = new JPanel();
				JButton add = new JButton("Ok");
				JButton cancel = new JButton("Cancel");
				panel2.add(add); panel2.add(cancel);
				panel.add(panel2);
				JDialog jd = new JDialog();
				jd.setBounds(200, 300, 400, 200);
				jd.add(panel);
				jd.setVisible(true);
				cancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						jd.setVisible(false);
					}
				});
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (text.getText().isEmpty()) {
							return;
						}
						try {
							Client.get().writeln(Commands.buildCommand(Command.AddUser, text.getText(), mFile.getName()));
							Client.get().flush();
							jd.setVisible(false);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
			}
		});
		removeUser = new JMenuItem("Remove");
		removeUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Client.get().writeln(Commands.buildCommand(Command.GetSharedUsers, mFile.getName()));
					Client.get().flush();
					System.out.println("waiting");
					String[] users = Client.get().read().split(" ");
					JPanel blah= new JPanel();
					System.out.println("crash");
					blah.setLayout(new BoxLayout(blah, BoxLayout.PAGE_AXIS));
					blah.add(new JLabel("Select a user: "));
					JList<String> list = new JList<String>(users);
					blah.add(list);
					JPanel but = new JPanel();
					JButton ok2 = new JButton("Remove");
					JButton cancel = new JButton("Cancel");
					but.add(ok2); but.add(cancel);
					JDialog jd = new JDialog();
					jd.setVisible(true);
					
					blah.add(but);
					jd.add(blah);
					jd.setBounds(200, 300, 300, 300);
					ok2.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								//System.out.println((String)list.getSelectedValue());
								Client.get().writeln(Commands.buildCommand(Command.RemoveUser, (String)list.getSelectedValue(), mFile.getName()));
								Client.get().flush();
								jd.setVisible(false);
							} catch (IOException e) {
								// TOdDO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mUsers.add(addUser);mUsers.add(removeUser);
		mSpellCheckMenu.add(mRunItem);
		mSpellCheckMenu.add(mConfigureItem);
		
	}
	
	public TextDocumentPanel(){}
	public TextDocumentPanel(File inFile, String owner) throws IOException {
		mFile = inFile;
		FileReader fr = new FileReader(inFile);
		mTextPane.read(fr, "");
		fr.close();
		//this.owner = owner;
		if (owner!= null) {
			this.owner = owner;
			isOnline = true;
		}
		else {
			this.owner = Client.get().getUser();
		}
	}
	
	public JMenu getEditMenu() {
		return mEditMenu;
	}
	
	public JMenu getSpellCheckMenu() {
		return mSpellCheckMenu;
	}
	
	public JMenu getUser() {
		return mUsers;
	}
	
	public File getFile() {
		return mFile;
	}
	public void save(File inFile) throws IOException {
		mFile = inFile;
		FileWriter fw = new FileWriter(mFile);
		fw.write(mTextPane.getText());
		fw.close();
	}
	
	public void setClient() {
		ct = new ClientThread(this, mFile.getName(), getText());
	}
	
	public String getText() {
		return mTextPane.getText();
	}
	
	public void setText(String text) {
		mTextPane.setText(text);
	}
	
}
