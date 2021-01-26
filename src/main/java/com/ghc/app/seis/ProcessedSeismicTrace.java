package com.ghc.app.seis;

/**
 * Seismic trace, processed.
 */
public class ProcessedSeismicTrace {
	private Number[] _headerValues;
	private SeismicData _origData;
	private SeismicData _processedData;
	private int _numHeaders;

	protected boolean _isProcessed;
	private int _processingType;

	public ProcessedSeismicTrace(int nSamples, int nHeaders) {
		_numHeaders = nHeaders;
		_headerValues = new Number[_numHeaders];
		_origData = new SeismicData(nSamples);
		_processedData = null;
		_isProcessed = false;
		_processingType = Processing.NONE;
	}

	public ProcessedSeismicTrace(float[] samples, Number[] headers) {
		_numHeaders = headers.length;
		_headerValues = headers;
		_origData = new SeismicData(samples);
		_processedData = null;
		_isProcessed = false;
		_processingType = Processing.NONE;
	}

	public SeismicData data() {
		return _origData;
	}

	public void applyProcessing(int procType) {
		if (procType == _processingType)
			return;
		switch (_processingType) {
			case Processing.NONE:
				break;
			case Processing.REMOVE_DC:
				_processedData.setSamples(Processing.removeDC(_origData.samples(), _origData.dcAmplitude()));
				break;
			default:
				_processingType = Processing.NONE;
				break;
		}
		_processingType = procType;
		if (_processingType != Processing.NONE) {
			_isProcessed = false;
		}
	}

	public float[] samples() {
		if (!_isProcessed) {
			return _origData.samples();
		} else {
			return _processedData.samples();
		}
	}

	public void setSamples(float[] samples) {
		_origData.setSamples(samples);
	}

	public Number[] headerValues() {
		return _headerValues;
	}

	public int numSamples() {
		return _origData.numSamples();
	}

	public int numHeaders() {
		return _numHeaders;
	}

	public void setDoubleHeader(int index, double value) {
		if (index >= 0 && index < _numHeaders) {
			_headerValues[index] = new Double(value);
		} else {

		}
	}

	public void setFloatHeader(int index, float value) {
		if (index >= 0 && index < _numHeaders) {
			_headerValues[index] = new Float(value);
		} else {

		}
	}

	public void setIntHeader(int index, int value) {
		if (index >= 0 && index < _numHeaders) {
			_headerValues[index] = new Integer(value);
		} else {

		}
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Maximum amplitude in specified trace
	 */
	public float maxAmplitude() {
		if (_isProcessed) {
			return _origData.maxAmplitude();
		} else {
			return _processedData.maxAmplitude();
		}
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Mean (absolute) amplitude of specified trace
	 */
	public float meanAmplitude() {
		if (_isProcessed) {
			return _origData.meanAmplitude();
		} else {
			return _processedData.meanAmplitude();
		}
	}

	/**
	 * 
	 * @param traceIndex
	 * @return DC amplitude (= mean of signed amplitude) of specified trace
	 */
	public float dcAmplitude() {
		if (_isProcessed) {
			return _origData.dcAmplitude();
		} else {
			return _processedData.dcAmplitude();
		}
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Minimum amplitude in specified trace
	 */
	public float minAmplitude() {
		if (_isProcessed) {
			return _origData.minAmplitude();
		} else {
			return _processedData.minAmplitude();
		}
	}
}
