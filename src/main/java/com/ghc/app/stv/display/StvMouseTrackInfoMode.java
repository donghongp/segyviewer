package com.ghc.app.stv.display;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ghc.app.general.Mode;
import com.ghc.app.general.ModeManager;
import com.ghc.app.general.MouseModes;

public class StvMouseTrackInfoMode extends Mode {
	private static final long serialVersionUID = 1L;

	public JLabel _trackingLabel = null;

	public StvMouseTrackInfoMode(ModeManager modeManager) {
		super(modeManager);
		setName("Track");
		// setIcon(loadIcon(MouseTrackInfoMode.class,"Track24.gif"));
		// setMnemonicKey(KeyEvent.VK_Z);
		// setAcceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_T,0));
		setShortDescription("Track mouse in tile");
	}

	public boolean isExclusive() {
		return false;
	}

	public void setTrackingLabel(JLabel trackingLabel) {
		_trackingLabel = trackingLabel;
	}

	///////////////////////////////////////////////////////////////////////////
	// protected

	protected void setActive(Component component, boolean active) {
		if ((component instanceof JPanel)) {
			if (active) {
				component.addMouseListener(_tml);
			} else {
				component.removeMouseListener(_tml);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// private


	private ViewPanel viewPanel;
	private int _xmouse; // x coordinate where mouse last tracked
	private int _ymouse; // y coordinate where mouse last tracked

	private MouseListener _tml = new MouseAdapter() {
		public void mouseEntered(MouseEvent e) {
			beginTracking(e);
		}

		public void mouseExited(MouseEvent e) {
			endTracking();
		}

		public void mousePressed(MouseEvent evt) {
		}

		public void mouseDragged(MouseEvent evt) {
		}

		public void mouseReleased(MouseEvent evt) {
		}
	};

	private MouseMotionListener _tmml = new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) {
			duringTracking(e);
		}

		public void mouseMoved(MouseEvent e) {
			duringTracking(e);
		}
	};

	private void beginTracking(MouseEvent e) {
		_xmouse = e.getX();
		_ymouse = e.getY();
		Object source = e.getSource();
		if (source instanceof ViewPanel) {
			ViewPanel viewPanel = (ViewPanel) source;
			this.viewPanel = viewPanel;
			if (viewPanel.getMouseMode() == MouseModes.PAN_MODE) {
				viewPanel.setCursor(MouseModes.PAN_CURSOR);
			} else {
				viewPanel.setCursor(Cursor.getDefaultCursor());
			}
			fireTrack();
			viewPanel.addMouseMotionListener(_tmml);
		}
	}

	private void duringTracking(MouseEvent e) {
		_xmouse = e.getX();
		_ymouse = e.getY();
		if (viewPanel != null) {
			fireTrack();
		}
	}

	private void endTracking() {
		if (this.viewPanel != null) {
			fireTrack();
			viewPanel.paintTrackingLine(-1, -1);
			this.viewPanel.removeMouseMotionListener(_tmml);
			this.viewPanel = null;
		}
	}

	private void fireTrack() {
		if (viewPanel == null) {
			return;
		}

		viewPanel.paintTrackingLine(_xmouse, _ymouse);

		if (_trackingLabel != null) {
			int selectedTraceIndex = viewPanel.clipTraceIndex(viewPanel.getTraceIndex(_xmouse, _ymouse));
			int selectedSampleIndex = viewPanel.clipSampleIndex(viewPanel.getSampleIndex(_xmouse, _ymouse));
			float amplitude = viewPanel.getSampleValue(selectedTraceIndex, selectedSampleIndex);
			_trackingLabel.setText(selectedTraceIndex + "," + selectedSampleIndex + "," + amplitude);
			viewPanel.getStvFrame().updateTraceHeaderDialogHeaderValues(selectedTraceIndex);
			viewPanel.getStvFrame().updateTraceSampleDialogSampleValues(selectedTraceIndex);
		}
	}
}
