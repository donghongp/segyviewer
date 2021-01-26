package com.ghc.app.stv.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import lombok.Getter;
import lombok.Setter;

import com.ghc.app.general.ModeManager;
import com.ghc.app.general.StvConst;
import com.ghc.app.stv.HomePanel;
import com.ghc.app.stv.StvFrame;
import com.ghc.app.stv.StvPanel;
import com.ghc.app.stv.TraceContext;

@Getter
@Setter
public class DisplayPanel extends JPanel implements ISeisViewListener, IPanningListener {

	protected StvFrame stvFrame = null;
	protected StvPanel stvPanel = null;
	protected ViewPanel viewPanel = null;
	protected ViewSettings viewSettings = null;

	private JScrollBar scrollBarVert = null;
	private JScrollBar scrollBarHorz = null;
	private int previousScrollValueVert = 0;
	private int previousScrollValueHorz = 0;
	private boolean stopScrolling = false;

	private SideLabelCornerPanel sideLabelCornerPanel;
	private SideLabelHorzPanel sideLabelHorzPanel;
	private SideLabelVertPanel sideLabelVertPanel;
	private JPanel viewPort;
	private HomePanel homePanel;

	public DisplayPanel(StvPanel stvPanel, ViewPanel viewPanel) {
		super(new BorderLayout());
		this.stvPanel = stvPanel;
		stvFrame = stvPanel.getStvFrame();
		viewSettings = stvFrame.getTraceContext().getViewSettings();
		this.viewPanel = viewPanel;
		viewPanel.addSeisViewListener(this);
		viewPanel.addPanningListener(this);
		init();
	}


