package com.ghc.app.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.io.FilenameUtils;

import com.ghc.app.seis.ISeismicReader;
import com.ghc.app.seis.SeisConstants;
import com.ghc.app.stv.io.SegyReader;

@Getter
@Setter
// @NoArgsConstructor
// @AllArgsConstructor
@ToString
public class ProjectStv extends Project {

	private int fileFormat = 0;
	private String fileOrDirectoryName = null;
	private ISeismicReader seismicReader;

	public ProjectStv(String inputFileName) {
		super(inputFileName);
		if (inputFileName != null) {
			read(inputFileName);
		}
	}

	public ProjectStv(String projectFileName, int iId, int iUnit, int fileFormat, String fileOrDirectoryName) {
		super(projectFileName, iId, iUnit);
		this.fileFormat = fileFormat;
		this.fileOrDirectoryName = fileOrDirectoryName;
	}

	public void genSeismicReader(int fileIndex) {
		File file = new File(fileOrDirectoryName);
		seismicReader = null;
		if (!file.exists()) {
			return;
		}

		if (file.isFile()) {
			seismicReader = genSeismicReader(fileFormat, fileOrDirectoryName);
		} else {
			File[] listOfFiles = new File(fileOrDirectoryName).listFiles();
			String fullPath = FilenameUtils.getFullPathNoEndSeparator(fileOrDirectoryName) + file.separator;
			for (int i = 0; i < listOfFiles.length; i++) {
				if (fileIndex == i) {
					if (listOfFiles[i].isFile()) {
						seismicReader = genSeismicReader(fileFormat, fullPath + listOfFiles[i].getName());
					} else {
						seismicReader = null;
					}
				}
			}
		}
	}

	private ISeismicReader genSeismicReader(int fileFormat, String filename) {
		ISeismicReader reader;
		try {
			File file = new File(filename);
			if (!file.exists()) {
				throw (new Exception("File not found: " + filename));
			}

			if (fileFormat == SeisConstants.FORMAT_ALL) {
				if (filename.toLowerCase().endsWith("cseis") || filename.toLowerCase().endsWith("oseis")) {
					this.fileFormat = SeisConstants.FORMAT_CSEIS;
				} else if (filename.toLowerCase().endsWith("segy") || filename.toLowerCase().endsWith("sgy")) {
					this.fileFormat = SeisConstants.FORMAT_SEGY;
				} else if (filename.toLowerCase().endsWith("su")) {
					this.fileFormat = SeisConstants.FORMAT_SU;
				} else if (filename.toLowerCase().endsWith("txt") || filename.toLowerCase().endsWith("asc")) {
					this.fileFormat = SeisConstants.FORMAT_ASCII;
				} else if (filename.toLowerCase().endsWith("segd") || filename.toLowerCase().endsWith("sgd")
						|| filename.toLowerCase().endsWith("gunlink")) {
					this.fileFormat = SeisConstants.FORMAT_SEGD;
				} else {
					this.fileFormat = SeisConstants.FORMAT_SEGY;
				}
			} else {
				this.fileFormat = fileFormat;
			}

			switch (this.fileFormat) {
				case SeisConstants.FORMAT_SEGY:
					reader = new SegyReader(filename);
					break;
				default:
					reader = new SegyReader(filename);
			}
			if (reader == null) {
				return null;
			} else if (reader.numTraces() == 0) {
				throw (new Exception("Input file " + filename + "\ncontains no data"));
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

	protected void readProject(BufferedReader reader) throws IOException {
		String line = null;
		int k = 0;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			if (k == 0) {
				fileFormat = Integer.parseInt(parseLine(line));
			} else if (k == 1) {
				fileOrDirectoryName = parseFileName(line);
			}

			k++;
		}
	}

	protected void writeProject(BufferedWriter writer) throws IOException {
		writeLine(writer, "#File format: ");
		writeLine(writer, String.format("%d", fileFormat));
		writeLine(writer, "#File or directory Name: ");
		writeFileName(writer, fileOrDirectoryName);
	}

}
