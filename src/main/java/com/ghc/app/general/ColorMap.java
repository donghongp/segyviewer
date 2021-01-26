package com.ghc.app.general;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Color map with static list of default color maps.
 * - Defines a number of fixed colors at key positions
 * - Interpolates colors to every position inside a given range
 */
public class ColorMap {
	public static final int DEFAULT = 0;
	public static final int GRAY_WB = 1;
	public static final int GRAY_BW = 2;
	public static final int BLUE_WHITE_RED = 3;
	public static final int RAINBOW = 4;
	public static final int BLACK_WHITE_ORANGE = 5;
	public static final int GRAY_BWB = 6;
	public static final int GRAY_WBW = 7;
	public static final int RAINBOW_BLACK = 8;
	public static final int RAINBOW_BLACK_REV = 9;
	public static final int RAINBOW_MIRROR = 10;
	public static final int COLD_WARM = 11;
	public static final int BLUE_WHITE_RED2 = 12;
	public static final int BLACK_WHITE_RED = 13;
	public static final int NUM_DEFAULT_MAPS = 14;

	public static final String TEXT_DEFAULT = "default";
	public static final String TEXT_GRAY_WB = "gray_w2b";
	public static final String TEXT_GRAY_BW = "gray_b2w";
	public static final String TEXT_BLUE_WHITE_RED = "blue_white_red";
	public static final String TEXT_RAINBOW = "rainbow";
	public static final String TEXT_BLACK_WHITE_ORANGE = "black_white_orange";
	public static final String TEXT_GRAY_BWB = "gray_bwb";
	public static final String TEXT_GRAY_WBW = "gray_wbw";
	public static final String TEXT_RAINBOW_BLACK = "rainbow_black";
	public static final String TEXT_RAINBOW_BLACK_REV = "rainbow_2";
	public static final String TEXT_RAINBOW_MIRROR = "rainbow_mirror";
	public static final String TEXT_COLD_WARM = "cold_warm";
	public static final String TEXT_BLUE_WHITE_RED2 = "blue_white_red2";
	public static final String TEXT_BLACK_WHITE_RED = "black_white_red";

	public static final String[] TEXT = {
			TEXT_DEFAULT,
			TEXT_GRAY_WB,
			TEXT_GRAY_BW,
			TEXT_BLUE_WHITE_RED,
			TEXT_RAINBOW,
			TEXT_BLACK_WHITE_ORANGE,
			TEXT_GRAY_BWB,
			TEXT_GRAY_WBW,
			TEXT_RAINBOW_BLACK,
			TEXT_RAINBOW_BLACK_REV,
			TEXT_RAINBOW_MIRROR,
			TEXT_COLD_WARM,
			TEXT_BLUE_WHITE_RED2,
			TEXT_BLACK_WHITE_RED
	};

	public static final int NUM_COLORS_DEFAULT = 101;

	public static final int METHOD_SCALAR = 1; // Global scalar
	public static final int METHOD_RANGE = 2; // Global range
	public static final int METHOD_TRACE = 3; // Trace-by-trace scalar

	public static final int COLOR_MAP_TYPE_32BIT = 91;
	public static final int COLOR_MAP_TYPE_8BIT = 92;

	public static final int SMOOTH_MODE_DISCRETE = 101;
	public static final int SMOOTH_MODE_CONTINUOUS = 102;

	protected int _numColors;
	protected double _valueRange;
	protected double _scalar;
	protected double _minValue;
	protected double _maxValue;
	/// Map index of color map. If the color map is user defined, the index is set to
	/// NUM_DEFAULT_MAPS
	public int _defaultMapIndex;
	protected int _scaleMethod;
	protected int _smoothMode = ColorMap.SMOOTH_MODE_CONTINUOUS;
	protected int _discreteStep = 1;

	protected Color[] _colorKneePoints;
	protected double[] _weightKneePoints;

	public int _colorMapType;
	protected Color[] _colors;
	protected byte[] _colors8bit;

	public ColorMap() {
		this(DEFAULT, COLOR_MAP_TYPE_32BIT);
	}

	public ColorMap(Color color) {
		Color[] colors = new Color[1];
		colors[0] = color;
		double[] weights = new double[1];
		weights[0] = 1.0;
		setColors(colors, weights, colors.length);
	}

