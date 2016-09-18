package frame;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.Client;
import customui.OfficeButton;
import customui.ThemeColors;
import library.ImageLibrary;
import server.Commands;
import server.Commands.Command;
import server.Encrypt;

public class LoginPanel extends JPanel {
	private static final long serialVersionUID = 10977325542021875L;
	
	private static final Image mBackgroundImage;
	private static final String mTitle = "Trojan Office";
	
	Navigator mNav;
	
	static {
		mBackgroundImage = ImageLibrary.getImage("img/backgrounds/darkgrey_panel.png");
	}
	
	private final JLabel mUsernameLabel;
	private final JTextField mUsernameField;
	private final JLabel mPasswordLabel;
	private final JPasswordField mPasswordField;
	private final OfficeButton mLoginButton;
	
	{
		setLayout(new GridBagLayout());
		mLoginButton = new OfficeButton("Login");
		mLoginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String userText = mUsernameField.getText();
				if(userText.isEmpty()) {return;};
				String passText = new String(mPasswordField.getPassword());
				if(passText.isEmpty()) {return;}

				try {
					if(Client.get().isOnline()) {
						String command = Commands.buildCommand(Command.Login, userText, Encrypt.MD5(passText));
						Client.get().writeln(command);
						Client.get().flush();
						System.out.println("sent");
						switch(Command.valueOf(Client.get().read())){
						case Success:
							//System.out.println("cant open");
							Client.get().setUser(userText);
							mNav.toMain();
							break;
						case Failure:
						default:
							JOptionPane.showMessageDialog(null,
									"Username or Password is invalid.",
									"Log-in Failed",
									JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(null, 
							"Server cannot be reached.\nProgram in offline mode.",
						    "Log-in Failed",
						    JOptionPane.WARNING_MESSAGE);
						mNav.toMain();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		mUsernameLabel = new JLabel("Username:");
		mUsernameField = new JTextField(10);
		mPasswordLabel = new JLabel("Password:");
		mPasswordField = new JPasswordField(10);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3,3,3,3);
		add(mUsernameLabel,c);
		add(mUsernameField,c);
		c.gridy = 2;
		add(mPasswordLabel,c);
		add(mPasswordField,c);
		c.gridy = 3;
		c.gridwidth = 3;
		add(mLoginButton,c);
	}

	public LoginPanel(Navigator inNav) {
		mNav = inNav;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(mBackgroundImage, 0, 0, getWidth(), getHeight(), null);
		g.setColor(ThemeColors.MainColor);
		Font font = g.getFont().deriveFont(36.0f);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		int heightc = metrics.getHeight()/2;
		int widthc = metrics.stringWidth(mTitle)/2;
		g.drawString(mTitle, (getWidth()/2) - widthc, (getHeight()/3) - heightc);
	}
}
