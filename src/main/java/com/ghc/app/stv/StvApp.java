package com.ghc.app.stv;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.ghc.app.project.ProjectPreference;
import com.ghc.app.resources.GhcImageLoader;
import com.ghc.app.utils.GhcLookAndFeel;

public class StvApp {
	public static void main(String[] args) {
		boolean createdNewDirectory = false;
		try {
			createdNewDirectory = ProjectPreference.setRootDirectory(".ghc");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString() + " - STV will not run.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			GhcLookAndFeel.setDefaultLookAndFeel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String launcherPath = System.getProperty("launcher.dir");
		//System.out.println("launcher.dir = " + launcherPath);

		StvFrame frame = new StvFrame(1, "STV ");
		frame.setProcessID(1);
		frame.setVisible(true);
		frame.setIconImage(GhcImageLoader.getImage(GhcImageLoader.STV_APP));
	}
}

