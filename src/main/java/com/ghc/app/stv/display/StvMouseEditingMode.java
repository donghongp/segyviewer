package com.ghc.app.stv.display;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.ghc.app.general.Mode;
import com.ghc.app.general.ModeManager;

public class StvMouseEditingMode extends Mode {
	private static final long serialVersionUID = 1L;
	private JPopupMenu 		_popupMenu 		= new JPopupMenu();
	private int selectedTraceIndex = 0;
	private Component 		_component 		= null;
	private JComponent [] 	_jComponent 	= null;

	private String _selectedCategory = null;

	public StvMouseEditingMode(ModeManager modeManager) {
		super(modeManager);
		setName("Editing");
		setShortDescription("Editing viewer");
		// _popupMenuListener = new StvPopupMenuListener();
	}

	public boolean isExclusive() { return false; }
	public void setJComponent(JComponent [] jComponent) 			{ _jComponent = jComponent; }

	public void setMouseListener(MouseListener ml) 					{ _component.addMouseListener(ml); }
	public void setMouseMotionListener(MouseMotionListener mml) 	{ _component.addMouseMotionListener(mml); }
	protected void setActive(Component component, boolean active) {
		if ((component instanceof JPanel)) {
			if (active) {
				_component = component;
				component.addMouseListener(_ml);
			} else {
				component.removeMouseListener(_ml);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// private
	//	private KeyListener _kl2 = new KeyAdapter() {
	//		public void keyTyped(KeyEvent e) { }
	//		public void keyReleased(KeyEvent e) { }
	//		public void keyPressed(KeyEvent e) { }
	//	};

	private ViewPanel viewPanel;
	public MouseListener _ml = new MouseAdapter() {
		public void mouseEntered(MouseEvent e) {
			Object source = e.getSource();
			if (source instanceof ViewPanel) {
				viewPanel = (ViewPanel) source;
			}
		}
		public void mouseExited(MouseEvent e) {
			viewPanel = null;
		}

		public void mousePressed(MouseEvent evt) {
			Object source = evt.getSource();
			if (source instanceof ViewPanel) {
				// if(_panel!=null) _panel.mousePressedOn(evt);

				if (evt.isMetaDown()) {// Press mouse right button
					if ((!evt.isAltDown()) && (!evt.isShiftDown())) {
						int xmouse = evt.getX();
						int ymouse = evt.getY();
						selectedTraceIndex = viewPanel.getTraceIndex(xmouse, ymouse);
						viewPanel.getRenderer().setSelectedTraceIndex(selectedTraceIndex);
						viewPanel.update(true, false, true);
						_popupMenu = new JPopupMenu();
						// _popupMenu.addPopupMenuListener(_popupMenuListener);
						setPopupMenu(_popupMenu, viewPanel);
						_popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
					}
				} else {
					// int index = (int)_vx;
				}
			}
		}

		public void mouseReleased(MouseEvent evt) { }
	};

	public void setPopupMenu(JPopupMenu popupMenu, ViewPanel viewPanel1) {
		JMenuItem jMenuItem = null;
		JMenu jMenu = null;
		ViewPanel viewPanel = viewPanel1;

		jMenuItem = new JMenuItem("Unselect");
		jMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedTraceIndex = -1;
				if (viewPanel != null) {
					viewPanel.getRenderer().setSelectedTraceIndex(selectedTraceIndex);
					viewPanel.update(false, false, true);
				}
			}
		});
	}

}
