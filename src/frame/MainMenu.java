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

import customui.OfficeButton;
import customui.OfficePanel;
import customui.ThemeColors;
import library.ImageLibrary;

public class MainMenu extends OfficePanel {
	private static final long serialVersionUID = -2085019378387937271L;
	
	private static final Image mBackgroundImage;
	private static final String mTitle = "Trojan Office";
	
	private final Navigator mNav;
	
	static {
		mBackgroundImage = ImageLibrary.getImage("img/backgrounds/darkgrey_panel.png");
	}
	
	OfficeButton mLoginButton;
	OfficeButton mSignupButton;
	OfficeButton mOfflineButton;
	
	{
		setLayout(new GridBagLayout());
		mLoginButton = new OfficeButton("Login");
		mLoginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mNav.toLogin();
			}
		});
		mSignupButton = new OfficeButton("Signup");
		mSignupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mNav.toSignup();
			}
		});
		mOfflineButton = new OfficeButton("Offline");
		mOfflineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mNav.toMain();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3,3,3,3);
		add(mLoginButton,c);
		add(mSignupButton,c);
		
		c.gridwidth = 2;
		c.gridy = 2;
		add(mOfflineButton,c);
	}

	public MainMenu(Navigator inNav) {
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
