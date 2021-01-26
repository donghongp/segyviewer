package com.ghc.app.stv;

import lombok.Getter;
import lombok.Setter;

/**
 * Selection parameters for seismic viewer. Defines selection of currently displayed
 * seismic section.
 */

@Getter
@Setter
public class TraceIOConfig {
	/// Index of first trace tome display (starting at 0)
	public int firstTraceIndex;
	/// Number of traces to display
	public int numTraces;
	public int traceStep;

	public long hdrValue;
	public String hdrName;
	public int hdrIndex;
	public boolean isSelectHdrValue;
	public int selectedHdrIndex;

	public boolean isSelectEnsemble;
	public int selectedEnsIndex;

	private boolean isBigEndian;

	public TraceIOConfig() {
		firstTraceIndex = 0;
		numTraces = 5000;
		traceStep = 1;
		isSelectHdrValue = true;
		isSelectEnsemble = false;
		hdrIndex = -1;
		hdrValue = 0;
		hdrName = "";
		selectedEnsIndex = 0;
		selectedHdrIndex = 0;
		isBigEndian = true;
	}

	public TraceIOConfig(TraceIOConfig sp) {
		firstTraceIndex = sp.firstTraceIndex;
		numTraces = sp.numTraces;
		traceStep = sp.traceStep;
		isSelectHdrValue = sp.isSelectHdrValue;
		isSelectEnsemble = sp.isSelectEnsemble;
		hdrIndex = sp.hdrIndex;
		hdrValue = sp.hdrValue;
		hdrName = sp.hdrName;
		selectedEnsIndex = sp.selectedEnsIndex;
		selectedHdrIndex = sp.selectedHdrIndex;
	}

	public void dump() {
		System.out.println("-----------------------------------");
		System.out.println("Dump of csSelectionParams");
		System.out.println("firstTraceIndex: " + firstTraceIndex);
		System.out.println("numTraces: " + numTraces);
		System.out.println("traceStep: " + traceStep);
		System.out.println("isSelectHdrValue: " + isSelectHdrValue);
		System.out.println("isSelectEnsemble: " + isSelectEnsemble);
		System.out.println("hdrIndex: " + hdrIndex);
		System.out.println("hdrValue: " + hdrValue);
		System.out.println("hdrName: " + hdrName);
		System.out.println("selectedEnsIndex: " + selectedEnsIndex);
		System.out.println("selectedHdrIndex: " + selectedHdrIndex);
	}
}
