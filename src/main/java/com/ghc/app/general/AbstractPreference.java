package com.ghc.app.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

/**
 * Base class for user preferences/configuration file
 */
public abstract class AbstractPreference {
	public static final boolean IS_WINDOWS = (java.io.File.separatorChar == '\\');
	public static final String FILE_NAME_START = "preferences_";

	private static File _cseisDirectory;
	private static File _homeDirectory;

	private PrintWriter _errorLogWriter;
	protected File _preferencesFile;
	protected File _errorFile;
	private Vector<String> _exceptionErrorList;

	public static File getCseisDirectory() {
		return _cseisDirectory;
	}

	public static boolean setCseisDirectory() throws Exception {
		FileSystemView fsv = FileSystemView.getFileSystemView();

		String folder = ".cseis";
		_homeDirectory = fsv.getHomeDirectory();
		if (IS_WINDOWS) {
			// Trick to get to C:\\Documents and Settings\\user_name :
			File dirFullPath = new File(_homeDirectory, "");
			_homeDirectory = dirFullPath.getParentFile();
		}

		_cseisDirectory = new File(_homeDirectory, folder);
		if (!_cseisDirectory.exists() || !_cseisDirectory.isDirectory()) {
			if (!_cseisDirectory.mkdir()) {
				JOptionPane.showMessageDialog(null,
						"Error occurred when trying to create Cseis configuration directory\n" +
								_cseisDirectory + "\nProgram will be terminated.",
						"", JOptionPane.ERROR_MESSAGE);
				throw new Exception("Cannot create configuration directory");
			}
			return true;
		}
		return false;
	}

	// --------------------------------------
	public AbstractPreference(String name) {
		_preferencesFile = new File(getCseisDirectory(), "pref_" + name + ".txt");
		_exceptionErrorList = new Vector<String>();
		_errorFile = new File(getCseisDirectory(), "pref_" + name + ".err");
	}

	public boolean readPreferences() {
		if (_preferencesFile.exists()) {
			readPreferences(_preferencesFile);
			return true;
		} else {
			return false;
		}
	}

	public void readPreferences(File file) {
		openErrorLogWriter();
		_exceptionErrorList.clear();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			readPreferences(reader);
			reader.close();
		} catch (IOException e) {
			if (_errorLogWriter != null)
				e.printStackTrace(_errorLogWriter);
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException exc) {
					if (_errorLogWriter != null)
						exc.printStackTrace(_errorLogWriter);
				}
			}
		}
		closeErrorLogWriter();
	}

	// ----------------------------------------------
	public void writePreferences() {
		writePreferences(_preferencesFile);
	}

	public void writePreferences(File file) {
		openErrorLogWriter();
		_exceptionErrorList.clear();

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writePreferences(writer);
			writer.close();
		} catch (IOException e) {
			if (_errorLogWriter != null)
				e.printStackTrace(_errorLogWriter);
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException exc) {
					if (_errorLogWriter != null)
						exc.printStackTrace(_errorLogWriter);
				}
			}
		}
		closeErrorLogWriter();
	}

	protected abstract void readPreferences(BufferedReader reader) throws IOException;

	protected abstract void writePreferences(BufferedWriter writer) throws IOException;

	// ----------------------------------------------
	private void closeErrorLogWriter() {
		if (_errorLogWriter != null) {
			_errorLogWriter.close();
			_errorLogWriter = null;
		}
	}

	// ----------------------------------------------
	private void openErrorLogWriter() {
		try {
			_errorLogWriter = new PrintWriter(new BufferedWriter(new FileWriter(_errorFile)));
		} catch (Exception e) {
			if (_errorLogWriter != null)
				_errorLogWriter = null;
		}
		_errorLogWriter.close();
		_errorLogWriter = null;
	}

}
