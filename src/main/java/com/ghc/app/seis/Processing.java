package com.ghc.app.seis;

/**
 * Processing methods
 */
public class Processing {
	public static final int NONE = 0;
	public static final int REMOVE_DC = 1;

	/**
	 * Remove DC bias component
	 * 
	 * @param samplesIn Input data
	 * @param dcValue DC value to remove
	 * @return Processed data
	 */
	public static float[] removeDC(float[] samplesIn, float dcValue) {
		int nSamples = samplesIn.length;
		float[] samplesOut = new float[nSamples];
		for (int isamp = 0; isamp < nSamples; isamp++) {
			samplesOut[isamp] = samplesIn[isamp] - dcValue;
		}
		return samplesOut;
	}

	public static void computeAmplitudes(Float mean, Float dc, Float min, Float max, float[] samples) {
		/*
		 * int nTraces = numTraces(); _totalMaxAmplitude = -Float.MAX_VALUE;
		 * _totalMinAmplitude = Float.MAX_VALUE; _totalMeanAmplitude = 0.0f; for( int
		 * itrc = 0; itrc < nTraces; itrc++ ) { csSeismicData data; if( _processingType
		 * == csProcessing.NONE ) { data = originalTrace(itrc).data(); } else { data =
		 * _processedData[itrc]; } _totalMeanAmplitude += data.meanAmplitude(); float
		 * max = data.maxAmplitude(); float min = data.minAmplitude(); if( max >
		 * _totalMaxAmplitude ) _totalMaxAmplitude = max; if( min < _totalMinAmplitude
		 * ) _totalMinAmplitude = min; } _totalMeanAmplitude /=
		 * _origTraceBuffer.numSamples();
		 */
	}
}
