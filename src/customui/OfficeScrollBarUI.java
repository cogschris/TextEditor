package customui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalScrollBarUI;

import library.ImageLibrary;

public class OfficeScrollBarUI extends MetalScrollBarUI {

	private static final Image sThumbImage;
	private static final Image sTrackImage;
	private static final Image sButtonUpImage;
	private static final Image sButtonDownImage;
	
	static{
		sThumbImage = ImageLibrary.getImage("img/scrollbar/red_button05.png");
		sTrackImage = ImageLibrary.getImage("img/scrollbar/red_button03.png");
		sButtonUpImage = ImageLibrary.getImage("img/scrollbar/red_sliderUp.png");
		sButtonDownImage = ImageLibrary.getImage("img/scrollbar/red_sliderDown.png");
	}
	
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
		Image sized = sThumbImage.getScaledInstance(r.width, r.height, Image.SCALE_FAST);
		g.drawImage(sized, r.x, r.y, r.width, r.height, null);
	}
	
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
		Image sized = sTrackImage.getScaledInstance(r.width, r.height, Image.SCALE_FAST);
		g.drawImage(sized, r.x, r.y, r.width, r.height, null);
	}
	
	@Override
    protected JButton createDecreaseButton(int orientation) {
		return new JButton() {
			private static final long serialVersionUID = 372463836246818325L;
			{
				setOpaque(false);
				setContentAreaFilled(false);
				setBorderPainted(false);
				setBackground(new Color(0,0,0,0));
			}
        	@Override
        	protected void paintComponent(Graphics g) {
        		g.drawImage(sButtonUpImage, 0, 0, getWidth(), getHeight(), null);
        	}
        };
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
    	return new JButton() {
			private static final long serialVersionUID = 372463836246818325L;
			{
				setOpaque(false);
				setContentAreaFilled(false);
				setBorderPainted(false);
				setBackground(new Color(0,0,0,0));
			}
        	@Override
        	protected void paintComponent(Graphics g) {
        		g.drawImage(sButtonDownImage, 0, 0, getWidth(), getHeight(), null);
        	}
        };
    }
}