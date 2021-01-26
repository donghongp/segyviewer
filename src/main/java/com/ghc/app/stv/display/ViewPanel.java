package com.ghc.app.stv.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import com.ghc.app.general.ModeManager;
import com.ghc.app.general.StvConst;
import com.ghc.app.resources.GhcImageLoader;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.StvFrame;

public class ViewPanel extends ViewPanelBase {

	private Image backgroundImage = null;

	public ViewPanel(StvFrame stvFrame) {
		super(stvFrame);
		renderer = new ViewRenderer32bit(this, viewSettings);
		backgroundImage = GhcImageLoader.getImage(GhcImageLoader.BLUE_SKY);
	}

	public void addModeManager() {
		boolean enableZoom = true;
		boolean enableTracking = true;
		boolean enableEditing = true;
		ModeManager modeManager = new ModeManager();
		modeManager.add(this);
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

	public void refresh() {
		update(false, false, false);
		repaint();
	}
	public void update(boolean initialized, boolean traceBufferChanged, boolean settingsChanged) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		if (traceBuffer == null) {
			return;
		}

		resetTraceScaling();
		resetLeftRightMargins();
		if (viewSettings.isVADisplay || viewSettings.wiggleType == StvConst.WIGGLE_TYPE_CUBIC) {
			renderer.resetSplineFields();
		}

		resetScreenBuffer();
		resetViewPositions();
		resetTimeLines();
		if (initialized) {
			addModeManager();
			resetScrollBar();
		}
		isFullRepaint = true;
		isScrolling = false;
	}

