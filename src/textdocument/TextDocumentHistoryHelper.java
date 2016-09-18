package textdocument;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import library.ImageLibrary;

public class TextDocumentHistoryHelper {
	private Document mDocument;
	private final UndoManager mUndoManager;
	private final JMenuItem mUndoItem;
	private final JMenuItem mRedoItem;

	{
		mUndoManager = new UndoManager();
		
		mUndoItem = new JMenuItem("Undo");
		mUndoItem.setMnemonic('U');
		mUndoItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/undo.png")));
		mUndoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		mUndoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mUndoManager.undo();
				} catch(CannotUndoException ue) {
					//ERROR WARNING
				}
				updateHistoryActions();
			}
		});
		
		mRedoItem = new JMenuItem("Redo");
		mRedoItem.setMnemonic('R');
		mRedoItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/redo.png")));
		mRedoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		mRedoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mUndoManager.redo();
				} catch(CannotRedoException re) {
					//ERROR WARNING
				}
				updateHistoryActions();
			}
		});
		updateHistoryActions();
	}
	
	public TextDocumentHistoryHelper(Document inDocument) {
		setDocument(inDocument);
	}
	
	public void setDocument(Document inDocument) {
		mDocument = inDocument;
		mDocument.addUndoableEditListener( new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				mUndoManager.addEdit(e.getEdit());
				updateHistoryActions();
			}
		});
	}
	
	private void updateHistoryActions() {
		mUndoItem.setEnabled(mUndoManager.canUndo());
	    mRedoItem.setEnabled(mUndoManager.canRedo());
	}
	
	public JMenuItem getUndoMenuItem() {
		return mUndoItem;
	}
	
	public JMenuItem getRedoMenuItem() {
		return mRedoItem;
	}
	
}
