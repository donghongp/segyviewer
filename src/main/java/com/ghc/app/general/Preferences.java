package com.ghc.app.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Preferences specific for seismic viewer application.
 * Preferences are saved when the application is quit, and reloaded during next start-up.
 * 
 */
public class Preferences extends AbstractPreference {
	private boolean DEBUG = false;
	public static final String FILE_HEADER = "### SEAVIEW preferences file ###";

	public static final String NODE_DIR_SEISMIC = "<seismic_dir>";
	public static final String NODE_DIR_PROPERTIES = "<properties_dir>";
	public static final String NODE_DIR_SCREENDUMP = "<screendump_dir>";
	public static final String NODE_END = "<end>";
	public static final String NODE_RECENT_FILES = "<recent_files>";

	private String _dataDirectoryPath;
	private String _screenDumpDirectoryPath;
	private String _propertiesDirectoryPath;
	private java.util.List<String> _recentFileList = null;

	public Preferences() {
		super("seaview");
		_dataDirectoryPath = AbstractPreference.getCseisDirectory().getAbsolutePath();
		_screenDumpDirectoryPath = AbstractPreference.getCseisDirectory().getAbsolutePath();
		_propertiesDirectoryPath = AbstractPreference.getCseisDirectory().getAbsolutePath();
	}

	public String getDataDirectory() {
		return _dataDirectoryPath;
	}

	public void setDataDirectoryPath(String dir) {
		_dataDirectoryPath = dir;
	}

	public String getPropertiesDirectory() {
		return _propertiesDirectoryPath;
	}

	public java.util.List<String> getRecentFileList() {
		return _recentFileList;
	}

	public void setPropertiesDirectoryPath(String dir) {
		_propertiesDirectoryPath = dir;
	}

	public String getScreenDumpDirectory() {
		return _screenDumpDirectoryPath;
	}

	public void setScreenDumpDirectoryPath(String dir) {
		_screenDumpDirectoryPath = dir;
	}

	public void setRecentFileList(java.util.List<String> list) {
		_recentFileList = list;
	}

	// ----------------------------------------------
	protected void readPreferences(BufferedReader reader) throws IOException {
		String line;
		_recentFileList = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (DEBUG)
				System.out.println("Preference reading...");
			if (line.equalsIgnoreCase(NODE_DIR_SEISMIC)) {
				while ((line = reader.readLine()) != null && !line.trim().equalsIgnoreCase(NODE_END)) {
					_dataDirectoryPath = line.trim();
				}
				if (DEBUG)
					System.out.println("Preference reading: " + NODE_DIR_SEISMIC + " data dir: " + _dataDirectoryPath);
			} else if (line.equalsIgnoreCase(NODE_DIR_SCREENDUMP)) {
				if (DEBUG)
					System.out.println("Preference reading: " + NODE_DIR_SCREENDUMP);
				while ((line = reader.readLine()) != null && !line.trim().equalsIgnoreCase(NODE_END)) {
					_screenDumpDirectoryPath = line.trim();
				}
			} else if (line.equalsIgnoreCase(NODE_DIR_PROPERTIES)) {
				if (DEBUG)
					System.out.println("Preference reading: " + NODE_DIR_PROPERTIES);
				while ((line = reader.readLine()) != null && !line.trim().equalsIgnoreCase(NODE_END)) {
					_propertiesDirectoryPath = line.trim();
				}
			} else if (line.equalsIgnoreCase(NODE_RECENT_FILES)) {
				_recentFileList = new ArrayList<String>();
				if (DEBUG)
					System.out.println("Preference reading: " + NODE_DIR_PROPERTIES);
				while ((line = reader.readLine()) != null && !line.trim().equalsIgnoreCase(NODE_END)) {
					_recentFileList.add(line.trim());
				}
			}

		}
	}

	protected void writePreferences(BufferedWriter writer) throws IOException {
		writer.write(FILE_HEADER);
		writer.newLine();
		writer.newLine();
		writer.write(NODE_DIR_SEISMIC);
		writer.newLine();
		String dir = _dataDirectoryPath;
		if (dir != null) {
			writer.write(dir);
			writer.newLine();
		}
		writer.write(NODE_END);
		writer.newLine();
		writer.write(NODE_DIR_SCREENDUMP);
		writer.newLine();
		dir = _screenDumpDirectoryPath;
		if (dir != null) {
			writer.write(dir);
			writer.newLine();
		}
		writer.write(NODE_END);
		writer.newLine();
		writer.write(NODE_DIR_PROPERTIES);
		writer.newLine();
		dir = _propertiesDirectoryPath;
		if (dir != null) {
			writer.write(dir);
			writer.newLine();
		}
		writer.write(NODE_END);
		writer.newLine();

		writer.write(NODE_RECENT_FILES);
		writer.newLine();
		if (_recentFileList != null) {
			for (int i = 0; i < _recentFileList.size(); i++) {
				writer.write((String) _recentFileList.get(i));
				writer.newLine();
			}
		}
		writer.write(NODE_END);
		writer.newLine();
		writer.newLine();
	}
}
