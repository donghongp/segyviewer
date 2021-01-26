package com.ghc.app.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.io.FilenameUtils;

import com.ghc.app.utils.GhcUtil;

@Getter
@Setter
@NoArgsConstructor
// @RequiredArgsConstructor
// @Log4j
@ToString
public abstract class Project {
	protected int iUnit = 0; // 0: feet; 1: meter; 2: km;
	protected int iId = 20;
	protected String fullPath = null;
	protected String baseName = null;
	protected String fileExtension = null;
	public String version = null;

	public Project(String projectFileName) {
		this(projectFileName, 20, 0);
	}

	public Project(String projectFileName, int iId, int iUnit) {
		this.iId = iId;
		this.iUnit = iUnit;
		if (projectFileName != null) {
			setProjectName(projectFileName);
		}
		fileExtension = GhcUtil.getProjectFileExtension(iId);
	}

	public boolean isUntitled() {
		return baseName.contains("untitled");
	}

	public String getFullPathNoEndSeparator() {
		return FilenameUtils.getFullPathNoEndSeparator(fullPath);
	}

	public String getProjectFileName() {
		return getFullName() + "." + fileExtension;
	}

	public String getFullName() {
		return fullPath + baseName;
	}

	public void setProjectName(String projectFileName) {
		fullPath = FilenameUtils.getFullPath(projectFileName);
		baseName = FilenameUtils.getBaseName(projectFileName);
		fileExtension = FilenameUtils.getExtension(projectFileName);
	}

	public String getResultsCwd() {
		if (isUntitled()) {
			return createFolder(System.getProperty("user.home") + File.separator + "untitled_Results");
		} else {
			return createFolder(getFullPath() + getBaseName() + "_Results");
		}
	}

	public String getImagesCwd() {
		return createFolder(getResultsCwd() + File.separator + "Images");
	}

	public String getScratchCwd() {
		return createFolder(getResultsCwd() + File.separator + "Scratch");
	}

	protected String walkingThroughFiles(int index, String srcPath, String extension) {
		String name = null;
		File[] listOfFiles = new File(srcPath).listFiles();

		int k = 0;
		for (int i = 0; i < listOfFiles.length; i++) {
			name = listOfFiles[i].getName();
			if (listOfFiles[i].isFile()) {
				if (FilenameUtils.isExtension(name, extension) && k == index)
					return srcPath + File.separator + name;
				k++;
			} else {
				walkingThroughFiles(index, srcPath + File.separator + name, extension);
			}
		}
		return null;
	}

