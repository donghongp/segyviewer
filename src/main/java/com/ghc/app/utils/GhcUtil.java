package com.ghc.app.utils;

import java.io.File;
import java.util.StringTokenizer;

import javax.swing.filechooser.FileNameExtensionFilter;

public class GhcUtil {

	public static String getProjectFileExtension(int iId) {
		if (iId < 10) {
			return "prj";
		} else if (iId >= 10 && iId <= 19) {
			return "vc1";
		} else if (iId >= 20 && iId <= 29) {
			return "vc2";
		} else if (iId >= 30 && iId <= 39) {
			return "vc3";
		} else if (iId >= 120 && iId <= 129) {
			return "tm2";
		} else if (iId >= 130 && iId <= 139) {
			return "tm3";
		} else if (iId >= 200 && iId <= 299) {
			return "stv";
		} else {
			return ".prj";
		}
	}

	public static String[] parseMultipleFileNames(String pckFileName) {
		if (pckFileName == null || pckFileName.isEmpty()) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(pckFileName, ";");
		String[] fileName = new String[st.countTokens()];
		for (int i = 0; i < fileName.length; i++) {
			fileName[i] = st.nextToken().trim();
		}
		return fileName;
	}

	public static boolean isExtension(String test, String... exts) {
		for (int i = 0; i < exts.length; i++) {
			exts[i] = exts[i].replaceAll("\\.", "");
		}
		return (new FileNameExtensionFilter("extension test", exts)).accept(new File(test));
	}


}
