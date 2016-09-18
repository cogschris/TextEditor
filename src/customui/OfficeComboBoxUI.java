package customui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;
import javax.swing.plaf.metal.MetalComboBoxUI;

import library.ImageLibrary;

public class OfficeComboBoxUI extends MetalComboBoxUI {

	private static final Image mArrowButtonImage;
	
	static {
		mArrowButtonImage = ImageLibrary.getImage("img/menu/red_sliderDown.png");
	}
	
	@Override
	protected JButton createArrowButton() {
		return new JButton(){
			private static final long serialVersionUID = 7393207335502213706L;
			{
				setOpaque(false);
				setContentAreaFilled(false);
				setBorderPainted(false);
				setBackground(new Color(0,0,0,0));
			}
			@Override
			protected void paintComponent(Graphics g) {
				Image sized = mArrowButtonImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST);
				g.drawImage(sized, 0, 0, getWidth(), getHeight(), null);
				super.paintComponent(g);
			}
		};
	}
}
