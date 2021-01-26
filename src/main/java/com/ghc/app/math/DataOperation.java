package com.ghc.app.math;

/**
 * Provides static methods for mathematical data operations on pairs of data points (2D curves).
 */
public class DataOperation {
	private int _numValuesOrig;
	private float[] _xValuesOrig;
	private float[] _yValuesOrig;

	private float[] _xValues;
	private float[] _yValues;

	public DataOperation(float[] xValues, float[] yValues) {
		_numValuesOrig = xValues.length;
		_xValuesOrig = xValues;
		_yValuesOrig = yValues;
		_xValues = xValues;
		_yValues = yValues;
	}

	/**
	 * Interpolate data points to regular grid, using mean values
	 * 
	 * @param increment Increment of new data points (X dimension)
	 */
	public void grid_mean(float increment) {
		float xValueStart = _xValuesOrig[0] - (_xValuesOrig[0] % increment);
		int nValuesNew = (int) ((_xValuesOrig[_numValuesOrig - 1] - xValueStart) / increment + 0.5);
		if (nValuesNew >= _numValuesOrig) {
			_xValues = _xValuesOrig;
			_yValues = _yValuesOrig;
			return;
		}
		float[] xValuesNew = new float[nValuesNew];
		float[] yValuesNew = new float[nValuesNew];

		int indexOld = 0;
		int indexStart = indexOld;
		for (int i = 0; i < nValuesNew; i++) {
			float xValue = xValueStart + (float) i * increment;
			float xValueHalf = xValue + increment * 0.5f;

			while (indexOld < _numValuesOrig - 1 && _xValuesOrig[indexOld] < xValueHalf) {
				indexOld += 1;
			}

			float yValue = 0.0f;
			if (indexOld != indexStart) {
				for (int index = indexStart; index < indexOld; index++) {
					yValue += _yValuesOrig[index];
				}
			} else {
				yValue = _yValuesOrig[indexOld];
			}

			xValuesNew[i] = xValue;
			yValuesNew[i] = yValue;
			indexStart = indexOld;
		}
		_xValues = xValuesNew;
		_yValues = yValuesNew;
	}

	public int numValues() {
		return _xValues.length;
	}

	public float[] xValues() {
		return _xValues;
	}

	public float[] yValues() {
		return _yValues;
	}
}
