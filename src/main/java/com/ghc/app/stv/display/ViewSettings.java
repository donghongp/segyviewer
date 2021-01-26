package com.ghc.app.stv.display;

import java.awt.Color;

import com.ghc.app.general.ColorMap;
import com.ghc.app.general.StvConst;

public class ViewSettings {

	/// Vertical zoom factor = Screen pixels per sample
	public float zoomVert = 1.0f;
	/// Horizontal zoom factor = Screen pixels per trace
	public float zoomHorz = 10.0f;

	// Attributes
	// public int fillWiggle;
	/// Wiggle type (linear,cubic)
	public int wiggleType = StvConst.WIGGLE_TYPE_LINEAR;
	/// Variable area type (discrete,vertical,spline)
	public int vaType = StvConst.VA_TYPE_DISCRETE;

	/// 'Scale' type: How is seismic trace scaled? By scalar, range, or trace-by-trace
	public int scaleType = StvConst.SCALE_TYPE_TRACE;
	/// Further definition of trace-by-trace scaling: By trace maximum, or average
	public int traceScaling = StvConst.TRACE_SCALING_AVERAGE;
	/// Scalar to be applied to amplitudes for global scaling
	public float dispScalar = 1.0f;
	/// Scalar to be applied to amplitudes for full trace scaling
	public float fullTraceScalar = 1.0f;
	/// Min value when range is specified
	public float minValue = -1.0f;
	/// Max value when range is specified
	public float maxValue = 1.0f;

	/// Show or hide wiggle trace
	public boolean showWiggle = true;
	/// Highlight flag
	public boolean isHighlight = true;
	/// true if positive wiggle is filled
	public boolean isPosFill = true;
	/// true if negative wiggle is filled
	public boolean isNegFill = false;
	/// true if wiggle area shall be filled by variable color
	public boolean isVariableColor = false;
	/// Show or hide variable area display
	public boolean isVADisplay = false;
	/// Apply trace clipping yes/no
	public boolean doTraceClipping = true;
	/// Show/hide zero line
	public boolean showZeroLines = false;
	/// Number of traces where seismic wiggle is clipped
	public float traceClip = 2.0f;
	/// Polarity of display (+/-1.0)
	public float polarity = StvConst.POLARITY_NORMAL;
	/// true if vertical axis shall be plotted in log scale
	public boolean isLogScale = false;
	/// Title of seismic display
	public String title = "untitled";
	/// Title of vertical axis
	public String titleVertAxis = "DEFAULT";

	/// Color of wiggle line
	public Color wiggleColor = Color.black;
	/// Color of wiggle line
	public Color wiggleColorHighlight = Color.red;
	/// Color of positive wiggle area
	public Color wiggleColorPos = Color.black;
	/// Color of negative wiggle area
	public Color wiggleColorNeg = Color.lightGray;
	/// Color map of positive wiggle area (in case of variable color display)
	public ColorMap wiggleColorMap = new ColorMap(ColorMap.GRAY_WB, ColorMap.COLOR_MAP_TYPE_32BIT);
	/// Color map of negative wiggle area (in case of variable color display)
	public ColorMap vaColorMap = new ColorMap(ColorMap.DEFAULT, ColorMap.COLOR_MAP_TYPE_32BIT);

	/// Show/hide horizontal time lines
	public boolean showTimeLines = true;
	/// true if horizontal lines shall be computed automatically
	@Deprecated
	public boolean isTimeLinesAuto;
	/// in vertical units (ms or Hz..)
	public double timeLineMinorInc = 100;
	/// in vertical units (ms or Hz..)
	public double timeLineMajorInc = 500;

	/// Plot direction: Vertical or horizontal
	public int plotDirection = StvConst.PLOT_DIR_VERTICAL;

	// the following attribute is shown under Menu and not on displayDialog.
	public boolean showSeqTraceNum = true;

	public ViewSettings(ViewSettings ds) {
		set(ds);
	}

	public ViewSettings() {
		// setDefaults();
	}

