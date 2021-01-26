package com.ghc.app.stv.display;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.ghc.app.general.Mode;
import com.ghc.app.general.ModeManager;
import com.ghc.app.general.MouseModes;

/**
 * A mode for zooming tiles and tile axes.
 * 
 * @author Dave Hale, Colorado School of Mines
 * @version 2004.12.29
 */
public class StvMousePanMode extends Mode {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a tile zoom mode with specified manager.
	 * 
	 * @param modeManager the mode manager for this mode.
	 */
	public StvMousePanMode(ModeManager modeManager) {
		super(modeManager);
		setName("Pan");
		setShortDescription("Pan mode");
	}

	public boolean isExclusive() {
		return false;
	}

	protected void setActive(Component component, boolean active) {
		if ((component instanceof JPanel)) {
			if (active) {
				component.addMouseListener(_ml);
			} else {
				component.removeMouseListener(_ml);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// private

	private ViewPanel viewPanel; // tile in which zooming began; null, if in axis
	private int _xbegin; // x coordinate where zoom began
	private int _ybegin; // y coordinate where zoom began

	private MouseListener _ml = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
				beginPan(e);
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
				endPan();
			}
		}
	};

	private MouseMotionListener _mml = new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) {
			if (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
				duringPan(e);
			}
		}
	};

	private void beginPan(MouseEvent e) {
		_xbegin = e.getX();
		_ybegin = e.getY();
		Object source = e.getSource();
		if (source instanceof ViewPanel) {
			ViewPanel viewPanel = (ViewPanel) source;
			this.viewPanel = viewPanel;
			if (viewPanel.getMouseMode() == MouseModes.PAN_MODE) {
				viewPanel.addMouseMotionListener(_mml);
			}
		}
	}

	private void duringPan(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (viewPanel != null) {
			if (x > 0 && x < viewPanel.getWidth() && y > 0 && y < viewPanel.getHeight()) {
				int dx = _xbegin - x;
				int dy = _ybegin - y;
				if (dx != 0 || dy != 0) {
					viewPanel.firePanningEvent(dx, dy);
					_xbegin = x;
					_ybegin = y;
				}
			}
		}
	}

	private void endPan() {
		if (this.viewPanel != null) {
			this.viewPanel.removeMouseMotionListener(_mml);
		}
	}
}
