package com.ghc.app.stv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;

import com.ghc.app.stv.display.DisplayPanel;
import com.ghc.app.stv.display.ViewPanel;
import com.ghc.app.stv.display.ViewSettingsPanel;
import com.ghc.app.utils.StatusBar;

/**
 * Seismic scroll pane. A seismic display consists of mainly two Java objects: The seismic
 * display itself (SeisView) and the scroll pane in which the display resides (SeisPane).
 * SeisPane encapsulates some of SeisView's functionality and acts as the interface to the
 * outside world. However, the encapsulation is not perfect, so in some instances the
 * methods of the SeisView object have to be called directly.
 * 
 * The SeisPane scroll pane provides: - vertical and horizontal scroll bars, - vertical
 * annotation area (side label), showing times, - horizontal annotation area (side label),
 * showing trace headers, and - zoom functionality based on mouse movements inside side
 * label areas.
 * 
 * Note: JScrollPane was avoided because of some bugs/inconsistent features.
 * 
 * Usage: To construct a full seismic display... 1) Create seismic view SeisView view =
 * new SeisView( parent_JFrame ); 2) Construct seismic pane SeisPane pane = new
 * csSeisPane( view ); 3) Create trace header definitions Header traceHeaders = new
 * Header[numHeaders]; for( ...all trace headers... ) { traceHeaders[i] = new Header(
 * headerName, headerDesc, headerType ); } 4) Load seismic data, populate trace buffer
 * TraceBuffer traceBuffer = new TraceBuffer( numSamples, numHeaders ); for( ...all
 * traces... ) { float[] sampleValues = new float[numSamples]; Number[] headerValues = new
 * Number[numHeaders]; // ...Set sample and header values... traceBuffer.addTrace( new
 * SeismicTrace( sampleValues, headerValues ) ); } 5) Finally, update seismic pane with
 * populated trace buffer pane.updateSeismic( traceBuffer, sampleInt, traceHeaders );
 */

@SuppressWarnings("serial")
@Getter
@Setter
public class StvPanel extends JPanel {

	private StvFrame stvFrame = null;
	private StvFrameActions stvFrameActions = null;
	private JToolBar toolBarLeft;
	private DisplayPanel displayPanel = null;

	private JToolBar toolBar;
	private ViewSettingsPanel viewSettingsPanel = null;
	JScrollPane viewSettingsJScrollPane = null;

	private JLabel trackingLabel = null;

	public StvPanel(StvFrame stvFrame, ViewPanel viewPanel) {
		super(new BorderLayout());
		setMinimumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(801, 601));
		this.stvFrame = stvFrame;
		stvFrameActions = stvFrame.getStvFrameActions();
		displayPanel = new DisplayPanel(this, viewPanel);
		toolBar = new JToolBar();
		toolBarLeft = new JToolBar(JToolBar.VERTICAL);
		init();
	}

	public void init() {
		// initToolBar();
		JButton jbutton = new JButton("V1");
		toolBarLeft.add(jbutton);

		StatusBar statusBar = new StatusBar();
		statusBar.setZoneBorder(BorderFactory.createLineBorder(Color.GRAY));

		statusBar.setZones(new String[] { "first_zone", "second_zone", "remaining_zones" },
				new Component[] { new JLabel("first"), new JLabel("second"), new JLabel("remaining") },
				new String[] { "40%", "40%", "*" });
		trackingLabel = (JLabel) statusBar.getZone("remaining_zones");

		viewSettingsPanel = new ViewSettingsPanel(stvFrame);
		viewSettingsJScrollPane = new JScrollPane(viewSettingsPanel);
		add(displayPanel, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
	}

	public void update(TraceContext traceContext) {
		displayPanel.update(traceContext);
	}

	public void toggleViewSettingsPanel() {
		if (viewSettingsPanel.isShowing()) {
			remove(viewSettingsJScrollPane);
		} else {
			add(viewSettingsJScrollPane, BorderLayout.EAST);
		}
		revalidate();

		if (stvFrame.getTraceBuffer() != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayPanel.getViewPanel().resetScreenBuffer();
					displayPanel.getViewPanel().repaint();
				}
			});
		}
	}

}

