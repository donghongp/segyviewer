package com.ghc.app.stv.io;

import java.nio.ByteOrder;

import com.ghc.app.seis.HeaderDef;
import com.ghc.app.seis.ISeismicReader;
import com.ghc.app.seis.PeekValue;
import com.ghc.app.seis.SeismicTrace;

public class SegyReader extends SegyReaderImpl implements ISeismicReader {

	private int traceIndex = 0;
	private int numTracesToRead = 1;

	private HeaderDef[] headerDef = null;
	private int[] headerValue = null;

	/**
	 * Constructs an image with specified SEG-Y file name. Assumes byte order is
	 * BIG_ENDIAN, which is the SEG-Y standard.
	 * <p>
	 * This method may
	 * 
	 * @param fileName name of the SEG-Y file that contains the image.
	 */
	public SegyReader(String fileName) {
		this(fileName, ByteOrder.BIG_ENDIAN);
	}

	public SegyReader(String fileName, boolean bigEndian) {
		this(fileName, bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Constructs an image with specified SEG-Y file name and byte order.
	 * 
	 * @param fileName name of the SEG-Y file that contains the image.
	 * @param byteOrder byte order, either BIG_ENDIAN or LITTLE_ENDIAN.
	 */
	public SegyReader(String fileName, ByteOrder byteOrder) {
		super(fileName, byteOrder);
	}

	public void genHeaderDef() {
		headerDef = new HeaderDef[numHeaders()];
		for (int i = 0; i < headerDef.length; i++) {
			String field = _traceHeaderFields[i];
			headerDef[i] = new HeaderDef(field.substring(0, 11), field.substring(11), HeaderDef.TYPE_INT);
		}
	}

	public HeaderDef[] genFileHeaderDef() {
		int nHeaders = _binaryHeaderFields.length;
		HeaderDef[] headerDef = new HeaderDef[nHeaders];
		for (int i = 0; i < headerDef.length; i++) {
			String field = _binaryHeaderFields[i];
			headerDef[i] = new HeaderDef(field.substring(0, 9), field.substring(11), HeaderDef.TYPE_INT);
		}
		return headerDef;
	}

	/**
	 * Closes the SEG-Y file corresponding to this image.
	 */
	public void closeFile() {
		close();
	}

	/**
	 * @return Number of traces
	 */
	public int numTraces() {
		return countTraces();
	}

	/**
	 * @return Number of samples
	 */
	public int numSamples() {
		return getN1();
	};

	/**
	 * @return Number of trace headers
	 */
	public int numHeaders() {
		return _traceHeaderFields.length;
	}

	/**
	 * @return Sample interval in [ms]
	 */
	public float sampleInt() {
		return (float) (getD1() * 1000);
	};

	/**
	 * @return true if data is in frequency domain
	 */
	public boolean isFrequencyDomain() {
		return false;
	}

	/**
	 * Retrieve next trace from input file
	 * 
	 * @param trace New seismic trace
	 * @return true if another trace was read in
	 */
	public boolean getNextTrace(SeismicTrace trace) throws Exception {
		try {
			getTrace(traceIndex, trace.data().samples());
			int [] headerValue = getTraceHeaderValue(traceIndex);
			for(int i=0; i<headerValue.length; i++) {
				trace.setIntHeader(i, headerValue[i]);
			}
			int originalTraceNumberIndex = 1;
			trace.setOriginalTraceNumber(headerValue[originalTraceNumberIndex]);
			
			return true;
		} catch (Exception e) {
			throw (e);
		}
	}

	/**
	 * Move (file pointer) to specified trace. The next call to getNextTrace() retrieves
	 * the trace with the specified trace index.
	 * 
	 * @param traceIndex (i) Trace index, starting at 0 for first trace
	 * @return true if operation was successful, false if error occurred (e.g. specified
	 *         trace does not exist)
	 */
	public boolean moveToTrace(int traceIndex, int numTracesToRead) throws Exception {
		try {
			this.traceIndex = traceIndex;
			this.numTracesToRead = numTracesToRead;
			return true;
		} catch (Exception e) {
			throw (e);
		}
	}

	public int hdrIntValue(int hdrIndex) {
		if (headerValue != null) {
			return headerValue[hdrIndex];
		}
		return 1;
	}

	public float hdrFloatValue(int hdrIndex) {
		return hdrIntValue(hdrIndex);
	}

	public double hdrDoubleValue(int hdrIndex) {
		return hdrIntValue(hdrIndex);
	}

	public String headerName(int hdrIndex) {
		if (headerDef == null) {
			genHeaderDef();
		}
		return headerDef[hdrIndex].name;
	}

	public String headerDesc(int hdrIndex) {
		if (headerDef == null) {
			genHeaderDef();
		}
		return headerDef[hdrIndex].desc;
	}

	public int headerType(int hdrIndex) {
		if (headerDef == null) {
			genHeaderDef();
		}
		return headerDef[hdrIndex].type;
	}

	public void setHeaderToPeek(String headerName) throws Exception {

	}

	public boolean peekHeaderValue(int traceIndex, PeekValue value) {
		return true;
	}
}

