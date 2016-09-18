package customui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
//The following code has been modified from the original

public class FileDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 7157603508648615022L;
	private static FileDialog dialog;
    private static String value = "";
    private JTextField valueField;
    private JList<String> list;
    
    public static String showDialog(String[] possibleValues, String confirmText, boolean custom) {
        dialog = new FileDialog(possibleValues, confirmText, custom);
        dialog.setVisible(true);
        return value;
    }

    private FileDialog(String[] filenames, String confirmText, boolean custom) {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				value = null;
				FileDialog.dialog.setVisible(false);
			}
        });
        
        JButton setButton = new JButton(confirmText);
        setButton.setActionCommand("Set");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        list = new JList<String>(filenames) {
			private static final long serialVersionUID = -8147059417879319668L;

			public int getScrollableUnitIncrement(Rectangle visibleRect,  int orientation, int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL &&
                      direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0))  {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(visibleRect, orientation, direction);
            }
        };

        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setPrototypeCellValue("123456789.txt");
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setButton.doClick();
                }
            }
        });
        
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(550, 180));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Choose a File:");
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPane.add(setButton);

        Container contentPane = getContentPane();
        JPanel mPanel = new JPanel(new BorderLayout());
        mPanel.add(listPane, BorderLayout.CENTER);
        valueField = new JTextField(30);
        if(custom) {
	        Box south = Box.createHorizontalBox();
	        south.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	        south.add(new JLabel("File:"));
	        south.add(valueField);
	        mPanel.add(south,BorderLayout.SOUTH);
        }
        contentPane.add(mPanel);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setModal(true);
    }

    public void actionPerformed(ActionEvent e) {
        if ("Set".equals(e.getActionCommand())) {
        	String value = valueField.getText();
        	if(value.length() > 0) FileDialog.value = value;
        	else FileDialog.value = (String)(list.getSelectedValue());
        }
        FileDialog.dialog.setVisible(false);
    }
}
