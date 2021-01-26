package com.ghc.app.stv.display;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import lombok.Getter;
import lombok.Setter;

import com.ghc.app.general.ColorMap;
import com.ghc.app.general.MouseModes;
import com.ghc.app.general.StvConst;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.StvFrame;


public class ViewPanelBase extends JPanel {
	@Getter
	protected StvFrame stvFrame = null;
	protected ViewSettings viewSettings = null;
	@Getter
	protected ViewRenderer renderer;

	protected ArrayList<ISeisViewListener> seisViewListeners;

	public final int INSET_DEFAULT = 10;
	public final double EPSILON = 10.0e-30;

	public final int DEFAULT_INC_PIXELS = 50;
	public final double DEFAULT_INC_MS[] = { 1, 2, 2.5, 5, 10 };
	public final int DEFAULT_MAJOR_MS[] = { 10, 10, 50, 50, 100 };

	@Getter
	protected int marginLeftRight = 0;
	// Width of inset (margin on left and right hand side)
	protected int marginTopBottom = 0;
	// Height of inset (margin at top and bottom)

	// Scrolling = View position of seismic display
	/// Current view position ( = scroll position)
	protected int viewPositionVert = 0;
	@Getter
	protected int viewPositionHorz = 100;
	protected int prevViewPositionVert = 0;
	protected int prevViewPositionHorz = 0;
	protected int nextViewPositionVert = -1;
	protected int nextViewPositionHorz = -1;
	protected boolean isScrolling = false;

	protected int imageWidth; // Width (in pixels) of bitmap image
	protected int imageHeight; // Height (in pixels) of bitmap image
	protected boolean isFullRepaint = false;

	// Highlighting
	protected int _highlightTrace = 0;
	protected boolean _isHighlightOn = false;
	protected boolean _showCrosshair = false;
	protected boolean _snapCrosshair = false;
	protected boolean _isCrosshairPainted = false;
	protected Point _crosshairPosition = new Point(-1, -1);

	/// Constant scalar to apply to each trace. This is usually 1.
	protected float ampScalarGlobal = 1.0f;
	/// When using 'trace' scaling, the trace scalar becomes the inverse of the mean or
	/// max of each individual trace.
	protected float[] traceScalar = null;
	/// The 'wiggle central amplitude' is usually 0; this is where the wiggle 'zeor' line
	/// is plotted.
	/// When using 'range' scaling, the wiggle central amplitude becomes the mean value of
	/// the specified min/max range values.
	protected float wiggleCentreAmplitude = 0;
	/// General display scalar. This local field is for convenience. The display scalar is
	/// also stored in the display settings (_settings)
	// protected float _dispScalar;
	/// Trace 'step'. Only every _stepTrace'th trace is drawn to the screen.
	/// This is to enhance performance when the display is squeezed below one pixel/trace.
	protected int _stepTrace = 1;

	// Data buffer
	protected BufferedImage bitmap = null;
	// protected Image volatileBitmap = null;

	protected ArrayList<IPanningListener> panningListeners;

	protected int colorBitType = ColorMap.COLOR_MAP_TYPE_32BIT;
	protected float logScaleRatio = 1.0f;
	/// Switch between horizontal & verticall scrolling
	private boolean _switchScroll = true;
	/// Mouse mode
	@Getter
	@Setter
	private int mouseMode = MouseModes.NO_MODE;

	protected ViewPanelBase(StvFrame stvFrame) {
		super();
		this.stvFrame = stvFrame;
		viewSettings = stvFrame.getTraceContext().getViewSettings();

		seisViewListeners = new ArrayList<ISeisViewListener>(1);
		panningListeners = new ArrayList<IPanningListener>(1);
	}

	protected double getSampleInt() {
		return stvFrame.getSampleInt();
	}

	protected ISeismicTraceBuffer getTraceBuffer() {
		return stvFrame.getTraceBuffer();
	}

	/**
	 * Call when vertical scroll value has been reset. Only repaint if scroll value really
	 * has changed, and when there is currently no repainting in progress.
	 * 
	 * @param scrollValue New vertical scroll value
	 */
	protected synchronized void resetViewPositionVert(int scrollValue) {
		if (scrollValue != viewPositionVert) {
			nextViewPositionVert = -1;
			prevViewPositionVert = viewPositionVert;
			viewPositionVert = scrollValue;
			repaint();
			fireEventVertScrollChanged(scrollValue);
		} else {
			nextViewPositionVert = scrollValue;
		}
	}