	public int getTraceIndex(int xpos, int ypos) {
		double traceDouble = 0;
		int trace = 0;
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			traceDouble = this.xView2Trace(xpos);
		} else {
			traceDouble = this.yView2TracePlotDirHorz(ypos);
		}
		trace = (int) (traceDouble + 0.5f);
		return trace;
	}

	public int clipTraceIndex(int trace) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int numTraces = traceBuffer.numTraces();
		if (trace < 0) {
			trace = 0;
		} else if (trace > numTraces - 1) {
			trace = numTraces - 1;
		}
		return trace;
	}

	public int getSampleIndex(int xpos, int ypos) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		double sampleDouble = 0;
		int sample = 0;
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			sampleDouble = this.yView2Model(ypos);
		} else {
			sampleDouble = this.xView2ModelPlotDirHorz(xpos);
		}
		sample = (int) (sampleDouble + 0.5f);
		if (sample < 0) {
			sample = 0;
		} else if (sample > numSamples - 1) {
			sample = numSamples - 1;
		}

		return sample;
	}

	public int clipSampleIndex(int sample) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		if (sample < 0) {
			sample = 0;
		} else if (sample > numSamples - 1) {
			sample = numSamples - 1;
		}

		return sample;
	}

	public float getSampleValue(int traceIndex, int sampleIndex) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int sIndex = clipSampleIndex(sampleIndex);
		int tIndex = clipTraceIndex(traceIndex);
		float[] samples = traceBuffer.samples(tIndex);
		return samples[sIndex];
	}

	public SampleInfo getSampleInfo(int xpos, int ypos) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int numTraces = traceBuffer.numTraces();
		int numSamples = traceBuffer.numSamples();
		SampleInfo info = new SampleInfo();
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			info.sampleDouble = this.yView2Model(ypos);
			info.traceDouble = this.xView2Trace(xpos);
		} else {
			info.sampleDouble = this.xView2ModelPlotDirHorz(xpos);
			info.traceDouble = this.yView2TracePlotDirHorz(ypos);
		}
		info.sample = (int) (info.sampleDouble + 0.5f);
		info.time = info.sampleDouble * getSampleInt() * 0.001;
		info.trace = (int) (info.traceDouble + 0.5f);
		if (info.trace < 0) {
			info.trace = 0;
		} else if (info.trace > numTraces - 1) {
			info.trace = numTraces - 1;
		}
		if (info.sample >= 0 && info.sample <= numSamples - 1) {
			float[] samples = traceBuffer.samples(info.trace);
			info.amplitude = samples[info.sample];
		}
		return info;
	}

	private void paintInstructionPanel(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle rect = getVisibleRect();

		int width = rect.width;
		int height = rect.height;

		if (backgroundImage == null) {
			backgroundImage = GhcImageLoader.getImage(GhcImageLoader.BLUE_SKY);
		}

		g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, height);
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.black);

		FontMetrics metrics = g2.getFontMetrics(g2.getFont());
		String text = "File >> Open or New ... to load data";
		int labelWidth = metrics.stringWidth(text);
		int labelHeight = metrics.getHeight();
		int xpos = (width - labelWidth) / 2;
		int ypos = (height - labelHeight) / 2;
		// g2.drawString(text, xpos, ypos);

		g2.drawImage(backgroundImage, 0, 0, this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		if (traceBuffer == null) {
			paintInstructionPanel(g);
			return;
		}

		boolean snapShot_isScrolling = false;
		boolean isRepainted = repaintStep1(snapShot_isScrolling);

		Graphics2D g2 = (Graphics2D) g;
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			g2.drawImage(bitmap, 0, 0, this);
		} else {
			g2.rotate(-Math.PI / 2);
			g2.translate(-imageWidth, 0);
			g2.drawImage(bitmap, 0, 0, this);
			g2.translate(imageWidth, 0);
			g2.rotate(Math.PI / 2);
		}
	}

	/**
	 * Repaint method, step 1 Computes the actual area that needs repainting, taking into
	 * account the current scroll Passes on repainting to step2, with given min/max pixels
	 * values to repaint
	 * 
	 * @param isScrolling Pass true if scrolling is currently taking place.
	 * @return true if repaint action was successful, false if not
	 */
	protected boolean repaintStep1(boolean isScrolling) {
		Rectangle rectVisible = getVisibleRect();
		if (rectVisible.width == 0 && rectVisible.height == 0) {
			return false;
		}

		renderer.repaintStep2(marginTopBottom, rectVisible.height - 1, 0, rectVisible.width - 1);

		return true;
	}

	public void zoom(float zoomVert, float zoomHorz) {
		Rectangle rect = getVisibleRect();
		float traceIndexCentre = xView2Trace(rect.width / 2);
		float sampleIndexCentre = yView2Model(rect.height / 2);
		zoom(zoomVert, zoomHorz, traceIndexCentre, sampleIndexCentre);
	}

	public void zoom(int zoomType, int zoomMode) {
		stvFrame.getStvPanel().getDisplayPanel().zoom(zoomType, zoomMode);
	}

	public void zoom(float zoomVert, float zoomHorz, float traceIndexCentre, float sampleIndexCentre) {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		if (traceBuffer == null) {
			return;
		}
		Rectangle rect = getVisibleRect();
		if (zoomVert != viewSettings.zoomVert) {
			viewSettings.zoomVert = zoomVert;

			int yViewCentreNew = (int) (yModel2View(sampleIndexCentre) + 0.5f) + viewPositionVert;
			prevViewPositionVert = viewPositionVert;
			viewPositionVert = yViewCentreNew - rect.height / 2;
			if (viewPositionVert < 0) {
				viewPositionVert = 0;
			}
			resetTimeLines();
		}
		if (zoomHorz != viewSettings.zoomHorz) {
			viewSettings.zoomHorz = zoomHorz;
			resetLeftRightMargins();
			int xViewCentreNew = (int) (xModel2View(traceIndexCentre) + 0.5f) + viewPositionHorz;
			prevViewPositionHorz = viewPositionHorz;
			viewPositionHorz = xViewCentreNew - rect.width / 2;
			if (viewPositionHorz < 0) {
				viewPositionHorz = 0;
			}
		}

		resetScreenBuffer();
		resetViewPositions();
		// _isFullRepaint = true;
		// if (_settings.isVADisplay || _settings.wiggleType ==
		// SeisDispSettingsConst.WIGGLE_TYPE_CUBIC)
		// _renderer.resetSplineFields();
		repaint(); // Only need repaint when using DataBufferInt method
		resetScrollBar();
		fireEventSettingsChanged();
		fireEventSizeChanged();
	}

	public void zoomHorz(float zoomLevel) {
		if (zoomLevel <= 0.0) {
			return;
		}
		zoom(viewSettings.zoomVert, zoomLevel);
	}

	public void zoomVert(float zoomLevel) {
		if (zoomLevel <= 0.0) {
			return;
		}
		zoom(zoomLevel, viewSettings.zoomHorz);
	}

	public void paintTrackingLine(int mouseX, int mouseY) {
		stvFrame.getStvPanel().getDisplayPanel().getSideLabelHorzPanel().paintTrackingLine(mouseX, mouseY);
		stvFrame.getStvPanel().getDisplayPanel().getSideLabelVertPanel().paintTrackingLine(mouseX, mouseY);
	}
}
