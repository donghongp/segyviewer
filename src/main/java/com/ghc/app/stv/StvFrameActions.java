package com.ghc.app.stv;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import lombok.Getter;

import com.ghc.app.general.MouseModes;
import com.ghc.app.general.StvConst;
import com.ghc.app.resources.GhcImageLoader;
import com.ghc.app.stv.display.ViewPanel;

/**
 * Actions that can be applied from SeaView application
 */
@Getter
@SuppressWarnings("serial")
public class StvFrameActions {
	private ZoomInAction zoomInAction;
	private ZoomOutAction zoomOutAction;
	private ZoomInHorzAction zoomInHorzAction;
	private ZoomOutHorzAction zoomOutHorzAction;
	private ZoomInVertAction zoomInVertAction;
	private ZoomOutVertAction zoomOutVertAction;
	private SnapShotAction snapShotAction;
	private SnapShotPaneAction snapShotPaneAction;
	private SnapShotFileAction snapShotFileAction;
	private SnapShotPaneFileAction snapShotPaneFileAction;
	private PanModeAction panModeAction;
	private PickModeAction pickModeAction;

	private SettingsAction settingsAction;
	private RefreshAction refreshAction;
	
	private StvFrame stvFrame;
	private ViewPanel viewPanel;

	public StvFrameActions(StvFrame stvFrame) {
		this.stvFrame = stvFrame;
		this.viewPanel = stvFrame.getViewPanel();
		zoomInAction = new ZoomInAction();
		zoomOutAction = new ZoomOutAction();
		zoomInHorzAction = new ZoomInHorzAction();
		zoomOutHorzAction = new ZoomOutHorzAction();
		zoomInVertAction = new ZoomInVertAction();
		zoomOutVertAction = new ZoomOutVertAction();

		snapShotAction = new SnapShotAction();
		snapShotPaneAction = new SnapShotPaneAction();
		snapShotFileAction = new SnapShotFileAction();
		snapShotPaneFileAction = new SnapShotPaneFileAction();

		panModeAction = new PanModeAction();
		pickModeAction = new PickModeAction();

		settingsAction = new SettingsAction();
		refreshAction = new RefreshAction();
	}

	public boolean validateView() {
		if (viewPanel == null) {
			return false;
		}
		return viewPanel.isShowing();
	}
	// ------------------------------------------------------------------------
	class ZoomInAction extends AbstractAction {
		public ZoomInAction() {
			super("Zoom in", GhcImageLoader.getIcon("icons/csZoomIn.gif"));
			// super("Zoom in", Resources.getIcon("zoomIn.png", "icons"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Zoom in");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.zoom(StvConst.ZOOM_IN, StvConst.ZOOM_BOTH);
			}
		}
	}

	// ------------------------------------------------------------------------
	class ZoomOutAction extends AbstractAction {
		public ZoomOutAction() {
			super("Zoom out", GhcImageLoader.getIcon("icons/csZoomOut.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Zoom out");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.zoom(StvConst.ZOOM_OUT, StvConst.ZOOM_BOTH);
			}

		}
	}

	// ------------------------------------------------------------------------
	class ZoomInHorzAction extends AbstractAction {
		public ZoomInHorzAction() {
			super("Zoom in horz", GhcImageLoader.getIcon("icons/csZoomInHorz.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Zoom in horizontally");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.zoom(StvConst.ZOOM_IN, StvConst.ZOOM_HORZ);
			}
		}
	}

	// ------------------------------------------------------------------------
	class ZoomOutHorzAction extends AbstractAction {
		public ZoomOutHorzAction() {
			super("Zoom out horz", GhcImageLoader.getIcon("icons/csZoomOutHorz.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Zoom out horizontally");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.zoom(StvConst.ZOOM_OUT, StvConst.ZOOM_HORZ);
			}
		}
	}

	// ------------------------------------------------------------------------
	class ZoomInVertAction extends AbstractAction {
		public ZoomInVertAction() {
			super("Zoom in vert", GhcImageLoader.getIcon("icons/csZoomInVert.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Zoom in vertically");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.zoom(StvConst.ZOOM_IN, StvConst.ZOOM_VERT);
			}
		}
	}

	// ------------------------------------------------------------------------
	class ZoomOutVertAction extends AbstractAction {
		public ZoomOutVertAction() {
			super("Zoom out vert", GhcImageLoader.getIcon("icons/csZoomOutVert.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Zoom out vertically");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.zoom(StvConst.ZOOM_OUT, StvConst.ZOOM_VERT);
			}
		}
	}

	// ------------------------------------------------------------------------
	class SnapShotAction extends AbstractAction {
		public SnapShotAction() {
			super("Snapshot", GhcImageLoader.getIcon("icons/seaview_snapshot.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Save snapshot to clipboard");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				stvFrame.createSnapShot(false, false, null);
			}
		}
	}

	// ------------------------------------------------------------------------
	class SnapShotPaneAction extends AbstractAction {
		public SnapShotPaneAction() {
			super("Snapshot incl. side labels", GhcImageLoader.getIcon("icons/seaview_snapshot_pane.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Save snapshot including side labels to clipboard");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				stvFrame.createSnapShot(false, true, null);
			}
		}
	}

	// ------------------------------------------------------------------------
	class SnapShotFileAction extends AbstractAction {
		public SnapShotFileAction() {
			super("Snapshot", GhcImageLoader.getIcon("icons/seaview_snapshot_file.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Save snapshot to file");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				stvFrame.createSnapShot(false, false, stvFrame.getProject().getImagesCwd());
			}
		}
	}

	// ------------------------------------------------------------------------
	class SnapShotPaneFileAction extends AbstractAction {
		public SnapShotPaneFileAction() {
			super("Snapshot incl. side labels", GhcImageLoader.getIcon("icons/seaview_snapshot_pane_file.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Save snapshot including side labels to file");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				stvFrame.createSnapShot(false, true, stvFrame.getProject().getImagesCwd());
			}
		}
	}

	class PanModeAction extends AbstractAction {
		public PanModeAction() {
			super("Pan mode", GhcImageLoader.getIcon("icons/csPanMode.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Pan mode");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				if (e.getSource() instanceof JToggleButton) {
					JToggleButton button = (JToggleButton) e.getSource();
					if (button.isSelected()) {
						viewPanel.setMouseMode(MouseModes.PAN_MODE);
					} else {
						viewPanel.setMouseMode(MouseModes.NO_MODE);
					}
				}
			}
		}
	}

	class PickModeAction extends AbstractAction {
		public PickModeAction() {
			super("Pick mode", GhcImageLoader.getIcon("icons/csPickMode.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Picking mode");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				if (e.getSource() instanceof JToggleButton) {
					JToggleButton button = (JToggleButton) e.getSource();
					// viewPanel.setMouseMode(MouseModes.PICK_MODE, button);
					stvFrame.createSnapShot(true, false, null);
				}
			}
		}
	}

	// ------------------------------------------------------------------------
	class SettingsAction extends AbstractAction {
		public SettingsAction() {
			super("Settings", GhcImageLoader.getIcon("icons/settingsmall.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Create snapshot");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.getStvFrame().getStvPanel().toggleViewSettingsPanel();
			}
		}
	}
	
	class RefreshAction extends AbstractAction {
		public RefreshAction() {
			super("Refresh", GhcImageLoader.getIcon("icons/refresh3.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Refresh display whenever needed");
		}

		public void actionPerformed(ActionEvent e) {
			if (validateView()) {
				viewPanel.getStvFrame().getStvPanel().update(viewPanel.getStvFrame().getTraceContext());
			}
		}
	}

}

