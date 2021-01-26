package com.ghc.app.general;

import java.awt.Color;

import javax.swing.JToolBar;

public class StvConst {
	public static final int ZOOM_VERT = 1;
	public static final int ZOOM_HORZ = 2;
	public static final int ZOOM_BOTH = ZOOM_VERT | ZOOM_HORZ;

	public static final int ZOOM_IN = 11;
	public static final int ZOOM_OUT = 12;

	// ---------DisplaySettings Constants-----------//
	public static final int WIGGLE_FILL_NONE = 0;
	public static final int WIGGLE_FILL_POS = 1;
	public static final int WIGGLE_FILL_NEG = 2;
	public static final int WIGGLE_FILL_POSNEG = 3;

	public static final int WIGGLE_COLOR_FIXED = 41;
	public static final int WIGGLE_COLOR_VARIABLE = 42;

	public static final int SCALE_TYPE_SCALAR = 31;
	public static final int SCALE_TYPE_RANGE = 32;
	public static final int SCALE_TYPE_TRACE = 33;

	public static final int TRACE_SCALING_MAXIMUM = 51;
	public static final int TRACE_SCALING_AVERAGE = 52;

	public static final int WIGGLE_TYPE_LINEAR = 11;
	public static final int WIGGLE_TYPE_CUBIC = 12;

	public static final float POLARITY_NORMAL = 1.0f;
	public static final float POLARITY_REVERSED = -1.0f;

	public static final int VA_TYPE_2DSPLINE = 21;
	public static final int VA_TYPE_VERTICAL = 22;
	public static final int VA_TYPE_DISCRETE = 23;

	public static final int PLOT_DIR_VERTICAL = 0;
	public static final int PLOT_DIR_HORIZONTAL = 1;

	// ---------StvPanel Constants-----------//
	public static final int DEFAULT_WIDTH_SIDELABEL = 64;
	public static final int DEFAULT_HEIGHT_SIDELABEL = 25;
	public static final int MIN_ZOOM_WIDTH = 3;

	public static final int PRESSED_NONE = 0;
	public static final int PRESSED_START = 1;
	public static final int PRESSED_END = 2;
	public static final int PRESSED_BOTH = 3;

	public static final int DEFAULT_UNIT_INCREMENT = 4;
	public static final Color DEFAULT_COLOR = new JToolBar().getBackground();

	public static final int DEFAULT_LABEL_FONTSIZE = 12;


}