	public void copyFileTree(String sourcePath, String targetPath) {
		try {
			Path source = Paths.get(sourcePath);
			Path target = Paths.get(targetPath);
			Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
					Integer.MAX_VALUE, new CopyDirectory(source, target));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private class CopyDirectory extends SimpleFileVisitor<Path> {

		private Path source;
		private Path target;

		public CopyDirectory(Path source, Path target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			// System.out.println("Copying " + source.relativize(file));
			Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
			Path targetDirectory = target.resolve(source.relativize(directory));
			try {
				// System.out.println("Copying " + source.relativize(directory));
				Files.copy(directory, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
			} catch (FileAlreadyExistsException e) {
				if (!Files.isDirectory(targetDirectory)) {
					throw e;
				}
			}
			return FileVisitResult.CONTINUE;
		}
	}

	public void deleteFileTree(String selectedFilePath) {
		try {
			Files.walkFileTree(Paths.get(selectedFilePath), new DeleteDirectory());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private class DeleteDirectory extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			// System.out.println("Deleting " + file.getFileName());
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path directory, IOException exception) throws IOException {
			if (exception == null) {
				// System.out.println("Deleting " + directory.getFileName());
				Files.delete(directory);
				return FileVisitResult.CONTINUE;
			} else {
				throw exception;
			}
		}
	}

	public String createNewFolder1(String folderName) {
		if (folderName == null || folderName.isEmpty())
			return null;
		String dir = getFullPath() + folderName.trim();
		File file = new File(dir);
		if (file.exists()) {
			return dir;
		} else {
			return file.mkdir() ? dir : null;
		}
	}

	public String createFolder(String folderName) {
		if (folderName == null || folderName.isEmpty())
			return null;
		String dir = folderName.trim();
		File file = new File(dir);
		if (file.exists()) {
			return dir;
		} else {
			return file.mkdir() ? dir : null;
		}
	}

	public String getDir(int maxN, String orgName, boolean isActiveDir) {
		orgName = orgName.trim();
		if (maxN == 0)
			return orgName;

		String dir = orgName.trim();
		File file = new File(dir);
		int i = 0;
		while (i < maxN) {
			if (file.exists()) {
				if (maxN == 1)
					return dir;
				i++;
				dir = String.format("%s%02d", orgName, i);
			} else {
				if (isActiveDir && i > 0) {
					return String.format("%s%02d", orgName, i - 1);
				} else {
					if (file.mkdir()) {
						System.out.println("Directory is created! " + dir);
						i = 200;
					}
				}
			}
			file = new File(dir);
		}
		return dir;
	}

	public void compareVersion(String executorVersion) {
		if (version != null && !version.isEmpty() && executorVersion != null) {
			int greater = compareVersions(version, executorVersion);
			if (greater > 0) {
				String msg = "The input file was produced in a newer version for analysts (producer version=" + version + ").\n\n";
				msg += "The file will be loaded in the current version (" + executorVersion + ")"
						+ ", but update is recommended to receive the latest  program features. \n\n"
						+ "Please contact your Halliburton representive to receive the latest version.";

				JOptionPane.showMessageDialog(null, msg,
						"Newer Version of MTV Available", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public int compareVersions(String version1, String version2) {
		String[] levels1 = version1.split("\\.");
		String[] levels2 = version2.split("\\.");

		int length = Math.max(levels1.length, levels2.length);
		for (int i = 0; i < length; i++) {
			Integer v1 = i < levels1.length ? Integer.parseInt(levels1[i]) : 0;
			Integer v2 = i < levels2.length ? Integer.parseInt(levels2[i]) : 0;
			int compare = v1.compareTo(v2);
			if (compare != 0) {
				return compare;
			}
		}

		return 0;
	}

	public void read() {
		read(getProjectFileName());
	}

	public void write() {
		write(getProjectFileName());
	}

	public void read(String projectFileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(projectFileName));
			String line = null;
			int k = 0;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				if (k == 0) {
					String[] item = parseLine(line).split("[ ,]+");
					iId = Integer.parseInt(item[0].trim());
					if (item.length > 1)
						version = item[1].trim();
					else
						version = null;
				} else if (k == 1) {
					iUnit = Integer.parseInt(parseLine(line));
					break;
				}
				k++;
			}

			readProject(reader);
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

	public void write(String inputFileName) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFileName));
			setProjectName(inputFileName);
			writeLine(writer, "#All file name could be either relative or abstract file path ");
			writeLine(writer, "#Word unknown or null mean non-applicable ");

			writer.newLine();
			writeLine(writer, "#ID and Version: ");
			String line = String.format("%d", getIId());
			if (version != null && version.isEmpty())
				line += " " + version.trim();
			writeLine(writer, line);
			writeLine(writer, "#Unit (1-meter; 2-feet; 3-km): ");
			line = String.format("%d", getIUnit());
			writeLine(writer, line);
			writer.newLine();
			writeProject(writer);
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

	public String parseFileName(String name) {
		String a = parseLine(name);
		if (a == null)
			return null;

		if (a.startsWith(".")) {
			String fileName = a.substring(2).trim();
			return fullPath + fileName;
		} else
			return a;
	}

	public String parseLine(String line) {
		if (line == null)
			return null;
		String a = line.trim();
		if (a.isEmpty())
			return null;
		else if (a.equalsIgnoreCase("null"))
			return null;
		return a;
	}

	public void writeFileName(BufferedWriter writer, String name) throws IOException {
		String a = parseLine(name);
		if (a == null)
			writer.write("null");
		else {
			String path = FilenameUtils.getFullPath(name);
			if (path.equals(fullPath)) {
				String fileName = FilenameUtils.getName(name);
				writer.write("." + File.separator + fileName);
			} else if (path.startsWith(fullPath)) {
				int len = fullPath.length();
				String fileName = name.substring(len);
				writer.write("." + File.separator + fileName);
			} else {
				writer.write(name);
			}
		}
		writer.newLine();
	}

	public void writeLine(BufferedWriter writer, String line) throws IOException {
		String a = parseLine(line);
		if (a == null)
			writer.write("null");
		else
			writer.write(a);
		writer.newLine();
	}

	protected abstract void readProject(BufferedReader reader) throws IOException;

	protected abstract void writeProject(BufferedWriter writer) throws IOException;

	public String toString(int id) {
		String separator = System.getProperty("line.separator");
		StringBuilder lines = new StringBuilder("iId=" + iId + " unit=" + iUnit + " version=" + version + " extension=" + fileExtension);
		lines.append(separator);
		return lines.toString();
	}
}

