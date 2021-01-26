package com.ghc.app.stv.display;

import java.awt.Color;
import java.awt.image.DataBuffer;

import lombok.Getter;
import lombok.Setter;

import com.ghc.app.general.StvConst;
import com.ghc.app.math.Spline;
import com.ghc.app.seis.ISeismicTraceBuffer;

/**
 * Renderer for seismic display.
 * This class contains methods performing 2D rendering of seismic traces.
 * 
 * Sub-classes are 32bit and 8bit realisations of this class, where color pixels are represented
 * by either 32bit integer values, and 8bit standard indexed bytes, respectively.
 */

// @Getter
// @Setter
public abstract class ViewRenderer implements ISeisViewListener {
	/// Full height of seismic view area
	protected int imageHeight;
	/// Full width of seismic view area
	protected int imageWidth;
	/// Seismic display settings
	protected ViewSettings viewSettings;
	protected ViewPanel viewPanel;

	/// Derivatives of seismic amplitudes, required for spline interpolation
	protected float[][] vA_yDerivative2Values = null;
	/// Flag indicating that derivatives are still being computed
	protected boolean isComputingDerivatives;

	/// Constant scalar to apply to each trace. This is usually 1.
	/// When using 'trace' scaling, the trace scalar becomes the inverse of the mean or
	/// max of each individual trace.
	protected float[] traceScalar = null;
	/// The 'wiggle central amplitude' is usually 0; this is where the wiggle 'zeor' line
	/// is plotted.
	/// When using 'range' scaling, the wiggle central amplitude becomes the mean value of
	/// the specified min/max range values.
	protected float wiggleCentreAmplitude = 0.0f;
	/// General display scalar. This local field is for convenience. The display scalar is
	/// also stored in the display settings (_settings)
	protected float ampScalarGlobal = 1.0f;
	/// Trace 'step'. Only every _stepTrace'th trace is drawn to the screen.
	/// This is to enhance performance when the display is squeezed below one pixel/trace.
	protected int stepTrace = 1;

	protected float bIAS = 0;

	@Getter
	@Setter
	protected int selectedTraceIndex = -1;

	public ViewRenderer(ViewPanel viewPanel, ViewSettings viewSettings) {
		this.viewPanel = viewPanel;
		this.viewSettings = viewSettings;

		vA_yDerivative2Values = null;
		isComputingDerivatives = false;

		changedSettings(viewSettings);
	}

	public void resetTraceScaling(float ampScalarGlobal, float wiggleCentreAmplitude, float[] traceScalar) {
		this.ampScalarGlobal = ampScalarGlobal;
		this.wiggleCentreAmplitude = wiggleCentreAmplitude;
		this.traceScalar = traceScalar;
	}

	// -----------------------------------------------------

	// public void reset(ISeismicTraceBuffer traceBuffer, double sampleInt) {
	// vA_yDerivative2Values = null;
	// if (viewSettings.isVADisplay || viewSettings.wiggleType ==
	// SeisDispSettingsConst.WIGGLE_TYPE_CUBIC) {
	// resetSplineFields();
	// }
	// }

	protected void resetSplineFields(boolean doReset) {
		if (vA_yDerivative2Values == null && doReset)
			resetSplineFields();

		if (viewSettings.isVADisplay || viewSettings.wiggleType == StvConst.WIGGLE_TYPE_CUBIC) {
			resetSplineFields();
		}
	}
	protected void resetSplineFields() {
		ISeismicTraceBuffer traceBuffer = viewPanel.getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();
		isComputingDerivatives = true;
		if (vA_yDerivative2Values == null || numTraces != vA_yDerivative2Values.length || numSamples != vA_yDerivative2Values[0].length) {
			vA_yDerivative2Values = new float[numTraces][numSamples];
		}
		float[] va_ypos = new float[numSamples];
		for (int isamp = 0; isamp < numSamples; isamp++) {
			va_ypos[isamp] = isamp;
		}
		for (int traceIndex = 0; traceIndex < numTraces; traceIndex++) {
			float[] samples = traceBuffer.samples(traceIndex);
			Spline.spline(va_ypos, samples, 1.0e30f, 1.0e30f, vA_yDerivative2Values[traceIndex]);
		}
		isComputingDerivatives = false;
	}

	public void changedSettings(ViewSettings ds) {
		boolean resetSpline = (vA_yDerivative2Values == null &&
				(ds.isVADisplay != viewSettings.isVADisplay || ds.wiggleType != viewSettings.wiggleType) &&
				(ds.isVADisplay || ds.wiggleType == StvConst.WIGGLE_TYPE_CUBIC));

		viewSettings = ds;

		if (resetSpline) {
			resetSplineFields();
		}
	}

	protected abstract boolean resetScreenBuffer(int width, int height, DataBuffer dataBuffer);

	protected abstract void copyBufferHorz(int width, int height, int xfrom, int xto);

	protected abstract void copyBufferVert(int width, int height, int yfrom, int yto);

	/**
	 * Repaint method, step 2 Clear or repaint background with VA display, then repaint
	 * wiggles on top Finally, repaint time lines
	 * 
	 * @param yMin minimum y view (pixel) value to repaint
	 * @param yMax maximum y view (pixel) value to repaint
	 * @param xMin minimum x view (pixel) value to repaint
	 * @param xMax maximum x view (pixel) value to repaint
	 */
	protected abstract void repaintStep2(int yMin, int yMax, int xMin, int xMax);

	/**
	 * Repaint time lines over given screen area
	 * 
	 * @param yMin minimum/maximum pixel values to repaint
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected abstract void repaintLines(int yMin, int yMax, int xMin, int xMax);

	/**
	 * Repaint wiggle display, for given trace range and y pixel range Basic concepts: a)
	 * Set pixel integer array. Do not use Java shapes, they're much too slow. Do not use
	 * bitmap, this is also too slow. b) Paint wiggles
	 * 
	 * @param yMin
	 * @param yMax
	 * @param minTrace
	 * @param maxTrace
	 * @param colorTrace
	 */
	protected void repaintWiggleTrace(int yMin, int yMax, int minTrace, int maxTrace, Color colorTrace) {
		repaintWiggleTrace(yMin, yMax, minTrace, maxTrace, colorTrace, true);
	}

	protected abstract void repaintWiggleTrace(int yMin, int yMax, int minTrace, int maxTrace, Color colorTrace, boolean doFill);

	/**
	 * Repaint seismic display with discrete colours (nearest sample value tp screen
	 * pixel), without any interpolation Variable density plot
	 * 
	 * @param yMin Minimum/maximum x pixel values to repaint
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected void repaintVADiscrete(int yMin, int yMax, int xMin, int xMax) {
		repaintVADiscrete(yMin, yMax, xMin, xMax, false);
	}

	protected abstract void repaintVADiscrete(int yMin, int yMax, int xMin, int xMax, boolean doHighlight);

	/**
	 * Repaint seismic display using linear or 1D spline interpolation, without
	 * interpolation in the horizontal direction Variable density plot
	 * 
	 * @param yMin
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected abstract void repaintVAVertical(int yMin, int yMax, int xMin, int xMax);

	/**
	 * Repaint seismic display using 2D spline interpolation Variable density plot
	 * 
	 * @param yMin
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected abstract void repaintVA2DSpline(int yMin, int yMax, int xMin, int xMax);

	public void horzScrollChanged(int scrollValue) {
	}

	public void vertScrollChanged(int scrollValue) {
	}

	public void sizeChanged(java.awt.Dimension size) {
	}
}