	public ColorMap(Color[] colors, int numColorResolution) {
		_colorMapType = ColorMap.COLOR_MAP_TYPE_32BIT;
		double[] weights = new double[colors.length];
		if (colors.length == 1) {
			weights[0] = 1.0;
		} else {
			for (int i = 0; i < colors.length; i++) {
				weights[i] = (double) i / (double) (colors.length - 1);
			}
		}
		setColors(colors, weights, numColorResolution);
	}

	public ColorMap(int colorMapIndex) {
		this(colorMapIndex, COLOR_MAP_TYPE_32BIT, NUM_COLORS_DEFAULT);
	}

	public ColorMap(int colorMapIndex, int colorMapType) {
		this(colorMapIndex, colorMapType, NUM_COLORS_DEFAULT);
	}

	public ColorMap(int colorMapIndex, int colorMapType, int numColors_in) {
		_numColors = 0;
		_scalar = 1.0;
		_minValue = -1.0;
		_maxValue = 1.0;
		_valueRange = _maxValue - _minValue;
		_scaleMethod = METHOD_RANGE;
		_colorMapType = colorMapType;
		_colors8bit = null;
		_colors = null;

		Color[] colors;
		double[] weights = null;

		if (colorMapIndex == DEFAULT) {
			colors = new Color[5];
			colors[0] = Color.cyan;
			colors[1] = Color.black;
			colors[2] = Color.white;
			colors[3] = Color.red;
			colors[4] = Color.yellow;
			weights = new double[5];
			weights[0] = 0.0;
			weights[1] = 0.05;
			weights[2] = 0.5;
			weights[3] = 0.95;
			weights[4] = 1.0;
		} else if (colorMapIndex == GRAY_WB) {
			colors = new Color[2];
			colors[0] = Color.white;
			colors[1] = Color.black;
		} else if (colorMapIndex == GRAY_BW) {
			colors = new Color[2];
			colors[0] = Color.black;
			colors[1] = Color.white;
		} else if (colorMapIndex == BLUE_WHITE_RED) {
			colors = new Color[3];
			colors[0] = Color.blue;
			colors[1] = Color.white;
			colors[2] = Color.red;
		} else if (colorMapIndex == BLUE_WHITE_RED2) {
			colors = new Color[15];
			colors[0] = new Color(0, 0, 144);
			colors[1] = new Color(5, 5, 154);
			colors[2] = new Color(10, 10, 164);
			colors[3] = new Color(16, 16, 174);
			colors[4] = new Color(120, 120, 184);
			colors[5] = new Color(165, 165, 216);
			colors[6] = new Color(215, 215, 245);
			colors[7] = new Color(255, 255, 255);
			colors[8] = new Color(255, 215, 215);
			colors[9] = new Color(246, 165, 165);
			colors[10] = new Color(224, 120, 120);
			colors[11] = new Color(214, 16, 16);
			colors[12] = new Color(204, 10, 10);
			colors[13] = new Color(194, 5, 5);
			colors[14] = new Color(184, 0, 0);
			weights = new double[15];
			weights[0] = 0.0;
			weights[1] = 0.1;
			weights[2] = 0.2;
			weights[3] = 0.3;
			weights[4] = 0.35;
			weights[5] = 0.4;
			weights[6] = 0.45;
			weights[7] = 0.5;
			weights[8] = 0.55;
			weights[9] = 0.6;
			weights[10] = 0.65;
			weights[11] = 0.7;
			weights[12] = 0.8;
			weights[13] = 0.9;
			weights[14] = 1.0;
		} else if (colorMapIndex == RAINBOW) {
			colors = new Color[6];
			colors[0] = Color.blue;
			colors[1] = Color.cyan;
			colors[2] = Color.green;
			colors[3] = Color.yellow;
			colors[4] = Color.red;
			colors[5] = Color.magenta;
		} else if (colorMapIndex == RAINBOW_BLACK) {
			colors = new Color[7];
			colors[0] = Color.black;
			colors[1] = Color.blue;
			colors[2] = Color.cyan;
			colors[3] = Color.green;
			colors[4] = Color.yellow;
			colors[5] = Color.red;
			colors[6] = Color.magenta;
		} else if (colorMapIndex == COLD_WARM) {
			colors = new Color[6];
			colors[0] = Color.black;
			colors[1] = Color.blue;
			// colors[2] = Color.cyan;
			colors[2] = Color.white;
			colors[3] = Color.yellow;
			// colors[5] = new Color( 255, 127, 0 );
			colors[4] = Color.red;
			colors[5] = Color.magenta;
			int numColors = colors.length;
			weights = new double[numColors];
			weights[0] = 0.0;
			weights[1] = 0.24;
			weights[2] = 0.48;
			weights[3] = 0.76;
			weights[4] = 0.90;
			weights[5] = 1.0;
		} else if (colorMapIndex == BLACK_WHITE_ORANGE) {
			colors = new Color[3];
			colors[0] = Color.black;
			colors[1] = Color.white;
			colors[2] = new Color(255, 130, 0);
		} else if (colorMapIndex == BLACK_WHITE_RED) {
			colors = new Color[6];
			colors[0] = Color.black;
			colors[1] = Color.darkGray;
			colors[2] = Color.white;
			colors[3] = Color.yellow;
			colors[4] = new Color(224, 30, 30);
			colors[5] = new Color(204, 10, 10);
			weights = new double[6];
			weights[0] = 0.0;
			weights[1] = 0.4;
			weights[2] = 0.5;
			weights[3] = 0.55;
			weights[4] = 0.6;
			weights[5] = 1.0;
		} else if (colorMapIndex == GRAY_BWB) {
			colors = new Color[3];
			colors[0] = Color.black;
			colors[1] = Color.white;
			colors[2] = Color.black;
		} else if (colorMapIndex == GRAY_WBW) {
			colors = new Color[3];
			colors[0] = Color.white;
			colors[1] = Color.black;
			colors[2] = Color.white;
		} else if (colorMapIndex == RAINBOW_MIRROR) {
			colors = new Color[11];
			colors[0] = Color.magenta;
			colors[1] = Color.red;
			colors[2] = Color.yellow;
			colors[3] = Color.green;
			colors[4] = Color.cyan;
			colors[5] = Color.blue;
			colors[6] = Color.cyan;
			colors[7] = Color.green;
			colors[8] = Color.yellow;
			colors[9] = Color.red;
			colors[10] = Color.magenta;
		} else if (colorMapIndex == RAINBOW_BLACK_REV) {
			colors = new Color[6];
			colors[0] = Color.blue;
			colors[1] = Color.green;
			colors[2] = Color.yellow;
			colors[3] = Color.red;
			colors[4] = Color.magenta;
			colors[5] = Color.black;
		} else {
			System.out.println("Unknown color map: " + colorMapIndex);
			return;
		}
		if (weights == null) {
			int numColors = colors.length;
			weights = new double[numColors];
			for (int i = 0; i < numColors; i++) {
				weights[i] = (double) i / (double) (numColors - 1);
			}
		}
		setColors(colors, weights, numColors_in);
		_defaultMapIndex = colorMapIndex;
	}

