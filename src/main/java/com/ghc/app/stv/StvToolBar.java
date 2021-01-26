package com.ghc.app.stv;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class StvToolBar extends JToolBar {

	private StvFrame stvFrame = null;
	private StvFrameActions stvFrameActions = null;

	private JButton buttonForwardSeismic;
	private JButton buttonBackwardSeismic;
	private JButton buttonBeginSeismic;
	private JButton buttonEndSeismic;
	private JButton buttonZoomInVert;
	private JButton buttonZoomInHorz;
	private JButton buttonZoomOutVert;
	private JButton buttonZoomOutHorz;
	private JButton buttonZoomInBoth;
	private JButton buttonZoomOutBoth;
	private JButton buttonSnapShot;
	private JButton buttonSnapShotPane;
	private JButton buttonSnapShotFile;
	private JButton buttonSnapShotPaneFile;
	private JButton buttonIncScaling;
	private JButton buttonDecScaling;
	private JButton buttonSettings;

	private JToggleButton buttonShowGraph;
	private JToggleButton buttonKillTraceMode;
	private JToggleButton buttonSpectrum;
	private JToggleButton buttonRubberZoom;
	private JToggleButton buttonPickMode;

	private JToggleButton buttonPanMode;
	private JButton buttonShowOverlay;

	private JButton buttonRefresh;
	private JCheckBox endianCheckBox;

	ArrayList<JComponent> compList = new ArrayList<JComponent>(1);

	public StvToolBar(StvFrame stvFrame) {
		this(stvFrame, JToolBar.HORIZONTAL);
	}


	public StvToolBar(StvFrame stvFrame, int orientation) {
		super(orientation);
		this.stvFrame = stvFrame;
		stvFrameActions = stvFrame.getStvFrameActions();
		initToolBar();
	}

	private void addComp(JComponent c) {
		add(c);
		compList.add(c);
	}

	@Override
	public void setEnabled(boolean enabled) {
		for (int i = 0; i < compList.size(); i++) {
			compList.get(i).setEnabled(enabled);
		}
	}

	public void initToolBar() {
		// buttonBeginSeismic = new JButton(stvFrameActions.getBeginSeismicAction());
		// buttonBeginSeismic.setText("");
		// addComp(buttonBeginSeismic);
		// buttonBackwardSeismic = new
		// JButton(stvFrameActions.getBackwardSeismicAction());
		// buttonBackwardSeismic.setText("");
		// addComp(buttonBackwardSeismic);
		// buttonForwardSeismic = new JButton(stvFrameActions.getForwardSeismicAction());
		// buttonForwardSeismic.setText("");
		// addComp(buttonForwardSeismic);
		// buttonEndSeismic = new JButton(stvFrameActions.getEndSeismicAction());
		// buttonEndSeismic.setText("");
		// addComp(buttonEndSeismic);
		//

		buttonRefresh = new JButton(stvFrameActions.getRefreshAction());
		buttonRefresh.setText("");
		addComp(buttonRefresh);
		
		endianCheckBox = new JCheckBox("Big Endian", stvFrame.getTraceContext().getTraceIOConfig().isBigEndian());
		endianCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				stvFrame.getTraceContext().getTraceIOConfig().setBigEndian(endianCheckBox.isSelected());
				//stvFrame.loadFile(stvFrame.getTraceIO().getFileFormat(),
				//		stvFrame.getTraceIO().getFileName());
			}
		});
		addComp(endianCheckBox);

		addComp(new javax.swing.JToolBar.Separator());
		addComp(new javax.swing.JToolBar.Separator());

		buttonZoomInBoth = new JButton(stvFrameActions.getZoomInAction());
		buttonZoomInBoth.setText("");
		addComp(buttonZoomInBoth);
		buttonZoomInHorz = new JButton(stvFrameActions.getZoomInHorzAction());
		buttonZoomInHorz.setText("");
		addComp(buttonZoomInHorz);
		buttonZoomInVert = new JButton(stvFrameActions.getZoomInVertAction());
		buttonZoomInVert.setText("");
		addComp(buttonZoomInVert);
		buttonZoomOutBoth = new JButton(stvFrameActions.getZoomOutAction());
		buttonZoomOutBoth.setText("");
		addComp(buttonZoomOutBoth);
		buttonZoomOutHorz = new JButton(stvFrameActions.getZoomOutHorzAction());
		buttonZoomOutHorz.setText("");
		addComp(buttonZoomOutHorz);
		buttonZoomOutVert = new JButton(stvFrameActions.getZoomOutVertAction());
		buttonZoomOutVert.setText("");
		addComp(buttonZoomOutVert);

		addComp(new javax.swing.JToolBar.Separator());
		addComp(new javax.swing.JToolBar.Separator());

		buttonPanMode = new JToggleButton(stvFrameActions.getPanModeAction());
		buttonPanMode.setText("");
		addComp(buttonPanMode);
		buttonPickMode = new JToggleButton(stvFrameActions.getPickModeAction());
		buttonPickMode.setText("");
		// addComp(buttonPickMode);

		addComp(new javax.swing.JToolBar.Separator());
		addComp(new javax.swing.JToolBar.Separator());

		buttonSnapShot = new JButton(stvFrameActions.getSnapShotAction());
		buttonSnapShot.setText("");
		// buttonSnapShot.setToolTipText("Create snapshot of current view");
		addComp(buttonSnapShot);
		buttonSnapShotPane = new JButton(stvFrameActions.getSnapShotPaneAction());
		buttonSnapShotPane.setText("");
		// buttonSnapShotPane.setToolTipText("Create snapshot of current view, including
		// side labels");
		addComp(buttonSnapShotPane);
		buttonSnapShotFile = new JButton(stvFrameActions.getSnapShotFileAction());
		buttonSnapShotFile.setText("");
		addComp(buttonSnapShotFile);
		buttonSnapShotPaneFile = new JButton(stvFrameActions.getSnapShotPaneFileAction());
		buttonSnapShotPaneFile.setText("");
		addComp(buttonSnapShotPaneFile);

		addComp(new javax.swing.JToolBar.Separator());
		addComp(new javax.swing.JToolBar.Separator());

		buttonSettings = new JButton(stvFrameActions.getSettingsAction());
		buttonSettings.setText("");
		buttonSettings.setToolTipText("Settings");
		addComp(buttonSettings);

		// buttonShowOverlay = new JButton(stvFrameActions.getShowOverlayAction());
		// buttonShowOverlay.setText("");
		// buttonShowGraph = new JToggleButton(stvFrameActions.getShowGraphAction());
		// buttonShowGraph.setText("");
		// buttonSpectrum = new JToggleButton(stvFrameActions.getSpectrumAction());
		// buttonSpectrum.setText("");
		// buttonRubberZoom = new
		// JToggleButton(stvFrameActions.getRubberBandZoomAction());
		// buttonRubberZoom.setText("");
		//
		// buttonKillTraceMode = new JToggleButton(stvFrameActions.getKillTraceAction());
		// buttonKillTraceMode.setText("");

	}

}
