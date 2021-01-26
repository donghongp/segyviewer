package com.ghc.app.stv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;

import com.ghc.app.seis.HeaderDef;
import com.ghc.app.stv.StvFrameBase.FileTreeNode;
import com.ghc.app.stv.dialog.TextDialog;
import com.ghc.app.stv.dialog.TraceHeaderDialog;
import com.ghc.app.stv.io.SegyReader;
import com.ghc.app.utils.GMenuBar;
import com.ghc.app.utils.RecentFileMenu;

@SuppressWarnings("serial")
public class StvMenuBar extends GMenuBar {
	private final static int MAX_RECENT_FILES = 20;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	StvFrame frame = null;
	private RecentFileMenu 	menuRecentFiles = null;

	private JMenuItem 	openFile 			= null;

	JToolBar toolBar = new JToolBar();

	public StvMenuBar(StvFrame frame) {
		this.frame 				= frame;
		JMenu menuFile = new JMenu("   File");
		JMenu menuView = new JMenu("   View");

		setMenuFile(menuFile);
		setMenuView(menuView);

		add(menuFile);
		add(menuView);

		// add(genEmptySpacer(200));
		add(Box.createHorizontalGlue());
		StvToolBar stvToolBar = new StvToolBar(frame);
		stvToolBar.setEnabled(false);
		add(stvToolBar);
	}

	public void setFrame(StvFrame frame) {
		this.frame = frame;
	}
	public JMenuItem getJMenuItemOpenFile() 		{ return openFile; }

	public void removeRecentFile( String filename ) { menuRecentFiles.removeFile( filename ); }
	public void addRecentFile( String filename ) 	{ menuRecentFiles.addFile( filename ); }
	public ArrayList<String> getRecentFileList() 	{ return menuRecentFiles.getFileList(); }

	public void setMenuFile(JMenu jMenu) {
		JMenuItem jMenuItem = null;
		jMenuItem = new JMenuItem("Open Folder");
		jMenuItem.setToolTipText("Open a data folder");
		jMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK) );
		jMenu.add(jMenuItem);
		jMenuItem.addActionListener(e -> {
			String base = frame.getRecentFileName();
			FileNameExtensionFilter [] exts = new FileNameExtensionFilter [] {
					new FileNameExtensionFilter("All files", "*")
			};
			String folder = frame.openFileAndDirectoryUsingJFileChooser(exts, base);
			if (folder != null) {
				FileTreeNode root = new FileTreeNode(new File[] { new File(folder) });
				DefaultTreeModel model = new DefaultTreeModel(root);
				frame.getTree().setModel(model);
				for (int i = 0; i < frame.getTree().getRowCount(); i++) {
					frame.getTree().expandRow(i);
				}
				for (int i = 1; i < getMenuCount(); i++) {
					JComponent component = (JComponent) getComponent(i);
					component.setEnabled(true);
				}
				addRecentFile(folder);
				frame.savePreferences();
			}
		});
		openFile = jMenuItem;
		jMenu.addSeparator();

		menuRecentFiles = new RecentFileMenu("Recent", MAX_RECENT_FILES);
		menuRecentFiles.addFileMenuListener(frame);
		// jMenu.add(menuRecentFiles);
		// jMenu.addSeparator();

		jMenuItem  	= new JMenuItem("Exit");
		jMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK) );
		jMenu.add(jMenuItem);
		jMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				frame.exit(false);
			}
		});
	}

	public void setMenuView(JMenu jMenu) {
		JMenuItem jMenuItem = null;

		jMenuItem = new JMenuItem("File EBCDIC header");
		jMenu.add(jMenuItem);
		jMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String aTitle = new String("File EBCDIC header");
				SegyReader reader = (SegyReader) frame.getTraceIO().getReader();
				String content = reader.readEBCDIC();
				TextDialog dialog = new TextDialog(frame, aTitle, false, content);
				dialog.showDialog();
			}
		});
		jMenuItem = new JMenuItem("File binary header");
		jMenu.add(jMenuItem);
		jMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String aTitle = new String("File binary header");
				SegyReader reader = (SegyReader) frame.getTraceIO().getReader();
				HeaderDef[] headers = reader.genFileHeaderDef();
				TraceHeaderDialog dialog = new TraceHeaderDialog(frame, aTitle, false, headers);
				dialog.setHeaderValue(reader.getFileBinaryHeaderValue());
				dialog.showDialog();
			}
		});

		jMenu.addSeparator();

		jMenuItem = new JMenuItem("Monitor trace header");
		jMenu.add(jMenuItem);
		jMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				frame.showTraceHeaderDialog();
			}
		});
		jMenuItem = new JMenuItem("Monitor trace sample");
		jMenu.add(jMenuItem);
		jMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.showTraceSampleDialog();
			}
		});
	}
}
