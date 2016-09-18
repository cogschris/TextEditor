package frame;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import customui.OfficeMenuBar;
import customui.ThemeColors;
import library.ImageLibrary;
import textdocument.TextDocumentManager;

public class OfficeFrame extends JFrame implements Navigator{
	private static final long serialVersionUID = 9183816558021947333L;

	{
		setTitle("Trojan Office");
		setSize(640,480);
		setMinimumSize(new Dimension(640,480));
		setJMenuBar(new OfficeMenuBar());
		getContentPane().add(new MainMenu(this));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//http://stackoverflow.com/questions/7434845/setting-the-default-font-of-swing-program
	public static void setUIFont (javax.swing.plaf.FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	}
	
	public static void setUITabs() {
		UIManager.put("TabbedPane.shadow",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.darkShadow",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.light",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.highlight",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.tabAreaBackground",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.unselectedBackground",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.background",ThemeColors.SecondColor);
	    UIManager.put("TabbedPane.foreground",ThemeColors.WhiteColor);
	    UIManager.put("TabbedPane.focus",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.contentAreaColor",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.selected",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.selectHighlight",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.borderHightlightColor",ThemeColors.MainColor);
	}
	
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			setUIFont(new javax.swing.plaf.FontUIResource(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(12.0f)));
			setUITabs();
			
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
	        Method getApplicationMethod = applicationClass.getMethod("getApplication");
	        Method setDockIconMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
	        Object macOSXApplication = getApplicationMethod.invoke(null);
	        setDockIconMethod.invoke(macOSXApplication, ImageLibrary.getImage("img/icon/office.png"));
	        
		} catch(Exception e){
			System.out.println("Warning! Cross-platform L&F not used!");
		} finally {
			/*Not necessary for Assignment 2 - but this is good practice*/
			SwingUtilities.invokeLater(() -> {
				OfficeFrame of = new OfficeFrame();
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Cursor c = toolkit.createCustomCursor(ImageLibrary.getImage("img/icon/cursor.png") , new Point(0, 0), "img");
				of.setCursor(c);
				of.setIconImage(ImageLibrary.getImage("img/icon/office.png"));
				of.setVisible(true);
				}
			);
		}
	}

	@Override
	public void toMain() {
		getContentPane().removeAll();
		getContentPane().add(new TextDocumentManager(getJMenuBar()));
		revalidate();
		repaint();
	}

	@Override
	public void toLogin() {
		getContentPane().removeAll();
		getContentPane().add(new LoginPanel(this));
		revalidate();
		repaint();
	}

	@Override
	public void toSignup() {
		getContentPane().removeAll();
		getContentPane().add(new SignupPanel(this));
		revalidate();
		repaint();
	}
}
