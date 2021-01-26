package com.ghc.app.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Base class for user preferences/configuration file
 */
public class ProjectPreference {
	public static final String NODE_DIR_SEISMIC = "<seismic_dir>";
	public static final String NODE_DIR_PROPERTIES = "<properties_dir>";
	public static final String NODE_DIR_SCREENDUMP = "<screendump_dir>";
	public static final String NODE_END = "<end>";
	public static final String NODE_RECENT_FILES = "<recent_files>";
	public static final String NODE_General = "<General>";

	private String fileHeader = null;
	private String screenDumpDirectoryPath;
	private String propertiesDirectoryPath;
	private String[] general = new String[30];

	private static File rootDirectory;
	protected File preferencesFile;
	private ArrayList<String> recentFileList = null;


	public ProjectPreference(String name) {
		preferencesFile = new File(getRootDirectory(), "pref_" + name + ".txt");
		fileHeader = "### " + name + " preferences file ###";
		screenDumpDirectoryPath = getRootDirectory().getAbsolutePath();
		propertiesDirectoryPath = getRootDirectory().getAbsolutePath();

	}

	public static File getRootDirectory() {
		return rootDirectory;
	}

	public static boolean setRootDirectory(String folder) throws Exception {
		File directory = new File(System.getProperty("user.dir"));

		rootDirectory = new File(directory, folder);
		if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
			if (!rootDirectory.mkdir()) {
				throw new Exception("Cannot create configuration directory");
			}
			return true;
		}
		return false;
	}

	public ArrayList<String> getRecentFileList() {
		return recentFileList;
	}

	public void setRecentFileList(ArrayList<String> recentFileList) {
		this.recentFileList = recentFileList;
	}

	public String getRecentFile() {
		if (recentFileList == null || recentFileList.isEmpty())
			return System.getProperty("user.dir");
		else
			return recentFileList.get(0);
	}

	public boolean readPreferences() {
		if (preferencesFile.exists()) {
			readPreferences(preferencesFile);
			return true;
		} else {
			return false;
		}
	}

	public void readPreferences(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			readPreferences(reader);
			reader.close();
		} catch (IOException e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException exc) {
				}
			}
		}
	}

	// ----------------------------------------------
	public void writePreferences() {
		writePreferences(preferencesFile);
	}

	public void writePreferences(File file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writePreferences(writer);
			writer.close();
		} catch (IOException e) {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException exc) {
				}
			}
		}
	}

	public void addRecentFileList(String fileName) {
		if (recentFileList == null) {
			recentFileList = new ArrayList<String>();
		}
		if (recentFileList.size() > 30) {
			recentFileList.remove(0);
		}
		recentFileList.add(fileName);
	}

	// ----------------------------------------------
	protected void readPreferences(BufferedReader reader) throws IOException {
		String line;
		recentFileList = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.equalsIgnoreCase(NODE_RECENT_FILES)) {
				recentFileList = new ArrayList<String>();
				while ((line = reader.readLine()) != null && !line.trim().equalsIgnoreCase(NODE_END)) {
					recentFileList.add(line.trim());
				}
			}

		}
	}

	protected void writePreferences(BufferedWriter writer) throws IOException {
		writer.write(fileHeader);
		writer.newLine();
		writer.newLine();
		writer.write(NODE_DIR_SCREENDUMP);
		writer.newLine();
		if (screenDumpDirectoryPath != null) {
			writer.write(screenDumpDirectoryPath);
			writer.newLine();
		}
		writer.write(NODE_END);
		writer.newLine();
		writer.write(NODE_DIR_PROPERTIES);
		writer.newLine();
		if (propertiesDirectoryPath != null) {
			writer.write(propertiesDirectoryPath);
			writer.newLine();
		}
		writer.write(NODE_END);
		writer.newLine();

		writer.newLine();

		int k = 0;
		for (int i = 0; i < general.length; i++) {
			if (general[i] != null) {
				k++;
			}
		}
		if (k > 0) {
			writer.write(NODE_General);
			writer.newLine();
			for (int i = 0; i < general.length; i++) {
				if (general[i] != null) {
					writer.write(general[i]);
					writer.newLine();
				}
			}
			writer.write(NODE_END);
			writer.newLine();
		}

		writer.write(NODE_RECENT_FILES);
		writer.newLine();
		if (recentFileList != null) {
			for (int i = 0; i < recentFileList.size(); i++) {
				writer.write((String) recentFileList.get(i));
				writer.newLine();
			}
		}
		writer.write(NODE_END);
		writer.newLine();
		writer.newLine();
	}


}
