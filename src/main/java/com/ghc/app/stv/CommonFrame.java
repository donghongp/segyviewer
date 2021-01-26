
package com.ghc.app.stv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

import com.ghc.app.project.Project;
import com.ghc.app.project.ProjectPreference;
import com.ghc.app.utils.FileMenuEvent;


public class CommonFrame extends JFrame {
	//static final Logger logger = Logger.getLogger(ModelFrame.class);

	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_WIDTH = 720;
	public static final int DEFAULT_HEIGHT = 550;
	public static final Font DEFAULT_FONT = new Font("Arial",Font.PLAIN,24);
	public static final Color DEFAULT_BACKGROUND = Color.WHITE;

	public boolean fontSizeForPrint; // true if auto font size for print
	public boolean fontSizeForSlide; // true if auto font size for slide
	public double fracWidthSlide; // fraction of slide height available
	public double fracHeightSlide; // fraction of slide width available
	public double fontSizePrint; // font size for print
	public double plotWidthPrint; // plot width for print

	protected int 				exitCode	= 0;
	protected int 				processID	= 0;
	protected ProjectPreference projectPreference = null;

	public CommonFrame() { this(null); }
	public CommonFrame(String title) {
		super();
		if(title!=null) setTitle(title);
		setBackground(Color.white);
	}

	public Project getProject() {
		return null;
	}
	public boolean openProject(String projectFileName) { return true; }
	public boolean saveProject(String selectedFileName) { return true; }
	public void savePreferences() { }


	public ProjectPreference getProjectPreference() {
		return projectPreference;
	}
	public int getProcessID() 						{ return processID; }

	public String getCwd() 		{
		String baseName = getProject().getBaseName();
		if(baseName.contains("untitled")) {
			return System.getProperty("user.dir")+File.separator;
		} else {
			return getProject().getFullPath();
		}
	}

	public void setProcessID(int processID) 		{ this.processID = processID; }

	public String getIconName(String iconName) 		{ return "com/vecon/resource/icon/" + iconName + ".png"; }

	public ArrayList<String> getRecentFileList() {
		return projectPreference.getRecentFileList();
	}

