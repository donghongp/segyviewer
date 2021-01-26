package com.ghc.app.seis;

import java.util.ArrayList;

/**
 * Trace buffer.
 * Stores any number of seismic traces.
 */
public class TraceBuffer implements ISeismicTraceBuffer {
	protected ArrayList<SeismicTrace> _traces;

	protected boolean _areAmplitudesComputed = false;
	protected float _totalMinAmplitude;
	protected float _totalMaxAmplitude;
	protected float _totalMeanAmplitude;

	/**
	 * Constructor.
	 * 
	 * @param numSamples Number of samples
	 * @param numHeaders Number of headers
	 */
	public TraceBuffer() {
		_traces = new ArrayList<SeismicTrace>();
		_areAmplitudesComputed = false;
	}

	/**
	 * Add trace to trace buffer
	 * 
	 * @param samples Sample values (size of array must be the same as number of samples
	 *            specified in constructor)
	 * @param headers Header values (size of array must be the same as number of headers
	 *            specified in constructor)
	 */
	public void addTrace(float[] samples, Header[] headers) {
		addTrace(new SeismicTrace(samples, headers));
	}

	/**
	 * Add trace to trace buffer
	 *
	 * @param trace Seismic trace to add
	 */
	public void addTrace(SeismicTrace trace) {
		_areAmplitudesComputed = false;
		_traces.add(trace);
	}

	/**
	 * Add trace to beginning of trace buffer
	 *
	 * @param trace Seismic trace to add
	 */
	public void addTraceAtStart(SeismicTrace trace) {
		_areAmplitudesComputed = false;
		_traces.add(0, trace);
	}

	/**
	 * 
	 * @return Number of traces in trace buffer
	 */
	public int numTraces() {
		return _traces.size();
	}

	/**
	 * 
	 * @return Number of samples in each trace
	 */
	public int numSamples() {
		return _traces.get(0).numSamples();
	}

	/**
	 * 
	 * @return Number of headers in each trace
	 */
	public int numHeaders() {
		return _traces.get(0).numHeaders();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Handle to sample values of specified trace
	 */
	public float[] samples(int traceIndex) {
		return originalTrace(traceIndex).samples();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Handle to header values of specified trace
	 */
	public Header[] headerValues(int traceIndex) {
		return _traces.get(traceIndex).headerValues();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Specified seismic trace
	 */
	public SeismicTrace originalTrace(int traceIndex) {
		return _traces.get(traceIndex);
	}

	public int originalTraceNumber(int traceIndex) {
		return _traces.get(traceIndex).originalTraceNumber();
	}

	/**
	 * Clear trace buffer. Remove all traces.
	 */
	public void clear() {
		_traces.clear();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Maximum amplitude in specified trace
	 */
	public float maxAmplitude(int traceIndex) {
		return originalTrace(traceIndex).maxAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Mean (absolute) amplitude of specified trace
	 */
	public float meanAmplitude(int traceIndex) {
		return originalTrace(traceIndex).meanAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return DC amplitude (= mean of signed amplitude) of specified trace
	 */
	public float dcAmplitude(int traceIndex) {
		return originalTrace(traceIndex).dcAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Minimum amplitude in specified trace
	 */
	public float minAmplitude(int traceIndex) {
		return originalTrace(traceIndex).minAmplitude();
	}

	/**
	 * 
	 * @return Maximum amplitude in all traces
	 */
	public float maxTotalAmplitude() {
		if (_areAmplitudesComputed)
			return _totalMaxAmplitude;
		computeTotalAmplitudes();
		return _totalMaxAmplitude;
	}

	/**
	 * 
	 * @return Minimum amplitude in all traces
	 */
	public float minTotalAmplitude() {
		if (_areAmplitudesComputed)
			return _totalMinAmplitude;
		computeTotalAmplitudes();
		return _totalMinAmplitude;
	}

	/**
	 * 
	 * @return Mean amplitude in all traces
	 */
	public float meanTotalAmplitude() {
		if (_areAmplitudesComputed)
			return _totalMeanAmplitude;
		computeTotalAmplitudes();
		return _totalMeanAmplitude;
	}

	public void computeTotalAmplitudes() {
		int nTraces = numTraces();
		_totalMaxAmplitude = -Float.MAX_VALUE;
		_totalMinAmplitude = Float.MAX_VALUE;
		_totalMeanAmplitude = 0.0f;
		for (int itrc = 0; itrc < nTraces; itrc++) {
			SeismicData data = originalTrace(itrc).data();
			_totalMeanAmplitude += data.meanAmplitude();
			float max = data.maxAmplitude();
			float min = data.minAmplitude();
			if (max > _totalMaxAmplitude)
				_totalMaxAmplitude = max;
			if (min < _totalMinAmplitude)
				_totalMinAmplitude = min;
		}
		_totalMeanAmplitude /= nTraces;
		_areAmplitudesComputed = true;
	}
}
