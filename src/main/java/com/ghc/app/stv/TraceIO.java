package com.ghc.app.stv;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;

import com.ghc.app.seis.HeaderDef;
import com.ghc.app.seis.ISeismicReader;
import com.ghc.app.seis.SeisConstants;
import com.ghc.app.seis.SeismicTrace;
import com.ghc.app.seis.TraceBuffer;
import com.ghc.app.stv.io.SegyReader;

@Getter
@Setter
public class TraceIO {

	protected TraceIOConfig traceIOConfig;
	private int fileFormat = 0;
	private String fileName = null;
	private ISeismicReader reader;
	private HeaderDef[] traceHeaders;

	public TraceIO(int fileFormat, String fileName, TraceIOConfig traceIOConfig) {
		this.fileFormat = fileFormat;
		this.fileName = fileName;
		this.traceIOConfig = traceIOConfig;
	}


	public void genSeismicReader() {
		reader = genSeismicReader(fileFormat, fileName);

	}
	public ISeismicReader genSeismicReader(int fileFormat, String fileName) {
		ISeismicReader reader;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				throw (new Exception("The input file not found. "));
			}

			switch (fileFormat) {
				case SeisConstants.FORMAT_SEGY:
					reader = new SegyReader(fileName, traceIOConfig.isBigEndian());
					break;

				default:
					reader = new SegyReader(fileName);
			}
			if (reader == null) {
				return null;
			} else if (reader.numTraces() == 0) {
				throw (new Exception("Input file " + fileName + "\ncontains no data"));
			}

			return reader;
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(null, "Unable to open file.\n" +
					"System message:\n" + "'" + exc.getMessage() + "'",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(null, "Unable to open file.\n" +
					"System message:\n" + "'" + exc.getMessage() + "'",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public void genTraceHeader() {
		int numHeaders = reader.numHeaders();
		traceHeaders = new HeaderDef[numHeaders];
		for (int i = 0; i < numHeaders; i++) {
			traceHeaders[i] = new HeaderDef(reader.headerName(i), reader.headerDesc(i), reader.headerType(i));
		}
	}

	public void printTraceHeader() {
		int numHeaders = traceHeaders.length;
		for (int i = 0; i < numHeaders; i++) {
			System.out.println("i=" + i + " traceHeaders.name=" + traceHeaders[i].toString());
		}
	}

	public TraceBuffer readTraceBuffer() {
		return readTraceBuffer(traceIOConfig);
	}

	public TraceBuffer readTraceBuffer(TraceIOConfig traceIOConfig) {
		int traceIndexInFile = traceIOConfig.getFirstTraceIndex();
		int numHeaders = reader.numHeaders();
		int numSamples = reader.numSamples();
		int numTraces = reader.numTraces();
		SeismicTrace trace = new SeismicTrace(numSamples, numHeaders);
		try {
			TraceBuffer traceBuffer = new TraceBuffer();
			int increment = traceIOConfig.getTraceStep();
			int k = 0;
			while (k < traceIOConfig.getNumTraces()) {
				if (traceIndexInFile < numTraces) {
					reader.moveToTrace(traceIndexInFile, 1);
					reader.getNextTrace(trace);
					trace.setOriginalTraceNumber(traceIndexInFile);
					traceBuffer.addTrace(trace);
					trace = new SeismicTrace(numSamples, numHeaders);
					traceIndexInFile += increment;
				} else {
					break;
				}
				k++;
			}
			return traceBuffer;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error occurred when reading next trace from input file. System message:\n\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

}
