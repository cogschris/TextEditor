package customui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JTabbedPane;

import library.ImageLibrary;

public class OfficeTabbedPane extends JTabbedPane{
	private static final long serialVersionUID = 7141608019316770268L;
	
	private static final Image mBackgroundImage;
	private static final String mTitle = "Trojan Office";
	
	static {
		mBackgroundImage = ImageLibrary.getImage("img/backgrounds/darkgrey_panel.png");
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
		g.drawString(mTitle, (getWidth()/2) - widthc, (getHeight()/2) - heightc);
		super.paintComponent(g);
	}
}