	/**
	 * Call when horizontal scroll value has been reset. Only repaint if scroll value
	 * really has changed, and when there is currently no repainting in progress.
	 * 
	 * @param scrollValue New horizontal scroll value
	 */
	protected synchronized void resetViewPositionHorz(int scrollValue) {
		if (scrollValue != viewPositionHorz && !isScrolling) {
			nextViewPositionHorz = -1;
			prevViewPositionHorz = viewPositionHorz;
			viewPositionHorz = scrollValue;
			repaint();
			fireEventHorzScrollChanged(scrollValue);
		} else {
			nextViewPositionHorz = scrollValue;
		}
	}

	protected void resetLeftRightMargins() {
		if (viewSettings.isVADisplay) {
			marginLeftRight = (int) (xModel2View(0.5f) - xModel2View(0, 0)) - 1;
		} else if (viewSettings.doTraceClipping) {
			marginLeftRight = (int) (xModel2View(viewSettings.traceClip) - xModel2View(0));
		} else {
			marginLeftRight = (int) (xModel2View(0.5f) - xModel2View(0, 0)) - 1;
		}
		marginLeftRight += INSET_DEFAULT;
	}

	/**
	 * Create screen buffer for seismic display Call whenever zoom level changes, or
	 * traces are added or removed from seismic buffer
	 * 
	 * @return true if creation was successful, false if display is currently not visible
	 *         (for example when panel is minimised).
	 */
	public boolean resetScreenBuffer() {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();
		// Compute absolute max x and y view pixels. Add current scroll value and margin
		// System.out.println("marginLeftRight=" + marginLeftRight + " marginTopBottom=" +
		// marginTopBottom);
		// System.out.println("viewPositionHorz=" + viewPositionHorz + "
		// viewPositionVert=" + viewPositionVert);
		int xViewMax = (int) xModel2View(numTraces - 1) + marginLeftRight + viewPositionHorz;
		int yViewMax = (int) yModel2View(numSamples - 1) + marginTopBottom + viewPositionVert;
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_HORIZONTAL) {
			int saveY = yViewMax;
			yViewMax = xViewMax;
			xViewMax = saveY;
		}
		setPreferredSize(new Dimension(xViewMax, yViewMax));
		setMinimumSize(new Dimension(xViewMax, yViewMax));
		// This will lead to a call to repaint()
		setSize(new Dimension(xViewMax, yViewMax));
		// System.out.println("xViewMax= " + xViewMax + " yViewMax= " + yViewMax);
		Rectangle rect = getVisibleRect();
		imageWidth = rect.width;
		imageHeight = rect.height;
		if (xViewMax < imageWidth) {
			imageWidth = xViewMax;
		}
		// System.out.println("imageWidth=" + imageWidth + " imageHeight=" + imageHeight);
		// System.out.println("rec.width= " + rect.width + " rec.height= " + rect.height);

		if (imageHeight == 0 || imageWidth == 0) {
			return false;
		}

