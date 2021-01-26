package com.ghc.app.stv;

import lombok.Getter;
import lombok.Setter;

import com.ghc.app.general.ColorMap;
import com.ghc.app.seis.HeaderDef;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.display.ViewSettings;

@Getter
@Setter
public class TraceContext {

	private ISeismicTraceBuffer traceBuffer;
	private double sampleInt;
	private HeaderDef[] headers;

	private ViewSettings viewSettings;
	private TraceIOConfig traceIOConfig;

	public TraceContext() {
		viewSettings = new ViewSettings();
		viewSettings.wiggleColorMap.setColorMapType(ColorMap.COLOR_MAP_TYPE_32BIT);
		viewSettings.vaColorMap.setColorMapType(ColorMap.COLOR_MAP_TYPE_32BIT);
		traceIOConfig = new TraceIOConfig();
	}

	/**
	 * Constructor Use this constructor seismic traces are already available before
	 * constructing the seismic pane.
	 * 
	 * @param buffer Trace buffer containing all seismic traces
	 * @param sampleInt Sample interval [ms]
	 * @param headers Header definitions for all trace headers (size of array should be
	 *            the same as number of trace headers in seismic trace buffer)
	 */
	// public TraceContext(ISeismicTraceBuffer traceBuffer, double sampleInt, HeaderDef[]
	// headers, TraceIOConfig traceIOConfig) {
	// this.traceBuffer = traceBuffer;
	// this.sampleInt = sampleInt;
	// this.headers = headers;
	// this.traceIOConfig = traceIOConfig;
	// }

}
