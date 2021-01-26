package com.ghc.app.stv.display;

import java.awt.Color;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

import com.ghc.app.general.StvConst;
import com.ghc.app.math.Spline;
import com.ghc.app.seis.ISeismicTraceBuffer;

/**
 * 32bit renderer for seismic display.
 * 
 * Color pixels are represented by 32bit integer values.
 */
public class ViewRenderer32bit extends ViewRenderer {
	private int[] buffer;
	private int COLOR_BLACK_32BIT = 0;

	public ViewRenderer32bit(ViewPanel viewPanel, ViewSettings ds) {
		super(viewPanel, ds);
	}

	protected boolean resetScreenBuffer(int width, int height, DataBuffer dataBuffer) {
		imageWidth = width;
		imageHeight = height;
		buffer = ((DataBufferInt) dataBuffer).getData();
		return true;
	}

	protected void copyBufferHorz(int width, int height, int xfrom, int xto) {
		if (xto < xfrom) {
			for (int iy = 0; iy < imageHeight; iy++) {
				int indexRow = iy * imageWidth;
				for (int ix = 0; ix < width; ix++) {
					buffer[ix + xto + indexRow] = buffer[ix + xfrom + indexRow];
				}
			}
		} else {
			for (int iy = 0; iy < imageHeight; iy++) {
				int indexRow = iy * imageWidth;
				for (int ix = width - 1; ix >= 0; ix--) {
					buffer[ix + xto + indexRow] = buffer[ix + xfrom + indexRow];
				}
			}
		}
	}

	protected void copyBufferVert(int width, int height, int yfrom, int yto) {
		if (yto < yfrom) {
			for (int ix = 0; ix < imageWidth; ix++) {
				for (int iy = 0; iy < height; iy++) {
					buffer[ix + imageWidth * (iy + yto)] = buffer[ix + imageWidth * (iy + yfrom)];
				}
			}
		} else {
			for (int ix = 0; ix < imageWidth; ix++) {
				for (int iy = height - 1; iy >= 0; iy--) {
					buffer[ix + imageWidth * (iy + yto)] = buffer[ix + imageWidth * (iy + yfrom)];
				}
			}
		}
	}