		// volatileBitmap = createImage(imageWidth, imageHeight);
		if (colorBitType == ColorMap.COLOR_MAP_TYPE_32BIT) {
			bitmap = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		} else {
			bitmap = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_INDEXED);
		}
		renderer.resetScreenBuffer(imageWidth, imageHeight, bitmap.getRaster().getDataBuffer());

		if (viewSettings.isLogScale) {
			float maxView = (traceBuffer.numSamples() * viewSettings.zoomVert);
			float maxLogView = (float) Math.log10(((traceBuffer.numSamples()) * viewSettings.zoomVert));
			logScaleRatio = maxView / maxLogView;
		}
		return true;
	}

	protected void resetTimeLines() {
		if (viewSettings.isTimeLinesAuto) {
			float pixelsPerMS = viewSettings.zoomVert / (float) getSampleInt();
			float minorIncNew = DEFAULT_INC_PIXELS / pixelsPerMS;

			if (minorIncNew < getSampleInt()) {
				viewSettings.timeLineMinorInc = (getSampleInt());
				viewSettings.timeLineMajorInc = (10 * viewSettings.timeLineMinorInc);
			} else {
				double powerOf10 = Math.floor(Math.log10(minorIncNew));
				double incReduced = minorIncNew / Math.pow(10, powerOf10);

				for (int i = 0; i < DEFAULT_INC_MS.length; i++) {
					if (incReduced < DEFAULT_INC_MS[i]) {
						viewSettings.timeLineMinorInc = (DEFAULT_INC_MS[i] * Math.pow(10, powerOf10));
						viewSettings.timeLineMajorInc = (DEFAULT_MAJOR_MS[i] * Math.pow(10, powerOf10));
						break;
					}
				}
			}
			// System.out.println("Pixels111: " + pixelsPerMS + " " + minorIncNew + " " +
			// _settings.timeLineMinorInc + " " + _settings.timeLineMajorInc);
		}
	}
	protected void resetTraceScaling() {
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		int numTraces = traceBuffer.numTraces();
		if (traceScalar == null || traceScalar.length != numTraces) {
			traceScalar = new float[numTraces];
		}

		wiggleCentreAmplitude = 0.0f;
		if (viewSettings.scaleType == StvConst.SCALE_TYPE_SCALAR) {
			for (int itrc = 0; itrc < numTraces; itrc++) {
				traceScalar[itrc] = 1.0f;
			}
			ampScalarGlobal = viewSettings.dispScalar;
			viewSettings.vaColorMap.setScalar(ampScalarGlobal);
			viewSettings.wiggleColorMap.setScalar(ampScalarGlobal);
		} else if (viewSettings.scaleType == StvConst.SCALE_TYPE_TRACE) {
			if (viewSettings.traceScaling == StvConst.TRACE_SCALING_AVERAGE) {
				for (int itrc = 0; itrc < numTraces; itrc++) {
					float amp = traceBuffer.meanAmplitude(itrc);
					if (amp != 0.0f)
						traceScalar[itrc] = 0.1f / amp;
					else
						traceScalar[itrc] = 1.0f;
				}
			} else {
				for (int itrc = 0; itrc < numTraces; itrc++) {
					float a1 = Math.abs(traceBuffer.minAmplitude(itrc));
					float a2 = Math.abs(traceBuffer.maxAmplitude(itrc));
					float amp = Math.max(a1, a2);
					if (amp > EPSILON)
						traceScalar[itrc] = 1.0f / amp;
					else
						traceScalar[itrc] = 1.0f;
				}
			}
			ampScalarGlobal = viewSettings.fullTraceScalar;
			viewSettings.vaColorMap.setScalar(ampScalarGlobal);
			viewSettings.wiggleColorMap.setScalar(ampScalarGlobal);
		} else {
			wiggleCentreAmplitude = 0.5f * (viewSettings.minValue + viewSettings.maxValue);
			float amp = Math.max(Math.abs(viewSettings.minValue), Math.abs(viewSettings.maxValue));
			if (amp > EPSILON) {
				ampScalarGlobal = 1.0f / amp;
			} else {
				ampScalarGlobal = 1.0f;
			}
			viewSettings.vaColorMap.setMinMax(viewSettings.minValue, viewSettings.maxValue);
			viewSettings.wiggleColorMap.setMinMax(viewSettings.minValue, viewSettings.maxValue);
		}
		renderer.resetTraceScaling(ampScalarGlobal, wiggleCentreAmplitude, traceScalar);
	}

	/**
	 * Convert model value/trace index to pixel value on screen
	 * 
	 * @param amplitude Amplitude of trace sample
	 * @param traceIndex Trace index
	 * @return View X coordinate, relative to current position of view in terms of
	 *         scrolling. For example, a negative xView means x coordinate is outside of
	 *         visible view area ('left' of view area)
	 */
	public float xModel2View(float amplitude, int traceIndex) {
		float xView = (int) (traceIndex * viewSettings.zoomHorz + 0.5f) - viewPositionHorz + marginLeftRight +
				amplitude * ampScalarGlobal * traceScalar[traceIndex] * viewSettings.zoomHorz;
		return xView;
	}

	public float xModel2View(float traceIndex) {
		float xView = (int) (traceIndex * viewSettings.zoomHorz + 0.5f) - viewPositionHorz + marginLeftRight;
		return xView;
	}

	/**
	 * Convert pixel value (view x coordinate) to trace index.
	 * 
	 * @param xView Absolute view x coordinate, which is independent of the current scroll
	 *            value (as if full seismic section was visible).
	 * @return trace index at this view coordinate.
	 */
	public float xView2Trace(int xView) {
		return ((float) (xView + viewPositionHorz - marginLeftRight) / (float) viewSettings.zoomHorz);
	}

	/**
	 * Convert model value/sample index to pixel value on screen
	 * 
	 * @param sampleIndex Sample index, starting with 0
	 * @return View Y coordinate, relative to current position of view in terms of
	 *         scrolling. For example, a negative yView means y coordinate is outside of
	 *         visible view area ('above' view area)
	 */
	public float yModel2View(float sampleIndex) {
		if (!viewSettings.isLogScale) {
			float ypixView = (sampleIndex * viewSettings.zoomVert) - viewPositionVert + marginTopBottom;
			return ypixView;
		} else {
			float ypixView = logScaleRatio * (float) Math.log10((sampleIndex + 1) * viewSettings.zoomVert)
					- viewPositionVert + marginTopBottom;
			// System.out.println("yModel2View: " + ypixView + " " + sampleIndex + " --- "
			// + maxView + " " + maxLogView);
			return ypixView;
		}
	}

	/**
	 * Convert pixel value to model value. Convert y pixel to sample index.
	 * 
	 * @param yView Absolute view coordinate, independent of scroll value (as if full
	 *            seismic section was visible)
	 * @return
	 */
	public float yView2Model(int yView) {
		if (viewSettings.isLogScale) {
			float yModel = (float) (Math.pow(10, (yView + viewPositionVert - marginTopBottom) / logScaleRatio)
					/ (float) viewSettings.zoomVert) - 1;
			// System.out.println("yView2Model: " + yView + " " + yModel + " --- " +
			// yModel2View(yModel) );
			return yModel;
		} else {
			float yModel = (float) (yView + viewPositionVert - marginTopBottom) / (float) viewSettings.zoomVert;
			return yModel;
		}
	}

	public float yView2TracePlotDirHorz(int yView) {
		return ((float) (imageWidth - yView + viewPositionHorz - marginLeftRight) / (float) viewSettings.zoomHorz);
	}

	public float xView2ModelPlotDirHorz(int xView) {
		float yModel;
		yModel = (float) (xView + viewPositionVert - marginTopBottom) / (float) viewSettings.zoomVert;
		return yModel;
	}
	protected void resetViewPositions() {
		Dimension size = getPreferredSize();
		Rectangle rect = getVisibleRect();
		int maxViewPosVert = size.height - rect.height;
		int maxViewPosHorz = size.width - rect.width;
		if (viewPositionVert > maxViewPosVert) {
			viewPositionVert = maxViewPosVert;
		}
		if (viewPositionHorz > maxViewPosHorz) {
			viewPositionHorz = maxViewPosHorz;
		}

		prevViewPositionVert = viewPositionVert;
		prevViewPositionHorz = viewPositionHorz;
	}

	protected void resetScrollBar() {
		Dimension size = getPreferredSize();
		Rectangle rect = getVisibleRect();
		
		DisplayPanel displayPanel = stvFrame.getStvPanel().getDisplayPanel();
		JScrollBar scrollBar = displayPanel.getScrollBarVert();
		double r = (1.0 * size.height) / rect.height;
		if (r <= 1.0) {
			scrollBar.setMaximum(0);
			scrollBar.setValue(0);
		} else {
			scrollBar.setMaximum(size.height);
			int extend = rect.height - 10;
			scrollBar.setVisibleAmount(extend);
			scrollBar.setBlockIncrement(extend);
			scrollBar.setValue(viewPositionVert);
		}
		scrollBar.updateUI();
		
		scrollBar = displayPanel.getScrollBarHorz();
		r = (1.0 * size.width) / rect.width;
		if (r <= 1.0) {
			scrollBar.setMaximum(0);
			scrollBar.setValue(0);
		} else {
			scrollBar.setMaximum(size.width);
			int extend = rect.width - 10;
			scrollBar.setVisibleAmount(extend);
			scrollBar.setBlockIncrement(extend);
			scrollBar.setValue(viewPositionHorz);
		}
		scrollBar.updateUI();
	}

	public void addSeisViewListener(ISeisViewListener listener) {
		for (int i = 0; i < seisViewListeners.size(); i++) {
			if (seisViewListeners.get(i).equals(listener)) {
				return;
			}
		}
		seisViewListeners.add(listener);
	}

	public void removeSeisViewListener(ISeisViewListener listener) {
		for (int i = 0; i < seisViewListeners.size(); i++) {
			if (seisViewListeners.get(i).equals(listener)) {
				seisViewListeners.remove(i);
				break;
			}
		}
	}

	public void addPanningListener(IPanningListener listener) {
		if (listener != null && !panningListeners.contains(listener)) {
			panningListeners.add(listener);
		}
	}

	public void firePanningEvent(int dx, int dy) {
		for (int i = 0; i < panningListeners.size(); i++) {
			IPanningListener target = panningListeners.get(i);
			target.hasPanned(dx, dy);
		}
	}

	public void fireEventSettingsChanged() {
		for (int i = 0; i < seisViewListeners.size(); i++) {
			seisViewListeners.get(i).settingsChanged(viewSettings);
		}
	}

	public void fireEventVertScrollChanged(int scrollValue) {
		for (int i = 0; i < seisViewListeners.size(); i++) {
			seisViewListeners.get(i).vertScrollChanged(scrollValue);
		}
	}

	public void fireEventHorzScrollChanged(int scrollValue) {
		for (int i = 0; i < seisViewListeners.size(); i++) {
			seisViewListeners.get(i).horzScrollChanged(scrollValue);
		}
	}

	public void fireEventSizeChanged() {
		for (int i = 0; i < seisViewListeners.size(); i++) {
			seisViewListeners.get(i).sizeChanged(getPreferredSize());
		}
	}

}