	public ColorMap(ColorMap map) {
		resetAll(map);
	}

	public void setSmoothModeDiscrete(int discreteStep) {
		_smoothMode = ColorMap.SMOOTH_MODE_DISCRETE;
		_discreteStep = discreteStep;
	}

	public void setSmoothModeContinuous() {
		_smoothMode = ColorMap.SMOOTH_MODE_CONTINUOUS;
	}

	public int getSmoothMode() {
		return _smoothMode;
	}

	public int getDiscreteStep() {
		return _discreteStep;
	}

	public void setColorMapType(int type) {
		if (_colorMapType != type) {
			_colorMapType = type;
			if (_colorMapType == ColorMap.COLOR_MAP_TYPE_32BIT) {
				_colors8bit = null;
			} else {
				computeColors8bit();
			}
		}
	}

	public void setNumColorResolution(int numColors) {
		if (numColors < _colorKneePoints.length)
			numColors = _colorKneePoints.length;
		setColors(_colorKneePoints, _weightKneePoints, numColors);
	}

	/**
	 * Reset all parameters to the values of the specified color map.
	 * 
	 * @param map Color map
	 */
	protected void resetAll(ColorMap map) {
		resetColors(map);
		_scalar = map._scalar;
		_minValue = map._minValue;
		_maxValue = map._maxValue;
		_valueRange = map._valueRange;
		_scaleMethod = map._scaleMethod;
		_smoothMode = map._smoothMode;
	}

