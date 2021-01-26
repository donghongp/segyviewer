package com.ghc.app.stv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import com.ghc.app.project.ProjectPreference;
import com.ghc.app.project.ProjectStv;
import com.ghc.app.stv.display.ViewPanel;
import com.ghc.app.utils.FileMenuListener;
import com.ghc.app.utils.GhcUtil;


@Getter
@Setter
public class StvFrame extends StvFrameBase implements FileMenuListener, TreeSelectionListener {
	private static final long serialVersionUID 		= 1L;

	public StvFrame(int exitCode, String title) {
		super(title);
		this.exitCode 	= exitCode;

		projectPreference = new ProjectPreference("stv");
		projectPreference.readPreferences();

		project = new ProjectStv(System.getProperty("user.dir") + File.separator + "untitled.stv");

		traceContext = new TraceContext();
		viewPanel = new ViewPanel(this);
		stvFrameActions = new StvFrameActions(this);

		menuBar = new StvMenuBar(this);
		ArrayList<String> list = projectPreference.getRecentFileList();
		if( list != null ) {
			for (int i = list.size() - 1; i >= 0; i--)
				menuBar.addRecentFile(list.get(i));
		}
		for (int i = 1; i < menuBar.getMenuCount(); i++) {
			JComponent component = (JComponent) menuBar.getComponent(i);
			component.setEnabled(true);
		}
		setJMenuBar(menuBar);

		stvPanel = new StvPanel(this, viewPanel);

		tree = genTree();

		JScrollPane treeJScrollPane = new JScrollPane(tree);
		treeJScrollPane.setMinimumSize(new Dimension(250, 500));
		treeJScrollPane.setPreferredSize(new Dimension(250, 500));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeJScrollPane);
		splitPane.setRightComponent(stvPanel);
		splitPane.setDividerLocation(0.2);
		splitPane.setPreferredSize(new Dimension(1200, 800));

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		addWindowListener( new WindowAdapter () {
			public void windowClosing( WindowEvent e ) {
				exit(true);
			}
		});
	}
	public void savePreferences() {
		ArrayList<String> list = menuBar.getRecentFileList();
		if(list.size()>0) {
			projectPreference.setRecentFileList(list);
			projectPreference.writePreferences();
		}
	}

	public String getRecentFileName() {
		ProjectStv prevProject = null;
		ArrayList<String> files = getProjectPreference().getRecentFileList();
		if (files != null && files.size() > 0) {
			return files.get(0);
		}
		return System.getProperty("user.dir");
	}

	public JTree genTree() {
		String fileName = getRecentFileName();
		File[] roots = new File[] { new File(fileName) };
		FileTreeNode rootTreeNode = new FileTreeNode(roots);
		JTree tree = new JTree(rootTreeNode);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.setRootVisible(false);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isMetaDown()) {
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					// JPopupMenu treePopupMenu = new JPopupMenu();
					// setTreePopupMenu(treePopupMenu, path);
					// if(_iViewer==2||_iViewer==3) treePopupMenu.show(e.getComponent(),
					// e.getX(), e.getY());
				}
			}
		});
		return tree;
	}

	public void createProject(String projectFileName, int iId, int iUnit, int fileFormat, String fileOrDirectoryName) {
		project = new ProjectStv(projectFileName, iId, iUnit, fileFormat, fileOrDirectoryName);

		if(projectFileName!=null) {
			for (int i = 1; i < menuBar.getMenuCount(); i++) {
				JComponent component = (JComponent) menuBar.getComponent(i);
				component.setEnabled(true);
			}
			menuBar.getJMenuItemOpenFile().setEnabled(true);

			if (!projectFileName.contains("untitled")) {
				menuBar.addRecentFile(project.getProjectFileName());
			}
			setTitle(projectFileName);
		}
	}

	public void loadProject(String projectName, int iId, int iUnit,
			int fileFormat, String fileOrDirectoryName) {

		String fullPath = FilenameUtils.getFullPath(fileOrDirectoryName);
		if (projectName == null) {
			projectName = fullPath + "untitled." + GhcUtil.getProjectFileExtension(iId);
		}
		createProject(projectName, iId, iUnit, fileFormat, fileOrDirectoryName);

		loadFile(getProject().getFileFormat(), getProject().getFileOrDirectoryName());
	}

	public void loadFile(int fileFormat, String fileName) {
		traceIO = new TraceIO(fileFormat, fileName, traceContext.getTraceIOConfig());
		traceIO.genSeismicReader();
		traceIO.genTraceHeader();
		// traceIO.printTraceHeader();
		traceContext.setTraceBuffer(traceIO.readTraceBuffer());
		traceContext.setSampleInt(traceIO.getReader().sampleInt());
		traceContext.setHeaders(traceIO.getTraceHeaders());

		stvPanel.update(traceContext);
	}

	public boolean openProject(String projectFileName)	{
		ProjectStv project = new ProjectStv(projectFileName);
		loadProject(projectFileName, project.getIId(), project.getIUnit(),
				project.getFileFormat(), project.getFileOrDirectoryName());

		menuBar.addRecentFile(projectFileName);

		savePreferences();
		setTitle(projectFileName);

		return true;
	}

	public boolean saveProject(String selectedFileName)	{
		project.setProjectName(selectedFileName);
		project.write(selectedFileName);
		menuBar.addRecentFile(selectedFileName);
		savePreferences();
		setTitle(selectedFileName);
		fadeOut();
		return true;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		FileTreeNode node = (FileTreeNode) e.getPath().getLastPathComponent();
		if (node.getFile().isFile()) {
			String selectedFileName = null;
			try {
				selectedFileName = node.getFile().getCanonicalPath();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			if (GhcUtil.isExtension(selectedFileName, "segy", "sgy")) {
				loadFile(1, selectedFileName);
			} else {
			}
		}
	}


}

