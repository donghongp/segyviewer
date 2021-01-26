package com.ghc.app.seis;

/**
 * Seismic data.
 */
public class SeismicData {
	private float[] _sampleValues;
	protected float _meanAmplitude; // Mean absolute amplitude = sum( abs(sample[i])
										// )/nsamp
	protected float _dCAmplitude; // Average amplitude = DC = sum( sample[i] )/nsamp
	protected float _maxAmplitude; // Maximum amplitude
	protected float _minAmplitude; // Minimum amplitude
	protected boolean _areAmplitudesComputed;

	public SeismicData() {
		_sampleValues = null;
		_meanAmplitude = 0;
		_dCAmplitude = 0;
		_maxAmplitude = 0;
		_minAmplitude = 0;
		_areAmplitudesComputed = false;
	}

	public SeismicData(int nSamples) {
		this();
		_sampleValues = new float[nSamples];
	}

	public SeismicData(float[] samples) {
		this();
		_sampleValues = samples;
	}

	public int numSamples() {
		return _sampleValues.length;
	}

	public float[] samples() {
		return _sampleValues;
	}

	public void setSamples(float[] samples) {
		_sampleValues = samples;
		_areAmplitudesComputed = false;
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Maximum amplitude in specified trace
	 */
	public float maxAmplitude() {
		if (_areAmplitudesComputed)
			return _maxAmplitude;
		computeTraceAmplitudes();
		return _maxAmplitude;
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Mean (absolute) amplitude of specified trace
	 */
	public float meanAmplitude() {
		if (_areAmplitudesComputed)
			return _meanAmplitude;
		computeTraceAmplitudes();
		return _meanAmplitude;
	}

	/**
	 * 
	 * @param traceIndex
	 * @return DC amplitude (= mean of signed amplitude) of specified trace
	 */
	public float dcAmplitude() {
		if (_areAmplitudesComputed)
			return _dCAmplitude;
		computeTraceAmplitudes();
		return _dCAmplitude;
	}

	/**
	 * 
	 * @param traceIndex
	 * @return Minimum amplitude in specified trace
	 */
	public float minAmplitude() {
		if (_areAmplitudesComputed)
			return _minAmplitude;
		computeTraceAmplitudes();
		return _minAmplitude;
	}

	private void computeTraceAmplitudes() {
		_meanAmplitude = 0;
		_dCAmplitude = 0;
		_maxAmplitude = 0;
		_minAmplitude = 0;

		float sum = 0.0f;
		float sum_abs = 0.0f;
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		for (int isamp = 0; isamp < numSamples(); isamp++) {
			float amp = _sampleValues[isamp];
			if (amp > max) {
				max = amp;
			}
			if (amp < min) {
				min = amp;
			}
			if (amp >= 0)
				sum_abs += amp;
			else
				sum_abs -= amp;
			sum += amp;
		}
		_meanAmplitude = sum_abs / numSamples();
		_dCAmplitude = sum / numSamples();
		_maxAmplitude = max;
		_minAmplitude = min;

		// System.out.println("Amplitudes " + itrc + " " + (sum / _numSamples) + " " +
		// min + " " + max );
	}
}