	/**
	 * Reset colors to the colors of the specified color map. Do not reset any other
	 * parameters.
	 * 
	 * @param map Color map giving the new colors
	 */
	public void resetColors(ColorMap map) {
		if (map == this)
			return;
		_numColors = map._numColors;
		_colorMapType = map._colorMapType;
		_defaultMapIndex = map._defaultMapIndex;
		_colorKneePoints = map._colorKneePoints;
		_weightKneePoints = map._weightKneePoints;

		Color[] colors = new Color[_numColors];
		for (int i = 0; i < _numColors; i++) {
			colors[i] = map._colors[i];
		}
		_colors = colors;
		if (_colorMapType == COLOR_MAP_TYPE_8BIT) {
			computeColors8bit();
		}
	}

	/**
	 * Set specified
	 * 
	 * @param colors
	 * @param colorWeights
	 * @param numColors
	 */
	protected void setColors(Color[] colors, double[] colorWeights, int numColors) {
		_defaultMapIndex = NUM_DEFAULT_MAPS;
		_numColors = numColors;
		_colorKneePoints = colors;
		_weightKneePoints = colorWeights;
		_colors = ColorMap.computeColors(_colorKneePoints, _weightKneePoints, _numColors);
		if (_colorMapType == COLOR_MAP_TYPE_8BIT) {
			computeColors8bit();
		}
	}

	/**
	 * Compute the specified number of interpolated colors for the specified set of color
	 * knee points and associated weights/distances.
	 * 
	 * @param colors N colors
	 * @param colorWeights N weights, values increasing from 0-1
	 * @param numColors Number of output colors
	 * @return Computed colors
	 */
	protected static Color[] computeColors(Color[] colors, double[] colorWeights, int numColors) {
		Color[] colorsOut = new Color[numColors];

		int index = 0;
		for (int i = 0; i < numColors; i++) {
			float weight = (float) i / (float) (numColors - 1);
			while (index < (colorWeights.length - 1) && weight >= colorWeights[index])
				index += 1;
			int redMin = colors[index - 1].getRed();
			int blueMin = colors[index - 1].getBlue();
			int greenMin = colors[index - 1].getGreen();
			int redMax = colors[index].getRed();
			int blueMax = colors[index].getBlue();
			int greenMax = colors[index].getGreen();
			float color_weight_step = (float) (colorWeights[index] - colorWeights[index - 1]);

			weight = (weight - (float) colorWeights[index - 1]) / color_weight_step;
			int red = (int) (redMin + weight * (redMax - redMin));
			int green = (int) (greenMin + weight * (greenMax - greenMin));
			int blue = (int) (blueMin + weight * (blueMax - blueMin));

			colorsOut[i] = new Color(red, green, blue);
		}
		return colorsOut;
	}

	/**
	 * Set min/max values
	 * 
	 * @param minValue
	 * @param maxValue
	 */
	public void setMinMax(double minValue, double maxValue) {
		_scaleMethod = METHOD_RANGE;
		setMinMax_internal(minValue, maxValue);
	}

	private void setMinMax_internal(double minValue, double maxValue) {
		_minValue = minValue;
		_maxValue = maxValue;
		_valueRange = _maxValue - _minValue;
		if (_valueRange == 0)
			_valueRange = 1;
	}

	/**
	 * Set constant scalar. This scalar is applied to each value for which the
	 * corresponding color shall be retrieved.
	 * 
	 * @param scalar
	 */
	public void setScalar(double scalar) {
		_scaleMethod = METHOD_SCALAR;
		_scalar = Math.abs(scalar);
		setMinMax_internal(-_scalar, _scalar);
	}

	public int getDefaultMapIndex() {
		return _defaultMapIndex;
	}

	public int getMethod() {
		return _scaleMethod;
	}

	public int getColorMapType() {
		return _colorMapType;
	}

