package com.ghc.app.seis;

/**
 * Interface to be implemented by seismic trace buffers
 */
public interface ISeismicTraceBuffer {
	public int numTraces();

	public int numSamples();

	public int numHeaders();

	public float[] samples(int traceIndex);

	public Header[] headerValues(int traceIndex);

	public void clear();

	public float maxAmplitude(int traceIndex);

	public float meanAmplitude(int traceIndex);

	public float dcAmplitude(int traceIndex);

	public float minAmplitude(int traceIndex);

	public float maxTotalAmplitude();

	public float minTotalAmplitude();

	public float meanTotalAmplitude();

	public int originalTraceNumber(int traceIndex);
}
