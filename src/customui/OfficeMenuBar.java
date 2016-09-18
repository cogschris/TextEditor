package customui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JMenuBar;

import library.ImageLibrary;

public class OfficeMenuBar extends JMenuBar{
	private static final long serialVersionUID = -587118990451225425L;
	private static final Image mBackgroundImage;
	
	static {
		mBackgroundImage = ImageLibrary.getImage("img/menu/red_button11.png");
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Image scaled = mBackgroundImage.getScaledInstance(getWidth(), getHeight(), 0);
		g.drawImage(scaled, 0, 0, getWidth(), getHeight(), null);
	}
}
