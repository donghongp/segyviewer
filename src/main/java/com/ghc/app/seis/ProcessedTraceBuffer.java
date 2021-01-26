package com.ghc.app.seis;

/**
 * Trace buffer, processed data.
 * Stores any number of seismic traces.
 */
public class ProcessedTraceBuffer implements ISeismicTraceBuffer {
	private TraceBuffer _origTraceBuffer;
	protected SeismicData[] _processedData;
	private int _processingType;

	protected float _totalMinAmplitude;
	protected float _totalMaxAmplitude;
	protected float _totalMeanAmplitude;

	/**
	 * Constructor.
	 * 
	 * @param numSamples Number of samples
	 * @param numHeaders Number of headers
	 */
	public ProcessedTraceBuffer(TraceBuffer origBuffer, int procType) {
		_origTraceBuffer = origBuffer;
		_processedData = null;
		_processingType = Processing.NONE;
		applyProcessing(procType);
	}

	TraceBuffer getUnprocessedTraceBuffer() {
		return _origTraceBuffer;
	}

	public void applyProcessing(int procType) {
		if (procType == _processingType)
			return;
		int nTraces = numTraces();
		if (procType != Processing.NONE && _processedData == null || _processedData.length != nTraces) {
			_processedData = new SeismicData[nTraces];
		}
		_processingType = procType;
		switch (_processingType) {
			case Processing.NONE:
				_processedData = null;
				break;
			case Processing.REMOVE_DC:
				for (int itrc = 0; itrc < nTraces; itrc++) {
					_processedData[itrc] = new SeismicData(
							Processing.removeDC(originalTrace(itrc).samples(), originalTrace(itrc).dcAmplitude()));
				}
				break;
			default:
				_processingType = Processing.NONE;
				break;
		}
		computeTotalAmplitudes();
	}

	public int numTraces() {
		return _origTraceBuffer.numTraces();
	}

	public int numSamples() {
		return _origTraceBuffer.numSamples();
	}

	public int numHeaders() {
		return _origTraceBuffer.numHeaders();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Handle to sample values of specified trace
	 */
	public float[] samples(int traceIndex) {
		if (_processingType == Processing.NONE) {
			return _origTraceBuffer.originalTrace(traceIndex).samples();
		} else {
			return _processedData[traceIndex].samples();
		}
	}

	public Header[] headerValues(int traceIndex) {
		return _origTraceBuffer.headerValues(traceIndex);
	}

	public SeismicTrace originalTrace(int traceIndex) {
		return _origTraceBuffer.originalTrace(traceIndex);
	}

	public int originalTraceNumber(int traceIndex) {
		return _origTraceBuffer.originalTrace(traceIndex).originalTraceNumber();
	}

	public void clear() {
		_origTraceBuffer.clear();
	}

	public float maxAmplitude(int traceIndex) {
		return _processedData[traceIndex].maxAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Mean (absolute) amplitude of specified trace
	 */
	public float meanAmplitude(int traceIndex) {
		return _processedData[traceIndex].meanAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return DC amplitude (= mean of signed amplitude) of specified trace
	 */
	public float dcAmplitude(int traceIndex) {
		return _processedData[traceIndex].dcAmplitude();
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Minimum amplitude in specified trace
	 */
	public float minAmplitude(int traceIndex) {
		return _processedData[traceIndex].minAmplitude();
	}

	/**
	 * 
	 * @return Maximum amplitude in all traces
	 */
	public float maxTotalAmplitude() {
		return _totalMaxAmplitude;
	}

	/**
	 * 
	 * @return Minimum amplitude in all traces
	 */
	public float minTotalAmplitude() {
		return _totalMinAmplitude;
	}

	/**
	 * 
	 * @return Mean amplitude in all traces
	 */
	public float meanTotalAmplitude() {
		return _totalMeanAmplitude;
	}

	private void computeTotalAmplitudes() {
		int nTraces = numTraces();
		_totalMaxAmplitude = -Float.MAX_VALUE;
		_totalMinAmplitude = Float.MAX_VALUE;
		_totalMeanAmplitude = 0.0f;
		for (int itrc = 0; itrc < nTraces; itrc++) {
			SeismicData data = _processedData[itrc];
			_totalMeanAmplitude += data.meanAmplitude();
			float max = data.maxAmplitude();
			float min = data.minAmplitude();
			if (max > _totalMaxAmplitude)
				_totalMaxAmplitude = max;
			if (min < _totalMinAmplitude)
				_totalMinAmplitude = min;
		}
		_totalMeanAmplitude /= _origTraceBuffer.numSamples();

		// System.out.println("Amplitudes " + _totalMinAmplitude + " " +
		// _totalMaxAmplitude + " " + _totalMeanAmplitude );
	}
}