	/**
	 * 
	 * @param value
	 * @return RGB color at given value
	 */
	public int getColorRGB(float value) {
		return _colors[bufferIndex(value)].getRGB();
	}

	/**
	 * 
	 * @param value
	 * @return Color at given value
	 */
	public Color getColor(float value) {
		return _colors[bufferIndex(value)];
	}

	public Color[] getColors() {
		return _colors;
	}

	public Color[] getColorKneePoints() {
		return _colorKneePoints;
	}

	/**
	 * 
	 * @param value
	 * @return RGB color at given value
	 */
	protected int bufferIndex(float value) {
		if (_smoothMode == ColorMap.SMOOTH_MODE_DISCRETE) {
			value = (float) (_discreteStep * Math.round(value / (float) _discreteStep));
		}
		double weight;
		switch (_scaleMethod) {
			case METHOD_RANGE:
				weight = (value - _minValue) / _valueRange;
				break;
			case METHOD_SCALAR:
				weight = (1 + value * _scalar) * 0.5;
				break;
			default:
				weight = 0;
		}
		int index = (int) (weight * (double) (_numColors - 1) + 0.5);
		if (index < 0)
			index = 0;
		if (index >= _numColors)
			index = _numColors - 1;
		return index;
	}

	public double getScalar() {
		return _scalar;
	}

	public double getMinValue() {
		return _minValue;
	}

	public double getMaxValue() {
		return _maxValue;
	}

	public static String textDefaultMap(int i) {
		if (i >= 0 && i < NUM_DEFAULT_MAPS) {
			return TEXT[i];
		}
		return "UNKNOWN";
	}

	// *******************************************************************
	//
	// 8bit methods
	//
	// *******************************************************************
	protected void computeColors8bit() {
		_colors8bit = new byte[_numColors];
		for (int i = 0; i < _numColors; i++) {
			int red = _colors[i].getRed();
			int green = _colors[i].getGreen();
			int blue = _colors[i].getBlue();
			_colors8bit[i] = ColorMap.convertColorTo8bit(red, green, blue);
		}
	}

	/**
	 * 
	 * @param value
	 * @return Indexed byte color at given value
	 */
	public byte getColor8bit(float value) {
		return _colors8bit[bufferIndex(value)];
	}

	/**
	 * Convert color to indexed byte value, according to default indexed byte color model
	 * (see BufferedImage TYPE_BYTE_INDEXED)
	 * 
	 * @param color
	 * @return
	 */
	public static byte convertColorTo8bit(Color color) {
		return convertColorTo8bit(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static byte convertColorTo8bit(int red, int green, int blue) {
		if (red != green || red != blue) {
			return (byte) (36 * (int) (red / 51.0 + 0.5) + 6 * (int) (green / 51.0 + 0.5) + (int) (blue / 51.0 + 0.5));
		} else {
			return (byte) (216 + (int) (red / 6.54 + 0.5));
		}
	}

	public static int getDefaultMapIndex(String text) {
		for (int i = 0; i < TEXT.length; i++) {
			if (text.compareTo(TEXT[i]) == 0) {
				return i;
			}
		}
		return -1;
	}

	public String toString() {
		if (_defaultMapIndex >= 0 && _defaultMapIndex <= TEXT.length) {
			return TEXT[_defaultMapIndex];
		} else {
			return "CUSTOM_COLOR_MAP";
		}
	}

	// -------------------------------------------------
	//
	public static void main(String[] args) {
		JDialog dialog = new JDialog();
		@SuppressWarnings("serial")
		JPanel panel = new JPanel(new BorderLayout()) {
			public void paintComponent(Graphics g) {
				int height = getHeight();
				int width = getWidth();
				ColorMap map = new ColorMap(ColorMap.RAINBOW, ColorMap.COLOR_MAP_TYPE_32BIT);
				map.setMinMax(0, height - 1);
				map.setSmoothModeDiscrete(height / 4);
				for (int i = 0; i < height; i++) {
					int colorRGB = map.getColorRGB(i);
					g.setColor(new Color(colorRGB));
					g.drawLine(0, i, width - 1, i);
				}
			}
		};
		dialog.getContentPane().add(panel);
		dialog.setSize(100, 600);
		dialog.setVisible(true);

	}
}