	public void set(ViewSettings ds) {
		if (ds == this)
			return;
		zoomVert = ds.zoomVert;
		zoomHorz = ds.zoomHorz;
		traceClip = ds.traceClip;
		dispScalar = ds.dispScalar;
		fullTraceScalar = ds.fullTraceScalar;
		scaleType = ds.scaleType;
		traceScaling = ds.traceScaling;
		polarity = ds.polarity;
		minValue = ds.minValue;
		maxValue = ds.maxValue;
		title = ds.title;

		wiggleColorHighlight = ds.wiggleColorHighlight;
		isHighlight = ds.isHighlight;

		// Wiggle settings
		// fillWiggle = WIGGLE_FILL_POS;
		wiggleType = ds.wiggleType;
		isVariableColor = ds.isVariableColor;
		doTraceClipping = ds.doTraceClipping;
		showWiggle = ds.showWiggle;
		isPosFill = ds.isPosFill;
		isNegFill = ds.isNegFill;
		isVADisplay = ds.isVADisplay;
		vaType = ds.vaType;
		showZeroLines = ds.showZeroLines;

		// Time lines
		showTimeLines = ds.showTimeLines;
		timeLineMinorInc = ds.timeLineMinorInc;
		timeLineMajorInc = ds.timeLineMajorInc;
		isTimeLinesAuto = ds.isTimeLinesAuto;
		titleVertAxis = ds.titleVertAxis;

		// Colors
		wiggleColorPos = ds.wiggleColorPos;
		wiggleColorNeg = ds.wiggleColorNeg;
		// Setting color maps as follows is problematic! Should be changed to something
		// more stable.
		// Problems occur with min/max values and scalars
		if (wiggleColorMap == null) {
			wiggleColorMap = new ColorMap(ds.wiggleColorMap);
		} else if (wiggleColorMap != ds.wiggleColorMap) {
			wiggleColorMap.resetColors(ds.wiggleColorMap);
		}
		if (vaColorMap == null) {
			vaColorMap = new ColorMap(ds.vaColorMap);
		} else if (vaColorMap != ds.vaColorMap) {
			vaColorMap.resetColors(ds.vaColorMap);
		}
		plotDirection = ds.plotDirection;
		isLogScale = ds.isLogScale;
	}

	// public void setDefaults() {
	// zoomVert = 1.0f;
	// zoomHorz = 10.0f;
	// traceClip = 2.0f;
	// fullTraceScalar = 1.0f;
	// scaleType = StvConst.SCALE_TYPE_SCALAR;
	// traceScaling = StvConst.TRACE_SCALING_AVERAGE;
	// polarity = StvConst.POLARITY_NORMAL;
	// minValue = 0.0f;
	// maxValue = 0.0f;
	// title = "";
	// titleVertAxis = "DEFAULT";
	//
	// highlightColor = Color.yellow;
	// isHightlightOn = false;
	//
	// // Wiggle settings
	// // fillWiggle = WIGGLE_FILL_POS;
	// wiggleType = StvConst.WIGGLE_TYPE_LINEAR;
	// isVariableColor = false;
	// doTraceClipping = true;
	// showWiggle = true;
	// isPosFill = true;
	// isNegFill = false;
	// isVADisplay = false;
	// vaType = StvConst.VA_TYPE_DISCRETE;
	// showZeroLines = false;
	//
	// // Time lines
	// showTimeLines = true;
	// timeLineMinorInc = 100;
	// timeLineMajorInc = 500;
	// isTimeLinesAuto = true;
	//
	// // Colors
	// wiggleColorPos = Color.black;
	// wiggleColorNeg = Color.lightGray;
	// wiggleColorMap = new ColorMap(ColorMap.GRAY_WB, ColorMap.COLOR_MAP_TYPE_32BIT);
	// vaColorMap = new ColorMap(ColorMap.DEFAULT, ColorMap.COLOR_MAP_TYPE_32BIT);
	//
	// plotDirection = StvConst.PLOT_DIR_VERTICAL;
	// isLogScale = false;
	// }

	public void dump() {
		System.out.println("zoomVert   " + zoomVert);
		System.out.println("zoomHorz   " + zoomHorz);
		System.out.println("traceClip  " + traceClip);
		System.out.println("dispScalar " + dispScalar);
		System.out.println("fullTraceScalar " + fullTraceScalar);
		System.out.println("scaleType       " + scaleType);
		System.out.println("traceScaling    " + traceScaling);
		System.out.println("polarity        " + polarity);
		System.out.println("minValue     " + minValue);
		System.out.println("maxValue     " + maxValue);

		System.out.println("wiggleColorHighlight " + wiggleColorHighlight.getRGB());
		System.out.println("isHighlightOn " + isHighlight);

		// Wiggle settings
		// fillWiggle = WIGGLE_FILL_POS);
		System.out.println("wiggleType      " + wiggleType);
		System.out.println("isVariableColor " + isVariableColor);
		System.out.println(" doTraceClipping " + doTraceClipping);
		System.out.println("showWiggle      " + showWiggle);
		System.out.println("isPosFill       " + isPosFill);
		System.out.println("isNegFill       " + isNegFill);
		System.out.println("isVADisplay     " + isVADisplay);
		System.out.println("vaType          " + vaType);
		System.out.println("showZeroLines   " + showZeroLines);

		// Time lines
		System.out.println("showTimeLines " + showTimeLines);
		System.out.println("timeLineMinorInc " + timeLineMinorInc);
		System.out.println(" timeLineMajorInc " + timeLineMajorInc);
		System.out.println(" isTimeLinesAuto  " + isTimeLinesAuto);

		// Colors
		System.out.println(" wiggleColorPos " + wiggleColorPos.getRGB());
		System.out.println("  wiggleColorNeg " + wiggleColorNeg.getRGB());
		System.out.println(" wiggleColorMap " + wiggleColorMap);
		System.out.println(" vaColorMap " + vaColorMap);
		System.out.println(" plotDirection " + plotDirection);
		System.out.println("  isLogScale    " + isLogScale);

		System.out.println(" title " + title);
		System.out.println("  titleVertAxis    " + titleVertAxis);
	}
}
