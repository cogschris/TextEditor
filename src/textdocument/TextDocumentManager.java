package textdocument;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.Client;
import client.ClientThread;
import customui.FileDialog;
import customui.OfficeTabbedPane;
import fileChooser.SingleTypeFileChooser;
import library.ImageLibrary;
import server.Commands;
import server.Commands.Command;

public class TextDocumentManager extends OfficeTabbedPane {
	private static final long serialVersionUID = -4649936834531540925L;
	
	private final JMenuBar mMenuBar;
	private final JMenu mFileMenu;
	private final JMenuItem mNewItem;
	private final JMenuItem mOpenItem;
	private final JMenuItem mSaveItem;
	private final JMenuItem mCloseItem;
	
	{
		mFileMenu = new JMenu("File");
		mFileMenu.setMnemonic('F');
		
		mNewItem = new JMenuItem("New");
		mNewItem.setMnemonic('N');
		mNewItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/new.png")));
		mNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		mNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel toAdd = new TextDocumentPanel();
				TextDocumentManager.this.addTab("New",toAdd);
				TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount()-1);
			}
		});
		
		mOpenItem = new JMenuItem("Open");
		mOpenItem.setMnemonic('O');
		mOpenItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/open.png")));
		mOpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		mOpenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(Client.get().isAuthentic() && Client.get().isOnline()) {
					try {
						Client.get().writeln(Commands.buildCommand(Command.GetSharedtome ));
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
						JButton ok2 = new JButton("My Files...");
						JButton cancel = new JButton("Select User");
						but.add(ok2); but.add(cancel);
						JDialog jd = new JDialog();
						jd.setVisible(true);
						
						blah.add(but);
						jd.add(blah);
						jd.setBounds(200, 300, 300, 300);
						ok2.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								jd.setVisible(false);
								openStage();
								
							}
							
						});
						cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								String name = (String)list.getSelectedValue();
								try {
									Client.get().writeln(Commands.buildCommand(Command.GetSharedFiles, name ));
									Client.get().flush();
									
									String[] files = Client.get().read().split(" ");
									String selectedFileName = FileDialog.showDialog(files, "Open...", false);
									if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
									//Client.get().writeln(Commands.buildCommand(Command.OpenShareFile, selectedFileName, name));
									//Client.get().flush();
									openShareOnline(name, selectedFileName);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						});
						
					}
					catch(IOException e) {
						
					}
				}
			}
		});
		
		mSaveItem = new JMenuItem("Save");
		mSaveItem.setMnemonic('S');
		mSaveItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/save.png")));
		mSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		mSaveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel toSave = getActiveDocumentPanel();
				if(toSave == null) return;
				int choice = JOptionPane.NO_OPTION;
				if(Client.get().isAuthentic() && Client.get().isOnline()) {
					Object[] options = {"Online", "Offline"};
					choice = JOptionPane.showOptionDialog(null,
					"Where would you like to open the file?",
					"Save...",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
				}
				if(choice == JOptionPane.YES_OPTION) saveFileOnline();
				else saveFileOffline();
			}
		});
		
		mCloseItem = new JMenuItem("Close");
		mCloseItem.setMnemonic('C');
		mCloseItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/close.png")));
		mCloseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selected = TextDocumentManager.this.getSelectedIndex();
				if(selected == -1) return;
				TextDocumentManager.this.remove(selected);
			}
		});
		
		addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            refreshMenuBar();
	        }
	    });
		
		mFileMenu.add(mNewItem);
		mFileMenu.add(mOpenItem);
		mFileMenu.add(mSaveItem);
		mFileMenu.add(mCloseItem);
	}
	
	public TextDocumentManager(JMenuBar inMenuBar) {
		mMenuBar = inMenuBar;
		
		refreshMenuBar();
	}
	
	public TextDocumentPanel getActiveDocumentPanel() {
		int selected = getSelectedIndex();
		if(selected == -1) return null;
		Component toReturn = getComponentAt(selected);
		if (toReturn instanceof TextDocumentPanel) return (TextDocumentPanel)toReturn;
		else return null;
	}
	
	private void selectTabByFile(File file) {
		int index = -1;
		for(int i = 0; i < getComponentCount(); ++i) {
			Component atIndex = getComponentAt(i);
			if (atIndex instanceof TextDocumentPanel) {
				if(file.equals(((TextDocumentPanel)atIndex).getFile())) {
					index = i;
					break;
				}
			}
		}
		if(index != -1) setSelectedIndex(index);
	}
	
	
	private void openStage() {
		int choice = JOptionPane.NO_OPTION;
		Object[] options = {"Online", "Offline"};
		choice = JOptionPane.showOptionDialog(null,
		"Where would you like to open the file?",
		"Open...",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
	
		if(choice == JOptionPane.YES_OPTION)
			try { 
				openFileOnline(); }
			catch (IOException e) { }
		else openFileOffline();
	}
	private void setActiveTabText(String title) {
		int selected = getSelectedIndex();
		if(selected == -1) return;
		this.setTitleAt(selected, title);
	}
	
	public boolean isFileOpen(File file) {
		for(Component component : TextDocumentManager.this.getComponents()) {
    		if(component instanceof TextDocumentPanel) {
    			if(file.equals(((TextDocumentPanel)component).getFile())) {
    				return true;
    			}
    		}
    	}
		return false;
	}

	private void refreshMenuBar() {
		mMenuBar.removeAll();
		mMenuBar.add(mFileMenu);
		TextDocumentPanel activeDocument = getActiveDocumentPanel();
		if(activeDocument != null) {
			mMenuBar.add(activeDocument.getEditMenu());
			mMenuBar.add(activeDocument.getSpellCheckMenu());
			//if (online) {
			mMenuBar.add(activeDocument.getUser());
			
		}
	}
	
	public void openFileOffline() {
		SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
		txtChooser.setDialogTitle("Open File...");
		int returnValue = txtChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File file = txtChooser.getSelectedFile();
        	openFile(file, null, false);
        }
	}
	
	public void openFileOnline() throws IOException {
		Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
		Client.get().flush();
		String[] files = Client.get().read().split(" ");
		String selectedFileName = FileDialog.showDialog(files, "Open...", false);
		if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
		Client.get().writeln(Commands.buildCommand(Command.OpenUserFile, selectedFileName));
		Client.get().flush();
		File temp = new File("clientfiles/"+selectedFileName);
		Client.get().readFile(temp);
		openFile(temp, null, true);
		temp.delete();
	}
	public void openShareOnline(String user, String filename) throws IOException {
		//Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
		//Client.get().flush();
		//String[] files = Client.get().read().split(" ");
		//String selectedFileName = FileDialog.showDialog(files, "Open...", false);
		//if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
		//Client.get().writeln(Commands.buildCommand(Command.OpenUserFile, selectedFileName));
		//Client.get().flush();
		Client.get().writeln(Commands.buildCommand(Command.OpenShareFile, filename, user));
		Client.get().flush();
		File temp = new File("clientfiles/"+filename);
		Client.get().readFile(temp);
		openFile(temp, user, true);
		
		//temp.delete();
	}
	
	/*private void openOtherFiles() throws IOException {
		Client.get().writeln(Commands.buildCommand(Command.GetSharedFiles));
		Client.get().flush();
		String[] files = Client.get().read().split(" ");
		String selectedFileName = FileDialog.showDialog(files, "Open...", false);
		if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
		Client.get().writeln(Commands.buildCommand(Command.GetSharedFile, selectedFileName));
		Client.get().flush();
		File temp = new File("clientfiles/"+selectedFileName);
		Client.get().readFile(temp);
		//openFile(temp);
		temp.delete();
	}*/
	
	private void openFile(File file,String owner, boolean online) {
		if(!isFileOpen(file)) {
			try {
				TextDocumentPanel toAdd = new TextDocumentPanel(file, owner);
	        	TextDocumentManager.this.addTab(file.getName(),toAdd);
	        	TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount()-1);
	        	if (online) {
	        		new ClientThread(toAdd,file.getName(), toAdd.getText()).start();
	        	}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(
						null,
					    file.getName() + " failed to be read.",
					    "File Error",
					    JOptionPane.ERROR_MESSAGE);
			}
    	} else {
    		selectTabByFile(file);
    		JOptionPane.showMessageDialog(null, file.getName()+"is already open.");
    	}
	}
	
	private void saveFileOffline() {
		SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
		txtChooser.setDialogTitle("Save File...");
		int returnValue = txtChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File file = txtChooser.getSelectedFile();
        	boolean shouldSave = true;
        	if(file.exists()) {
        		int n = JOptionPane.showConfirmDialog(
        			    TextDocumentManager.this,
        			    file.getName() + " already exists\nDo you want to replace it?",
        			    "Confirm Save As",
        			    JOptionPane.YES_NO_OPTION,
        			    JOptionPane.WARNING_MESSAGE);
        		if(n != 0) shouldSave = false;
        	}
        	if(shouldSave) {
        		try {
					getActiveDocumentPanel().save(file);
					setActiveTabText(file.getName());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(
							null,
						    file.getName() + " failed to be saved.",
						    "Saving Error",
						    JOptionPane.ERROR_MESSAGE);
				}
        	}
        }
	}
	
	private void saveFileOnline() {
		try {
			Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
			Client.get().flush();
			String[] files = Client.get().read().split(" ");
			String selectedFileName = FileDialog.showDialog(files, "Save...", true);
			if(selectedFileName == null || !selectedFileName.endsWith(".txt") || selectedFileName.length() < 3) {
				JOptionPane.showMessageDialog(null, "File failed to save.", "", JOptionPane.ERROR_MESSAGE);
				}
			else {
				File temp = new File("clientfiles/"+selectedFileName);
				getActiveDocumentPanel().save(temp);
				setActiveTabText(temp.getName());
				Client.get().writeln(Commands.buildCommand(Command.SaveUserFile, selectedFileName));
				Client.get().flush();
				Client.get().writeFile(temp);
				temp.delete();
				JOptionPane.showMessageDialog(null, "File successfully saved.", "", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File failed to save.", "", JOptionPane.ERROR_MESSAGE);
		}
	}
}