	public void init() {
		scrollBarVert = new JScrollBar(JScrollBar.VERTICAL);
		scrollBarVert.setValues(0, 50, 0, 0);
		scrollBarVert.setBlockIncrement(200);
		scrollBarVert.setUnitIncrement(StvConst.DEFAULT_UNIT_INCREMENT);
		scrollBarVert.setVisibleAmount(200);

		scrollBarHorz = new JScrollBar(JScrollBar.HORIZONTAL);
		scrollBarHorz.setValues(0, 50, 0, 0);
		scrollBarHorz.setBlockIncrement(200);
		scrollBarHorz.setUnitIncrement(StvConst.DEFAULT_UNIT_INCREMENT);
		scrollBarHorz.setVisibleAmount(200);

		sideLabelCornerPanel = new SideLabelCornerPanel(this);
		sideLabelHorzPanel = new SideLabelHorzPanel(this);
		sideLabelVertPanel = new SideLabelVertPanel(this);

		scrollBarVert.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// if (scrollBarVert.getValueIsAdjusting()) {
				// return;
				// }
				int valueNew = e.getValue();
				if (previousScrollValueVert != valueNew) {
					previousScrollValueVert = valueNew;
					viewPanel.resetViewPositionVert(valueNew);
				}
			}
		});

		scrollBarHorz.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// if (scrollBarHorz.getValueIsAdjusting()) {
				// return;
				// }
				int valueNew = e.getValue();
				if (getStvFrame().getTraceContext().getViewSettings().plotDirection == StvConst.PLOT_DIR_HORIZONTAL) {
					valueNew = scrollBarHorz.getMaximum() - valueNew;
				}
				if (previousScrollValueHorz != valueNew) {
					previousScrollValueHorz = valueNew;
					viewPanel.resetViewPositionHorz(valueNew);
				}
			}
		});
		viewPanel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
					int newValue = scrollBarVert.getValue() + e.getUnitsToScroll() * scrollBarVert.getUnitIncrement();
					if (newValue > scrollBarVert.getMinimum() && newValue < scrollBarVert.getMaximum()) {
						scrollBarVert.setValue(newValue);
					}
				}
			}
		});

		viewPort = new JPanel(new GridBagLayout());
		// viewPort.setBackground(Color.green);
		homePanel = new HomePanel();
		viewPort.add(homePanel, new GridBagConstraints(
				0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		// viewPort.add(viewPanel, new GridBagConstraints(
		// 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
		// GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// viewPort.add(Box.createHorizontalGlue(), new GridBagConstraints(
		// 1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
		// GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		// viewPort.add(Box.createVerticalGlue(), new GridBagConstraints(
		// 0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,
		// GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(sideLabelCornerPanel, BorderLayout.WEST);
		topPanel.add(sideLabelHorzPanel, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);
		add(viewPort, BorderLayout.CENTER);
		add(sideLabelVertPanel, BorderLayout.WEST);
		add(scrollBarVert, BorderLayout.EAST);
		add(scrollBarHorz, BorderLayout.SOUTH);

	}

	public void addModeManager() {
		boolean enableZoom = true;
		boolean enableTracking = true;
		boolean enableEditing = true;
		ModeManager modeManager = new ModeManager();
		modeManager.add(viewPanel);
		modeManager.add(sideLabelHorzPanel);
		modeManager.add(sideLabelVertPanel);
		StvMouseZoomMode mouseZoomMode = new StvMouseZoomMode(modeManager);
		mouseZoomMode.setActive(enableZoom);

		StvMousePanMode mousePanMode = new StvMousePanMode(modeManager);
		mousePanMode.setActive(true);

		if (enableTracking) {
			StvMouseTrackInfoMode mouseTrackInfoMode = new StvMouseTrackInfoMode(modeManager);
			mouseTrackInfoMode.setActive(enableTracking);
			mouseTrackInfoMode.setTrackingLabel(stvFrame.getStvPanel().getTrackingLabel());
		}
		if (enableEditing) {
			StvMouseEditingMode mouseEditingMode = new StvMouseEditingMode(modeManager);
			mouseEditingMode.setActive(enableEditing);
		}
	}

	public void scrollVertical(int scrollStep) {
		int newValue = scrollBarVert.getValue() + scrollStep;
		if (newValue > scrollBarVert.getMinimum() && newValue < scrollBarVert.getMaximum()) {
			scrollBarVert.setValue(newValue);
		}
	}

	public void scrollHorizontal(int scrollStep) {
		int newValue = scrollBarHorz.getValue() + scrollStep;
		if (newValue > scrollBarHorz.getMinimum() && newValue < scrollBarHorz.getMaximum()) {
			scrollBarHorz.setValue(newValue);
		}
	}

	@Override
	public void hasPanned(int dx, int dy) {
		scrollHorizontal(dx);
		scrollVertical(dy);
	}

	public void update(TraceContext traceContext) {
		if (viewPanel.isShowing()) {
			viewPanel.update(true, true, false);
		}
		if (homePanel != null) {
			viewPort.remove(homePanel);
			homePanel = null;
			viewPort.add(viewPanel, new GridBagConstraints(
					0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			viewPanel.update(true, true, false);
		}
		sideLabelHorzPanel.repaint();
		sideLabelVertPanel.repaint();
	}

	public void zoom(int inOut, int zoomType) {
		float zoomVert = viewSettings.zoomVert;
		float zoomHorz = viewSettings.zoomHorz;
		if ((zoomType & StvConst.ZOOM_VERT) != 0) {
			if (inOut == StvConst.ZOOM_IN) {
				zoomVert *= 2.0f;
			} else {
				zoomVert /= 2.0f;
			}
			if (zoomVert == 0)
				zoomVert = 1;
		}
		if ((zoomType & StvConst.ZOOM_HORZ) != 0) {
			if (inOut == StvConst.ZOOM_IN) {
				zoomHorz *= 2.0f;
			} else {
				zoomHorz /= 2.0f;
			}
			if (zoomHorz == 0)
				zoomHorz = 4.0f;
		}
		zoom(zoomVert, zoomHorz);
	}

	/**
	 * Zoom
	 * 
	 * @param zoomVert Vertical zoom factor (= pixels per time unit)
	 * @param zoomHorz Horizontal zoom factor (pixels per trace)
	 */
	public void zoom(float zoomVert, float zoomHorz) {
		if ((zoomVert <= 0.0 && zoomHorz <= 0.0) ||
				(zoomVert == viewSettings.zoomVert &&
						zoomHorz == viewSettings.zoomHorz)) {
			return;
		}
		viewPanel.zoom(zoomVert, zoomHorz);

		if (zoomVert > 0.0 || zoomVert != viewSettings.zoomVert) {
			sideLabelVertPanel.repaint();
		}
		if (zoomHorz > 0.0 || zoomHorz != viewSettings.zoomHorz) {
			sideLabelHorzPanel.repaint();
		}
	}

	/**
	 * Zoom
	 * 
	 * @param zoomVert Vertical zoom factor (= pixels per time unit)
	 * @param zoomHorz Horizontal zoom factor (pixels per trace)
	 */
	public void zoomArea(SampleInfo pos1, SampleInfo pos2) {
		int height = viewPanel.getVisibleRect().height;
		int width = viewPanel.getVisibleRect().width;

		double traceDiffNew = Math.abs(pos2.traceDouble - pos1.traceDouble);
		double sampleDiffNew = Math.abs(pos2.sampleDouble - pos1.sampleDouble);
		float traceMid = 0.5f * (float) (pos2.traceDouble + pos1.traceDouble);
		float sampleMid = 0.5f * (float) (pos2.sampleDouble + pos1.sampleDouble);

		double traceDiffCurrent = Math.abs(viewPanel.xView2Trace(0) - viewPanel.xView2Trace(width));
		double sampleDiffCurrent = Math.abs(viewPanel.yView2Model(0) - viewPanel.yView2Model(height));

		float zoomVert = viewSettings.zoomVert;
		float zoomHorz = viewSettings.zoomHorz;

		if (traceDiffNew < StvConst.MIN_ZOOM_WIDTH && sampleDiffNew < StvConst.MIN_ZOOM_WIDTH) {
			zoom(StvConst.ZOOM_OUT, StvConst.ZOOM_BOTH);
		} else {
			traceDiffNew = Math.max(StvConst.MIN_ZOOM_WIDTH, traceDiffNew);
			sampleDiffNew = Math.max(StvConst.MIN_ZOOM_WIDTH, sampleDiffNew);
			traceDiffCurrent = Math.max(StvConst.MIN_ZOOM_WIDTH, traceDiffCurrent);
			sampleDiffCurrent = Math.max(StvConst.MIN_ZOOM_WIDTH, sampleDiffCurrent);

			zoomVert *= (float) (sampleDiffCurrent / sampleDiffNew);
			zoomHorz *= (float) (traceDiffCurrent / traceDiffNew);
			// System.out.println("SAMPLE " + sampleDiffNew + " " + sampleDiffCurrent + "
			// " + ( sampleDiffNew / sampleDiffCurrent ) );
			// System.out.println("TRACE " + traceDiffNew + " " + traceDiffCurrent + " " +
			// ( traceDiffNew / traceDiffCurrent ) );
			// System.out.println("SAMPLE_MID " + sampleMid );
			// System.out.println("TRACE " + traceMid );
			viewPanel.zoom(zoomVert, zoomHorz, traceMid, sampleMid);

			sideLabelVertPanel.repaint();
			sideLabelHorzPanel.repaint();
		}
	}

	public void zoomHorz(float zoomLevel) {
		if (zoomLevel > 0.0 && zoomLevel != viewSettings.zoomHorz) {
			zoom(zoomLevel, viewSettings.zoomHorz);
		}
	}

	public void zoomVert(float zoomLevel) {
		if (zoomLevel > 0.0 && zoomLevel != viewSettings.zoomVert) {
			zoom(viewSettings.zoomVert, zoomLevel);
		}
	}

	public void settingsChanged(ViewSettings viewSettings) {
		sideLabelVertPanel.settingsChanged(viewSettings);
		viewPanel.update(false, false, true);
	}


	public void vertScrollChanged(int scrollValue) {
		sideLabelVertPanel.repaint();
	}

	public void horzScrollChanged(int scrollValue) {
		sideLabelHorzPanel.repaint();
	}

	public void sizeChanged(Dimension size) {

	}

}
