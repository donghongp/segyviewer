package com.ghc.app.stv.display;

import com.ghc.app.general.Standard;
import com.ghc.app.general.StvConst;

/**
 * Trace/sample information for current trace/sample.
 */
public class SampleInfo {
	/// Seismic amplitude
	public double amplitude;
	/// Depth [m]
	public double depth;

	public int domainType;
	/// Offset
	public double offset;
	/// Time [s]
	public double time;
	/// Sample index, starting at 0
	public int sample;
	/// Sample index, including decimal places, starting at 0
	public double sampleDouble;
	/// Trace number (interval counting, starting with 0 from the left)
	public int trace;
	/// Trace number, including decimal places
	public double traceDouble;

	public SampleInfo() {
		time = Standard.ABSENT_VALUE;
		depth = Standard.ABSENT_VALUE;
		amplitude = 0.0;
		offset = Standard.ABSENT_VALUE;
		trace = Standard.ABSENT_VALUE_INT;
		traceDouble = Standard.ABSENT_VALUE;
		domainType = 0;
	}

	public SampleInfo(SampleInfo sInfo) {
		time = sInfo.time;
		depth = sInfo.depth;
		amplitude = sInfo.amplitude;
		offset = sInfo.offset;
		trace = sInfo.trace;
		traceDouble = sInfo.traceDouble;
		domainType = sInfo.domainType;
	}

	public boolean isZoomable(SampleInfo other) {
		int dx = sample - other.sample;
		dx = dx >= 0 ? dx : -dx;
		int dy = trace - other.trace;
		dy = dy >= 0 ? dy : -dy;
		return dx > StvConst.MIN_ZOOM_WIDTH || dy > StvConst.MIN_ZOOM_WIDTH;
	}
}