	public void fileSelected( FileMenuEvent e ) {
		File file = e.file();
		if(!file.exists()) {
			JOptionPane.showMessageDialog(this, "project file does not exist", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		}
		boolean success = openProject(file.getAbsolutePath() );
	}
	public class doYouWantSave extends WindowAdapter {
		public void windowClosing( WindowEvent e ) {
			Project project = getProject();
			if(project!=null) {
				String baseName = project.getBaseName();
				if(baseName!=null&&!baseName.isEmpty()) {
					if(baseName.contains("untitled")) {
						Object[] options = {"Save As","No","Cancel"};
						int userChoice = JOptionPane.showOptionDialog(
								CommonFrame.this, "Your work has not been saved. Do you want to save the project?",
								"Exit Dialog", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options, options[0] );

						if( userChoice == 0 ) 		saveProjectAs();
						else if( userChoice == 1 ) 	exit(exitCode==1);
						else 						return;
					}
				}
			}
			exit(exitCode==1);
		}
	}

	public void exit(boolean global) {
		savePreferences();
		setVisible(false);
		dispose();
		if(exitCode==1) System.exit(0);
	}

	public String openFileUsingJFileChooser(FileNameExtensionFilter[] exts, String base) {
		return usingJFileChooser(0, true, exts, new File(base));
	}
	public String openDirectoryUsingJFileChooser(String base)	{
		return usingJFileChooser(1, true, null, new File(base));
	}
	public String openFileAndDirectoryUsingJFileChooser(FileNameExtensionFilter [] exts, String base)	{
		return usingJFileChooser(2, true, exts, new File(base));
	}
	public String saveFileUsingJFileChooser(FileNameExtensionFilter [] exts, String base)	{
		return usingJFileChooser(0, false, exts, new File(base));
	}

	private String usingJFileChooser(int isDir, boolean isOpenAction, FileNameExtensionFilter exts[], File baseFile) {
		JFileChooser chooser = new JFileChooser(baseFile);
		String titleName = null;
		if (isDir == 0) {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setSelectedFile(baseFile);
			titleName = " Files";
			if (exts != null) {
				for (int i = 0; i < exts.length; i++) {
					chooser.addChoosableFileFilter(exts[i]);
				}
			}
		} else if (isDir == 1) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			titleName = " Directories";
		} else {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			titleName = " Files And Directories";
			if (exts != null) {
				for (int i = 0; i < exts.length; i++) {
					chooser.addChoosableFileFilter(exts[i]);
				}
			}
		}

		int returnVal = 0;
		if (isOpenAction) {
			chooser.setDialogTitle("Open" + titleName);
			returnVal = chooser.showOpenDialog(this);
		} else {
			chooser.setDialogTitle("Save" + titleName);
			returnVal = chooser.showSaveDialog(this);
		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String fullFileName = file.getAbsolutePath();
			if (isDir == 0) {
				if (isOpenAction) {
					if (!file.exists()) {
						return null;
					}
					return fullFileName;
				} else {
					if (file.exists()) {
						int response = JOptionPane.showConfirmDialog(null,
								"Overwrite existing file? ", "Confirm Overwrite",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (response == JOptionPane.CANCEL_OPTION) {
							return null;
						}
					}
					return fullFileName;
				}
			} else if (isDir == 1) {
				if (isOpenAction) {
					return fullFileName;
				}
			} else {
				if (isOpenAction) {
					return fullFileName;
				} else {
					if (file.exists()) {
						int response = JOptionPane.showConfirmDialog(null,
								"Overwrite existing file? ", "Confirm Overwrite",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (response == JOptionPane.CANCEL_OPTION) {
							return null;
						}
					}
					return fullFileName;
				}
			}
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			return null;
		} else {
		}
		return null;
	}

	private String usingJFileChooser1(int isDir, boolean isOpenAction, FileNameExtensionFilter exts[], String base) {
		File baseFile = new File(base);
		JFileChooser chooser = new JFileChooser(baseFile.isDirectory()?base:FilenameUtils.getPath(base));
		int index = FilenameUtils.indexOfExtension(base);
		if(index>0) chooser.setSelectedFile(baseFile);
		String titleName = null;
		if(isDir==0) {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			titleName = " Files";
			if (exts != null) {
				for (int i = 0; i < exts.length; i++) {
					chooser.addChoosableFileFilter(exts[i]);
				}
			}
		} else if(isDir==1){
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			titleName = " Directories";
		} else {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			titleName = " Files And Directories";
		}

		int returnVal = 0;
		if (isOpenAction) {
			chooser.setDialogTitle("Open"+titleName);
			returnVal = chooser.showOpenDialog( null );
		} else {
			chooser.setDialogTitle("Save"+titleName);
			returnVal = chooser.showSaveDialog( null );
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String fullFileName = null;
			if (isDir == 0) {

				if (isOpenAction) {
					if (!file.exists())
						return null;
					// return checkFileName();
				} else {
					if (file.exists()) {
						int response = JOptionPane.showConfirmDialog(null,
								"Overwrite existing file? ", "Confirm Overwrite",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (response == JOptionPane.CANCEL_OPTION) {
							return null;
						}
					}
					// return checkFileName();
				}
			} else if (isDir == 1) {
				if (file!= null) return file.getAbsolutePath();
				else return null;
//				if(file.isFile()) {
//					if(isDir==1) return null;
//					else return file.getAbsolutePath();
//				}
//				if (file!= null) {
//					if (file.exists()) {
//						fullFileName = file.getAbsolutePath();
//					} else {
//						File parentFile = new File(file.getParent());
//						fullFileName = parentFile.getAbsolutePath();
//					}
//				}
//				return fullFileName;
			}
			fullFileName = file.getAbsolutePath();
			if ((!isOpenAction) && file.exists()) {
				int response = JOptionPane.showConfirmDialog (null,
						"Overwrite existing file? ","Confirm Overwrite",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CANCEL_OPTION) { return null; }
			}
			if(exts==null) return fullFileName;

			String selectedExt = FilenameUtils.getExtension(fullFileName);

			if(selectedExt==null||selectedExt.isEmpty()) {
				if (!isOpenAction)
					fullFileName = fullFileName + "." + exts[0].getExtensions()[0];
			} else {
				boolean validExt = false;
				for(int i=0; i<exts.length; i++) {
					String [] possibleExt = exts[i].getExtensions();
					//System.out.println("selectedExt="+selectedExt+" "+Arrays.toString(possibleExt));
					for(int j=0; j<possibleExt.length; j++) {
						if(selectedExt.equalsIgnoreCase(possibleExt[j])) {
							validExt = true;
							j = possibleExt.length+1;
							i = exts.length+1;
						}
					}
				}
				if(!validExt) {
					String message = "File name extension is not valid: "+fullFileName;
					String title = "Alert";
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			return fullFileName;
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			return null;
		} else { }
		return null;
	}

	public void openProject()	{
		String recentFileName = projectPreference.getRecentFile();
		String ext = getProject().getFileExtension();
		FileNameExtensionFilter exts[] = new FileNameExtensionFilter [] {
				new FileNameExtensionFilter("(*."+ext+")", ext) };
		String fileName = openFileUsingJFileChooser(exts, recentFileName);
		if(fileName==null) return;
		else { openProject(fileName); }
	}
	public void openProjectWithExtension(FileNameExtensionFilter [] exts)	{
		String recentFileName = projectPreference.getRecentFile();
		String fileName = openFileUsingJFileChooser(exts, recentFileName);
		if(fileName==null) return;
		else { openProject(fileName); }
	}

	public boolean saveProject()	{
		String baseName = getProject().getBaseName();
		if(baseName.contains("untitled")) {
			return saveProjectAs();
		} else {
			return saveProject(getProject().getProjectFileName());
		}
	}
	public boolean saveProjectAs()	{
		String ext = getProject().getFileExtension();
		FileNameExtensionFilter exts[] = new FileNameExtensionFilter [] {
				new FileNameExtensionFilter("(*."+ext+")", ext) };
		String base = getProject().getFullName()+"."+exts[0].getExtensions()[0];
		String fileName = saveFileUsingJFileChooser(exts, base);
		if(fileName==null) return false;
		else {
			String selectedExt = FilenameUtils.getExtension(fileName);
			if(selectedExt==null||selectedExt.isEmpty()) fileName = fileName+"."+ext;
			int k = 0;
			for(int i=0; i<exts.length; i++) {
				String [] possibleExt = exts[i].getExtensions();
				for(int j=0; j<possibleExt.length; j++) {
					if(selectedExt.equals(possibleExt[j])) {
						k = i;
						j = possibleExt.length+1;
						i = exts.length+1;
					}
				}
			}
			if(k==0) 	saveProject(fileName);
			else 		saveProject(fileName+"."+ext);
		}
		return true;
	}

	public void fadeOut() {
		int delay = 500;   // delay for 1 sec.
		int period = 200;  // repeat every 3 sec.
		JDialog dialog = new JDialog(this);
		dialog.setUndecorated(true);
		JLabel label = new JLabel("saving...   ");

		dialog.setLocationRelativeTo(this);
		//dialog.setTitle(label.getText());
		//dialog.add(label);
		dialog.add(new JLabel("<html><font color='red' size='+3' >saving...   </font></html>"));
		dialog.pack();
		dialog.setVisible(true);
		fadeOut(dialog, delay, period);
	}
	public void fadeOut(final JDialog dialog, int delay, int period) {
		final Timer timer = new Timer(period, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			private float opacity = 1;
			@Override
			public void actionPerformed(ActionEvent e) {
				opacity -= 0.15f;
				dialog.setOpacity(Math.max(opacity, 0));
				if (opacity <= 0) {
					timer.stop();
					dialog.dispose();
				}
			}
		});

		dialog.setOpacity(1);
		timer.setInitialDelay(delay);
		timer.start();
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if(visible) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration [] gc = gd.getConfigurations();
			Rectangle gcBounds = gc[0].getBounds();

			//Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			//int w = dimension.width / 2;
			//int h = 3*dimension.height / 4;
			//System.out.println("w=" + w + " h=" + h + " 1=" + dimension.width + " 2=" + dimension.height);

			int w = (int) (9 * gcBounds.getWidth() / 10.0);
			int h = (int) (9 * gcBounds.getHeight() / 10.0);
			//int h = (int)(w/2);
			//int h = (int)(gcBounds.getHeight()-40.0);
			//setSize((int)(gcBounds.getWidth()-10), (int)(gcBounds.getHeight()/2));
			setSize(w, h);
			//setSize(1000, 800);

			setLocationRelativeTo(null);

			//			int cx = (int)(gcBounds.getWidth() / 2.0 -w/2);
			//			int cy = (int)(gcBounds.getHeight() / 2.0 -h/2);
			//			//setLocation(cx, cy);
			//			setLocation(50, 50);
			//			int minW = (int)(0.9*w / 3.0);
			//			int minW1 = (int)(1.9*w / 3.0);
			//			int minH = (int)(0.9*h / 2.0);
			//_mvp.setMinimumSize(new Dimension(minW1, minH));
			//_mvs.setMinimumSize(new Dimension(minW1, minH));
			//_world.setMinimumSize(new Dimension(minW, h));
		}
		//repaint();
	}

	public void to1to1Ratio() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration [] gc = gd.getConfigurations();
		Rectangle gcBounds = gc[0].getBounds();

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int w1 = dimension.width / 2;
		int h1 = 3*dimension.height / 4;
		System.out.println("w1=" + w1 + " h1=" + h1 + " 1=" + dimension.width + " 2=" + dimension.height);

		int w = (int)(gcBounds.getWidth());
		int h = (int)(gcBounds.getHeight());
		//int h = (int)(w/2);
		setSize(w, h);
		//setSize(800, 600);
		//System.out.println("w=" + w + " h=" + h );

		setLocationRelativeTo(null);
	}

	//http://viralpatel.net/blogs/20-useful-java-code-snippets-for-java-developers/
	public void captureScreen(String fileName) throws Exception {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		ImageIO.write(image, "png", new File(fileName));
	}
}
