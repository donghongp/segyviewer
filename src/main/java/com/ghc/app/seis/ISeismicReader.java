package com.ghc.app.seis;

/**
 * Interface to be implemented by seismic data readers.
 */
public interface ISeismicReader {
	/**
	 * @return Number of traces
	 */
	public int numTraces();

	/**
	 * @return Number of samples
	 */
	public int numSamples();

	/**
	 * @return Number of trace headers
	 */
	public int numHeaders();

	/**
	 * @return Sample interval in [ms]
	 */
	public float sampleInt();

	/**
	 * @return true if data is in frequency domain
	 */
	public boolean isFrequencyDomain();

	/**
	 * Retrieve next trace from input file
	 * 
	 * @param trace New seismic trace
	 * @return true if another trace was read in
	 */
	public boolean getNextTrace(SeismicTrace trace) throws Exception;

	/**
	 * Move (file pointer) to specified trace. The next call to getNextTrace() retrieves
	 * the trace with the specified trace index.
	 * 
	 * @param traceIndex (i) Trace index, starting at 0 for first trace
	 * @return true if operation was successful, false if error occurred (e.g. specified
	 *         trace does not exist)
	 */
	public boolean moveToTrace(int traceIndex, int numTracesToRead) throws Exception;

	/**
	 * @param hdrIndex Header index
	 * @return Value of integer trace header
	 */
	public int hdrIntValue(int hdrIndex);

	/**
	 * @param hdrIndex Header index
	 * @return Value of float trace header
	 */
	public float hdrFloatValue(int hdrIndex);

	/**
	 * @param hdrIndex Header index
	 * @return Value of double trace header
	 */
	public double hdrDoubleValue(int hdrIndex);

	/**
	 * @param hdrIndex Header index
	 * @return Name of trace header
	 */
	public String headerName(int hdrIndex);

	/**
	 * @param hdrIndex Header index
	 * @return Description of trace header
	 */
	public String headerDesc(int hdrIndex);

	/**
	 * @param hdrIndex Header index
	 * @return Type of trace header
	 */
	public int headerType(int hdrIndex);

	public void setHeaderToPeek(String headerName) throws Exception;

	public boolean peekHeaderValue(int traceIndex, PeekValue value);

	/**
	 * Close file
	 */
	public void closeFile();
}