	/**
	 * Repaint method, step 2 Clear or repaint background with VA display, then repaint
	 * wiggles on top Finally, repaint time lines
	 * 
	 * @param yMin minimum y view (pixel) value to repaint
	 * @param yMax maximum y view (pixel) value to repaint
	 * @param xMin minimum x view (pixel) value to repaint
	 * @param xMax maximum x view (pixel) value to repaint
	 */
	protected void repaintStep2(int yMin, int yMax, int xMin, int xMax) {
		if (isComputingDerivatives) {
			return;
		}
		// System.out.println("Paint step 2 xmin/xmax ymin/ymax " + xMin + " " + xMax + "
		// " + yMin + " " + yMax);
		// The following two statements catch the case when the painted area is smaller
		// than the visible area
		// This may happen when the seisview window is smaller than the seispane
		// window(=viewport)
		if (xMax > imageWidth - 1) {
			xMax = imageWidth - 1;
		}
		if (yMax > imageHeight - 1) {
			yMax = imageHeight - 1;
		}

		stepTrace = 1;
		if (viewSettings.zoomHorz < 1.0) {
			stepTrace = (int) (1.0 / viewSettings.zoomHorz);
		}

		ISeismicTraceBuffer traceBuffer = viewPanel.getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();

		int colorBackgroundRGB = Color.white.getRGB();
		// (1) Zero all pixels in given rectangle
		if (!viewSettings.isVADisplay) {
			for (int iy = yMin; iy <= yMax; iy++) {
				int row = iy * imageWidth;
				for (int ix = xMin; ix <= xMax; ix++) {
					buffer[ix + row] = colorBackgroundRGB;
				}
			}
		}
		// (1) Alternatively to zeroing , paint VA display as background.
		else {

			// Only zero out left/right margin:
			int nLeftMargin = viewPanel.getMarginLeftRight() - viewPanel.getViewPositionHorz();
			for (int ix = 0; ix <= nLeftMargin; ix++) {
				for (int iy = yMin; iy <= yMax; iy++) {
					int row = iy * imageWidth;
					buffer[ix + row] = colorBackgroundRGB;
					// Exception in thread "AWT-EventQueue-0"
					// java.lang.ArrayIndexOutOfBoundsException: 3408
				}
			}
			float pos = viewPanel.xModel2View(0.0f, numTraces - 1);
			// float pos = viewPanel.xModel2View(0.0f, viewPanel._numTraces - 1);
			for (int ix = Math.max((int) pos, xMin); ix <= xMax; ix++) {
				for (int iy = yMin; iy <= yMax; iy++) {
					int row = iy * imageWidth;
					buffer[ix + row] = colorBackgroundRGB;
				}
			}
			// This is not correct yet!
			int xMinVA = Math.max(xMin, nLeftMargin);
			int xMaxVA = Math.min(xMax, (int) pos);
			switch (viewSettings.vaType) {
				case StvConst.VA_TYPE_DISCRETE:
					repaintVADiscrete(yMin, yMax, xMin, xMax);
					break;
				case StvConst.VA_TYPE_VERTICAL:
					repaintVAVertical(yMin, yMax, xMin, xMax);
					break;
				case StvConst.VA_TYPE_2DSPLINE:
					repaintVA2DSpline(yMin, yMax, xMinVA, xMaxVA);
					break;
			}
		}
		// (2) Paint wiggle display
		if (viewSettings.showWiggle || viewSettings.isNegFill || viewSettings.isPosFill) {
			// Determine min/max trace that contribute to the given rectangle
			int minTrace = (int) viewPanel.xView2Trace(xMin) - (int) (viewSettings.traceClip + 0.5f) - 1;
			int maxTrace = (int) viewPanel.xView2Trace(xMax) + (int) (viewSettings.traceClip + 0.5f) + 1;
			minTrace = Math.min(Math.max(minTrace, 0), numTraces - 1);
			maxTrace = Math.min(Math.max(maxTrace, 0), numTraces - 1);

			repaintWiggleTrace(yMin, yMax, minTrace, maxTrace, viewSettings.wiggleColor);
		}

		// (3) Paint time lines (or frequency or other domain...)
		if (viewSettings.showTimeLines) {
			repaintLines(yMin, yMax, xMin, xMax);
		}
	}

