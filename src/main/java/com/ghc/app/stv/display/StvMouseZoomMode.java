package com.ghc.app.stv.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.ghc.app.general.Mode;
import com.ghc.app.general.ModeManager;
import com.ghc.app.general.StvConst;

/**
 * A mode for zooming tiles and tile axes.
 * 
 * @author Dave Hale, Colorado School of Mines
 * @version 2004.12.29
 */
public class StvMouseZoomMode extends Mode {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a tile zoom mode with specified manager.
	 * 
	 * @param modeManager the mode manager for this mode.
	 */
	public StvMouseZoomMode(ModeManager modeManager) {
		super(modeManager);
		setName("Zoom");
		// setIcon(loadIcon(StvMouseZoomMode.class, "ZoomIn16.gif"));
		// setMnemonicKey(KeyEvent.VK_Z);
		// setAcceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0));
		setShortDescription("Zoom in tile or axis");
	}

	public boolean isExclusive() {
		return false;
	}
	///////////////////////////////////////////////////////////////////////////
	// protected

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
	private int _xdraw; // x coordinate to which zoom rect was last drawn
	private int _ydraw; // y coordinate to which zoom rect was last drawn
	private SampleInfo pos1;
	private SampleInfo pos2;

	private MouseListener _ml = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if (e.isAltDown() && e.isShiftDown() && (!e.isControlDown())) {
				beginZoom(e);
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isAltDown() && e.isShiftDown() && (!e.isControlDown())) {
				zoom();
			}
			endZoom();
		}
	};

	private MouseMotionListener _mml = new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) {
			if (e.isAltDown() && e.isShiftDown() && (!e.isControlDown())) {
				duringZoom(e);
			}
		}
	};

	private void beginZoom(MouseEvent e) {
		_xbegin = e.getX();
		_ybegin = e.getY();
		Object source = e.getSource();
		if (source instanceof ViewPanel) {
			ViewPanel viewPanel = (ViewPanel) source;
			this.viewPanel = viewPanel;
			drawZoom(viewPanel, _xbegin, _ybegin, true, true);
			viewPanel.addMouseMotionListener(_mml);
			pos1 = viewPanel.getSampleInfo(_xbegin, _ybegin);
		}
	}

	private void duringZoom(MouseEvent e) {
		int xdraw = e.getX();
		int ydraw = e.getY();
		if (viewPanel != null) {
			drawZoom(viewPanel, _xdraw, _ydraw, true, true);
			drawZoom(viewPanel, xdraw, ydraw, true, true);
		}
	}

	private void endZoom() {
		if (this.viewPanel != null) {
			drawZoom(this.viewPanel, _xdraw, _ydraw, true, true);
			this.viewPanel.removeMouseMotionListener(_mml);
			this.viewPanel = null;
		}
	}

	private void drawZoom(ViewPanel viewPanel, int x, int y, boolean bx, boolean by) {
		if (viewPanel == null) {
			return;
		}
		// Clip
		x = max(0, min(viewPanel.getWidth() - 1, x));
		y = max(0, min(viewPanel.getHeight() - 1, y));

		// Draw zoom in this tile.
		drawRect(viewPanel, x, y, bx, by);
	}

	private void drawRect(JComponent c, int x, int y, boolean bx, boolean by) {
		_xdraw = x;
		_ydraw = y;
		int xmin = bx ? min(_xbegin, _xdraw) : -1;
		int xmax = bx ? max(_xbegin, _xdraw) : c.getWidth();
		int ymin = by ? min(_ybegin, _ydraw) : -1;
		int ymax = by ? max(_ybegin, _ydraw) : c.getHeight();
		Graphics g = c.getGraphics();
		g.setColor(Color.RED);
		g.setXORMode(c.getBackground());
		g.drawRect(xmin, ymin, xmax - xmin, ymax - ymin);
		g.dispose();
	}

	private void zoom() {
		if (this.viewPanel != null) {
			SampleInfo pos2 = viewPanel.getSampleInfo(_xdraw, _ydraw);
			if (pos1.isZoomable(pos2)) {
				viewPanel.getStvFrame().getStvPanel().getDisplayPanel().zoomArea(pos1, pos2);
			} else {
				viewPanel.getStvFrame().getStvPanel().getDisplayPanel().zoom(StvConst.ZOOM_OUT, StvConst.ZOOM_BOTH);
			}
		}
	}

}




