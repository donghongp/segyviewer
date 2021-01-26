package com.ghc.app.seis;

/**
 * Seismic trace.
 */
public class SeismicTrace {
	private Header[] _headerValues;
	private SeismicData _data;
	private int _originalTraceNumber;

	public SeismicTrace(int nSamples, int nHeaders) {
		_headerValues = new Header[nHeaders];
		for (int i = 0; i < nHeaders; i++) {
			_headerValues[i] = new Header();
		}
		_data = new SeismicData(nSamples);
		_originalTraceNumber = 1;
	}

	public SeismicTrace(float[] samples, Header[] headers) {
		this(samples, headers, 1);
	}

	public SeismicTrace(float[] samples, Header[] headers, int originalTraceNumber) {
		_headerValues = headers;
		_data = new SeismicData(samples);
		_originalTraceNumber = originalTraceNumber;
	}

	public SeismicData data() {
		return _data;
	}

	public float[] samples() {
		return _data.samples();
	}

	public void setSamples(float[] samples) {
		_data.setSamples(samples);
	}

	public Header[] headerValues() {
		return _headerValues;
	}

	public int numSamples() {
		return _data.numSamples();
	}

	public int numHeaders() {
		return _headerValues.length;
	}

	/**
	 * Get original trace number (starting at 1 for first trace)
	 * 
	 * @return
	 */
	public int originalTraceNumber() {
		return _originalTraceNumber;
	}

	/**
	 * Set original trace number (starting at 1 for first trace)
	 * 
	 * @param traceNumber
	 */
	public void setOriginalTraceNumber(int traceNumber) {
		_originalTraceNumber = traceNumber;
	}
	/*
	 * public void setHeader( int index, double value ) { if( index >= 0 && index <
	 * _numHeaders ) { _headerValues[index].setValue( value ); } }
	 */

	public void setDoubleHeader(int index, double value) {
		if (index >= 0 && index < numHeaders()) {
			_headerValues[index].setValue(value);
		}
	}

	public void setLongHeader(int index, long value) {
		if (index >= 0 && index < numHeaders()) {
			_headerValues[index].setValue(value);
		}
	}

	public void setFloatHeader(int index, float value) {
		if (index >= 0 && index < numHeaders()) {
			_headerValues[index].setValue(value);
		}
	}

	public void setIntHeader(int index, int value) {
		if (index >= 0 && index < numHeaders()) {
			_headerValues[index].setValue(value);
		}
	}

	public void setStringHeader(int index, String value) {
		if (index >= 0 && index < numHeaders()) {
			_headerValues[index].setValue(value);
		}
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Maximum amplitude in specified trace
	 */
	public float maxAmplitude() {
		return _data.maxAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Mean (absolute) amplitude of specified trace
	 */
	public float meanAmplitude() {
		return _data.meanAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return DC amplitude (= mean of signed amplitude) of specified trace
	 */
	public float dcAmplitude() {
		return _data.dcAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Minimum amplitude in specified trace
	 */
	public float minAmplitude() {
		return _data.minAmplitude();
	}

	public static void main(String[] args) {
		SeismicTrace t = new SeismicTrace(100, 20);
		t.setIntHeader(0, 123);
	}
}