	/**
	 * Repaint time lines over given screen area
	 * 
	 * @param yMin minimum/maximum pixel values to repaint
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected void repaintLines(int yMin, int yMax, int xMin, int xMax) {
		if (viewPanel.getTraceBuffer() == null) {
			return;
		}

		double sampleInt = viewPanel.getSampleInt();
		int sampMinorInc = (int) (viewSettings.timeLineMinorInc / sampleInt + 0.5f);
		int sampMajorInc = (int) (viewSettings.timeLineMajorInc / sampleInt + 0.5f);
		if (sampMinorInc <= 0) {
			sampMinorInc = 1;
		}
		if (sampMajorInc <= 0) {
			sampMajorInc = 1;
		}

		float sampleIndex = viewPanel.yView2Model(yMin);
		int minSamp = (int) sampleIndex;
		sampleIndex = viewPanel.yView2Model(yMax);
		int maxSamp = (int) sampleIndex + 1;

		int firstSamp = ((int) (minSamp / sampMinorInc - 0.1f) + 1) * sampMinorInc;

		for (int isamp = firstSamp; isamp <= maxSamp; isamp += sampMinorInc) {
			int ypix = (int) viewPanel.yModel2View(isamp);
			if (ypix < 0) {
				continue;
			}
			int row = ypix * imageWidth;
			if (ypix <= yMax) {
				for (int ix = xMin; ix <= xMax; ix++) {
					buffer[ix + row] = COLOR_BLACK_32BIT;
				}
			}
			if (isamp % sampMajorInc == 0) {
				if (ypix < -1 || ypix > yMax - 1) {
					continue;
				}
				row += imageWidth;
				for (int ix = xMin; ix <= xMax; ix++) {
					buffer[ix + row] = COLOR_BLACK_32BIT;
				}
			}
		}
	}

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
	protected void repaintWiggleTrace(int yMin, int yMax, int minTrace, int maxTrace, Color colorTrace, boolean doFill) {
		int colorWiggle = colorTrace.getRGB();
		int colorWiggleHighlight = viewSettings.wiggleColorHighlight.getRGB();
		int colorTrace32bit = colorWiggle;
		ISeismicTraceBuffer traceBuffer = viewPanel.getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();
		// long time1 = System.currentTimeMillis(); // Comment out to compute time of
		// wiggle paint operation
		// for( int ii = 0; ii < 10; ii++ ) {

		// Trace step: Do not paint every single trace when display is squeezed to less
		// than 0.5 pixels per trace.
		// Also, artificially boost samples amplitudes by trace step factor so that
		// wiggles extend over more traces when display is squeezed.
		minTrace = (int) (minTrace / stepTrace + 0.5f) * stepTrace;
		maxTrace = (int) (maxTrace / stepTrace + 0.5f) * stepTrace;

		// Boost trace clip by stepTrace for squeezed display
		float traceClipPixels = viewPanel.xModel2View(stepTrace * viewSettings.traceClip) - viewPanel.xModel2View(0);
		int xAddum = 2000;
		// Add 2000 to avoid xModel2View to become negative. This leads
		// to problems when converting between float and integer

		if (yMin > 0) {
			yMin -= 1;
		}

		// Main loop: Repaint trace after trace. Always start with right-hand trace. This
		// will bring positive wiggle to the front.
		for (int traceIndex = maxTrace; traceIndex >= minTrace; traceIndex -= stepTrace) {
			float[] samples = traceBuffer.samples(traceIndex);
			int xViewZeroCentre = (int) (viewPanel.xModel2View(wiggleCentreAmplitude, traceIndex) + xAddum + 0.5f);
			int xViewZero = (int) (viewPanel.xModel2View(traceIndex) + xAddum + 0.5f);

			if (viewSettings.isHighlight) {
				colorTrace32bit = selectedTraceIndex == traceIndex ? colorWiggleHighlight : colorWiggle;
			} else {
				colorTrace32bit = colorWiggle;
			}
			// Set some variables upfront, for 'previous' sample index
			int isamp1 = -1;
			int isamp2 = -1;
			float value1 = 0.0f;
			float value2 = 0.0f;
			float dx = 0.0f;

			int isampPrevious = -1;
			float sampleIndex = 0;
			float value = 0;
			float valNext = 0;
			float valPrev = 0;
			float oneSixth = 1.0f / 6.0f;

			// Loop from yMin-2: First two passes are only for setting 'valuePrev' and
			// 'value'
			for (int yView = yMin - 2; yView <= yMax; yView++) {
				valPrev = value;
				value = valNext;

				sampleIndex = viewPanel.yView2Model(yView + 1);
				if (sampleIndex >= numSamples) {
					sampleIndex = numSamples - 1.001f;
				}
				if (sampleIndex <= 0) {
					sampleIndex = 0.001f;
				}
				isamp1 = (int) sampleIndex;
				isamp2 = isamp1 + 1;
				if (isamp1 != isampPrevious && isamp2 < numSamples) {
					isamp2 = (isamp2 <= numSamples - 1) ? isamp2 : isamp1;
					// Boost sample values by stepTrace when display is squeezed a lot
					value1 = stepTrace * samples[isamp1] + bIAS;
					value2 = stepTrace * samples[isamp2] + bIAS;
					dx = value2 - value1;
				}
				isampPrevious = isamp1;

				if (viewSettings.wiggleType == StvConst.WIGGLE_TYPE_LINEAR) {
					valNext = (sampleIndex - isamp1) * dx + value1;
				} else { // _settings.wiggleType == csSeisDispSettings.WIGGLE_TYPE_CUBIC
					float a = isamp2 - sampleIndex;
					float b = 1.0f - a;
					valNext = a * value1 + b * value2 + (a * (a * a - 1) * vA_yDerivative2Values[traceIndex][isamp1] +
							b * (b * b - 1) * vA_yDerivative2Values[traceIndex][isamp2]) * oneSixth;
				}
				if (yView < yMin) {
					continue;
				}
				// First two passes are only for setting 'valuePrev' and 'value'

				int xView = (int) (viewPanel.xModel2View(viewSettings.polarity * value, traceIndex) + xAddum + 0.5f);

				int xStart = xView;
				int xEnd = xView;

				// xView is the view x coordinate of the sample value at the current
				// sample position
				// Determine which line shall be plotted to make a smooth line between
				// adjacent samples
				int xMidPrev = (int) (viewPanel.xModel2View(viewSettings.polarity * (valPrev + value) / 2, traceIndex) + xAddum);
				int xMidNext = (int) (viewPanel.xModel2View(viewSettings.polarity * (valNext + value) / 2, traceIndex) + xAddum);
				if (xView > xMidPrev) {
					if (xMidNext > xView) {
						// Wiggle curve is INCREASING both before and after current pixel
						xStart = xMidPrev + 1;
						xEnd = xMidNext;
					} else {
						// Wiggle curve exhibits MAXIMUM at current pixel
						xEnd = xView;
						xStart = Math.min(xEnd, Math.min(xMidPrev + 1, xMidNext + 1));
					}
				} else {
					if (xMidNext < xView) {
						// Wiggle curve is DECREASING both before and after current pixel
						xStart = xMidNext + 1;
						xEnd = xMidPrev;
					} else {
						// Wiggle curve exhibits MINIMUM at current pixel
						xStart = xView;
						xEnd = Math.max(Math.max(xMidPrev, xMidNext), xStart);
					}
				}

				if (viewSettings.doTraceClipping) {
					if (xViewZero - xStart > traceClipPixels) {
						xStart = xViewZero - (int) traceClipPixels;
						xEnd = Math.max(xStart, xEnd);
					}
					if (xEnd - xViewZero > traceClipPixels) {
						xEnd = xViewZero + (int) traceClipPixels;
						xStart = Math.min(xStart, xEnd);
					}
				}

				// Fill wiggle with color
				if ((viewSettings.isPosFill || viewSettings.isNegFill) && doFill) {
					int colorVar = viewSettings.wiggleColorMap.getColorRGB(viewSettings.polarity * value);
					// Only fill if wiggle actually extends out from zero line (xStart >
					// xViewZeroCentre)
					if (viewSettings.isPosFill && xStart > xViewZeroCentre) {
						int color = viewSettings.wiggleColorPos.getRGB();
						if (viewSettings.isVariableColor)
							color = colorVar;
						for (int xpix = xViewZeroCentre - xAddum; xpix < xStart - xAddum; xpix++) {
							if (xpix < 0 || xpix >= imageWidth) {
								continue;
							}
							buffer[xpix + imageWidth * yView] = color;
						}
					} else if (viewSettings.isNegFill && xStart < xViewZeroCentre) {
						int color = viewSettings.wiggleColorNeg.getRGB();
						if (viewSettings.isVariableColor)
							color = colorVar;
						for (int xpix = stepTrace * xStart - xAddum + 1; xpix <= xViewZeroCentre - xAddum; xpix++) {
							if (xpix < 0 || xpix >= imageWidth) {
								continue;
							}
							buffer[xpix + imageWidth * yView] = color;
						}
					}
				} // END: Fill wiggle with color
					// Paint wiggle trace
				if (viewSettings.showWiggle) {
					for (int xpix = xStart - xAddum; xpix <= xEnd - xAddum; xpix++) {
						if (xpix < 0 || xpix >= imageWidth) {
							continue;
						}
						buffer[xpix + imageWidth * yView] = colorTrace32bit;
					}
				}
			} // END Loop over all yView

			// Plot zero line, but only if zero line is not further away from trace than
			// the trace clip value.
			// Note: If 'range' min/max values are specified, zero line may not coincide
			// with actual trace zero amplitude.
			if (viewSettings.showZeroLines && Math.abs(xViewZeroCentre - xViewZeroCentre) < traceClipPixels) {
				int xpix = xViewZeroCentre - xAddum;
				if (xpix >= 0 && xpix < imageWidth) {
					for (int yView = yMin; yView <= yMax; yView++) {
						buffer[xpix + imageWidth * yView] = colorTrace32bit;
					}
				}
			}
		}

		// } // END ii
		// long time2 = System.currentTimeMillis();
		// System.out.println("NEW Elapsed time: " + (time2-time1) + "\n" );
		// System.out.println("Number of paint operations " + this._zoomHorz + " " +
		// this._zoomVert + " -- " + " min/max sample " + minSamp + " " + maxSamp );
	}

	protected void repaintVADiscrete(int yMin, int yMax, int xMin, int xMax, boolean doHighlight) {
		ISeismicTraceBuffer traceBuffer = viewPanel.getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();

		int minTrace = (int) viewPanel.xView2Trace(xMin) - (int) (viewSettings.traceClip + 0.5f) - 1;
		int maxTrace = (int) viewPanel.xView2Trace(xMax) + (int) (viewSettings.traceClip + 0.5f) + 1;
		minTrace = Math.min(Math.max(minTrace, 0), numTraces - 1);
		maxTrace = Math.min(Math.max(maxTrace, 0), numTraces - 1);

		minTrace = (int) (minTrace / stepTrace + 0.5f) * stepTrace;
		maxTrace = (int) (maxTrace / stepTrace + 0.5f) * stepTrace;

		int xAddum = 2000;
		for (int traceIndex = maxTrace; traceIndex >= minTrace; traceIndex--) {
			float[] samples = traceBuffer.samples(traceIndex);
			int xmin = (int) (viewPanel.xModel2View(traceIndex - stepTrace * 0.5f) + xAddum) - xAddum;
			int xmax = (int) (viewPanel.xModel2View(traceIndex + stepTrace * 0.5f) + xAddum) - xAddum;
			if (xmin < xMin) {
				xmin = xMin;
			}
			if (xmax > xMax) {
				xmax = xMax;
			}
			for (int yView = yMin; yView <= yMax; yView++) {
				int sampleIndex = (int) (viewPanel.yView2Model(yView) + 0.5f);
				if (sampleIndex < 0) {
					sampleIndex = 0;
				}
				if (sampleIndex > numSamples - 1) {
					sampleIndex = numSamples - 1;
				}
				// SCALE_TYPE_TRACE is not trivial. The following is a quick fix to make
				// it work:
				if (viewSettings.scaleType == StvConst.SCALE_TYPE_TRACE) {
					viewSettings.vaColorMap.setScalar(ampScalarGlobal * traceScalar[traceIndex]);
				}
				int colorRGB = viewSettings.vaColorMap.getColorRGB(viewSettings.polarity * samples[sampleIndex]);
				if (doHighlight) {
					colorRGB ^= 0x00aaaaff;
				}
				int row = imageWidth * yView;
				for (int xpix = xmin; xpix <= xmax; xpix++) {
					buffer[xpix + row] = colorRGB;
				}
			}
		}
		// System.out.println("Given xmin/max: " + xMin + " " + xMax + " ACTUAL xmin/xmax:
		// " + minmin+ " " + maxmax + " ---- trace: " + traceMinMin + " " + traceMaxMax );
	}

	/**
	 * Repaint seismic display using linear or 1D spline interpolation, without
	 * interpolation in the horizontal direction Variable density plot
	 * 
	 * @param yMin
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected void repaintVAVertical(int yMin, int yMax, int xMin, int xMax) {
		ISeismicTraceBuffer traceBuffer = viewPanel.getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();

		int minTrace = (int) viewPanel.xView2Trace(xMin) - (int) (viewSettings.traceClip + 0.5f) - 1;
		int maxTrace = (int) viewPanel.xView2Trace(xMax) + (int) (viewSettings.traceClip + 0.5f) + 1;
		minTrace = Math.min(Math.max(minTrace, 0), numTraces - 1);
		maxTrace = Math.min(Math.max(maxTrace, 0), numTraces - 1);

		minTrace = (int) (minTrace / stepTrace + 0.5f) * stepTrace;
		maxTrace = (int) (maxTrace / stepTrace + 0.5f) * stepTrace;

		int xAddum = 2000;
		for (int traceIndex = maxTrace; traceIndex >= minTrace; traceIndex--) {
			float[] samples = traceBuffer.samples(traceIndex);
			int xmin = (int) (viewPanel.xModel2View(traceIndex - stepTrace * 0.5f) + xAddum) - xAddum;
			int xmax = (int) (viewPanel.xModel2View(traceIndex + stepTrace * 0.5f) + xAddum) - xAddum;
			if (xmin < xMin)
				xmin = xMin;
			if (xmax > xMax)
				xmax = xMax;

			for (int yView = yMin; yView <= yMax; yView++) {
				float sampleIndex = viewPanel.yView2Model(yView);
				if (sampleIndex >= numSamples - 1)
					sampleIndex = numSamples - 1.001f;
				if (sampleIndex <= 0)
					sampleIndex = 0.001f;
				int isamp1 = (int) sampleIndex;
				int isamp2 = isamp1 + 1;
				float value1 = samples[isamp1];
				float value2 = samples[isamp2];
				float dx = value2 - value1;
				float value;
				if (viewSettings.wiggleType == StvConst.WIGGLE_TYPE_LINEAR) {
					value = (sampleIndex - isamp1) * dx + value1;
				} else { // CUBIC
					float a = isamp2 - sampleIndex;
					float b = 1.0f - a;
					value = a * value1 + b * value2 + (a * (a * a - 1) * vA_yDerivative2Values[traceIndex][isamp1] +
							b * (b * b - 1) * vA_yDerivative2Values[traceIndex][isamp2]) * 1.0f / 6.0f;
				}
				// SCALE_TYPE_TRACE is not trivial. The following is a quick fix to make
				// it work:
				if (viewSettings.scaleType == StvConst.SCALE_TYPE_TRACE) {
					viewSettings.vaColorMap.setScalar(ampScalarGlobal * traceScalar[traceIndex]);
				}
				int colorRGB = viewSettings.vaColorMap.getColorRGB(viewSettings.polarity * value);
				int row = imageWidth * yView;
				for (int xpix = xmin; xpix <= xmax; xpix++) {
					buffer[xpix + row] = colorRGB;
				}
			}
		}

	}

	/**
	 * Repaint seismic display using 2D spline interpolation Variable density plot
	 * 
	 * @param yMin
	 * @param yMax
	 * @param xMin
	 * @param xMax
	 */
	protected void repaintVA2DSpline(int yMin, int yMax, int xMin, int xMax) {
		ISeismicTraceBuffer traceBuffer = viewPanel.getTraceBuffer();
		int numSamples = traceBuffer.numSamples();
		int numTraces = traceBuffer.numTraces();

		if (numTraces < 2) {
			repaintVADiscrete(yMin, yMax, xMin, xMax);
			return;
		}
		// Include 5 additional traces on each side to get correct result for spline
		// interpolation
		// These limits are only required for computing interpolated spline values using
		// csSpline.spline().
		// Repainting is only done within the given smaller limits
		int minTrace = (int) (viewPanel.xView2Trace(xMin) - 5.2f);
		int maxTrace = Math.round(viewPanel.xView2Trace(xMax) + 5.2f);

		minTrace = Math.min(Math.max(minTrace, 0), numTraces - 1);
		maxTrace = Math.min(Math.max(maxTrace, 0), numTraces - 1);

		if (viewSettings.scaleType == StvConst.SCALE_TYPE_TRACE) {
			viewSettings.vaColorMap.setScalar(ampScalarGlobal);
		}
		minTrace = Math.max(Math.round(minTrace / stepTrace) * stepTrace, 0);
		maxTrace = Math.min(Math.round(maxTrace / stepTrace + 0.5f) * stepTrace, numTraces - 1);
		// Compute x view position for each trace that shall be painted, preset arrays for
		// 2D interpolation
		int nTraces = (maxTrace - minTrace) / stepTrace + 1;
		// Make sure there are least two traces to be repainted. One is too few for the 2D
		// interpolation
		if (nTraces == 1) {
			if (minTrace == 0) {
				maxTrace = minTrace + stepTrace;
				if (maxTrace >= numTraces) {
					maxTrace = numTraces - 1;
					minTrace = maxTrace - stepTrace;
				}
			} else {
				minTrace = maxTrace - stepTrace;
				if (minTrace < 0) {
					minTrace = 0;
					maxTrace = minTrace + stepTrace;
				}
			}
			nTraces = 2;
		}
		float[] xViewAtTrace = new float[nTraces];
		float[] seqTraceIndex = new float[nTraces];
		for (int traceIndex = maxTrace; traceIndex >= minTrace; traceIndex -= stepTrace) {
			int traceIndexReduced = (traceIndex - minTrace) / stepTrace;
			float xViewZero = viewPanel.xModel2View(traceIndex);
			xViewAtTrace[traceIndexReduced] = xViewZero;
			seqTraceIndex[traceIndexReduced] = traceIndex;
		}

		float[] valueAtTrace = new float[nTraces]; // Array holding computed values for
													// all traces at one yView position
		float[] va2D_yDerivative2Values = new float[nTraces]; // Second derivatives for 2D
																// spline interpolation
		// Slice through repainted area one by one row from smallest yView to largest
		// yView
		for (int yView = yMin; yView <= yMax; yView++) {
			float sampleIndex = viewPanel.yView2Model(yView);
			if (sampleIndex >= numSamples - 1)
				sampleIndex = numSamples - 1.001f;
			if (sampleIndex <= 0)
				sampleIndex = 0.001f;
			int isamp1 = (int) sampleIndex;
			int isamp2 = isamp1 + 1;

			// At current yView position, compute 1D interpolated spline value for all
			// traces
			for (int traceIndex = maxTrace; traceIndex >= minTrace; traceIndex -= stepTrace) {
				int traceIndexReduced = (traceIndex - minTrace) / stepTrace;
				float[] samples = traceBuffer.samples(traceIndex);
				// float xViewZero = xViewAtTrace[traceIndexReduced];

				// Quick fix to make it work...
				float value1 = samples[isamp1];
				float value2 = samples[isamp2];
				float a = isamp2 - sampleIndex;
				float b = 1.0f - a;
				valueAtTrace[traceIndexReduced] = a * value1 + b * value2 + (a * (a * a - 1) * vA_yDerivative2Values[traceIndex][isamp1] +
						b * (b * b - 1) * vA_yDerivative2Values[traceIndex][isamp2]) * 1.0f / 6.0f;
			}
			// Compute spline derivatives for second dimension, across traces
			Spline.spline(seqTraceIndex, valueAtTrace, 1.0e30f, 1.0e30f, va2D_yDerivative2Values);

			// At current yView position, compute 2D spline values for all x pixel
			// positions
			int row = imageWidth * yView;

			for (int xpix = xMin; xpix <= xMax; xpix++) {
				float traceIndexReduced = (viewPanel.xView2Trace(xpix) - minTrace) / (float) stepTrace;
				// float traceIndexReduced = xView2Trace( xpix ) - minTrace;
				if (traceIndexReduced >= nTraces - 1)
					traceIndexReduced = nTraces - 1.001f;
				if (traceIndexReduced <= 0)
					traceIndexReduced = 0.001f;
				int trace1 = (int) traceIndexReduced;
				int trace2 = trace1 + 1;
				float value1 = valueAtTrace[trace1];
				float value2 = valueAtTrace[trace2];
				float a = trace2 - traceIndexReduced;
				float b = 1.0f - a;
				float value = a * value1 + b * value2 + (a * (a * a - 1) * va2D_yDerivative2Values[trace1] +
						b * (b * b - 1) * va2D_yDerivative2Values[trace2]) * 1.0f / 6.0f;
				int colorRGB = viewSettings.vaColorMap.getColorRGB(viewSettings.polarity * value);
				buffer[xpix + row] = colorRGB;
			}
		}
	}

	@Override
	public void settingsChanged(ViewSettings settings) {
		// TODO Auto-generated method stub

	}
}
