package com.ghc.app.stv;

import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import com.ghc.app.project.ProjectStv;
import com.ghc.app.seis.Header;
import com.ghc.app.seis.HeaderDef;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.dialog.TraceHeaderDialog;
import com.ghc.app.stv.dialog.TraceSampleDialog;
import com.ghc.app.stv.display.ViewPanel;

@Getter
@Setter
public class StvFrameBase extends CommonFrame {
	private static final long serialVersionUID 		= 1L;
	public int 					exitCode 			= 1;

	private TraceHeaderDialog traceHeaderDialog;
	private TraceSampleDialog traceSampleDialog;

	protected TraceIO traceIO;
	protected TraceContext traceContext;
	protected ViewPanel viewPanel;
	protected ProjectStv project = null;
	@Getter(AccessLevel.NONE)
	protected StvMenuBar menuBar = null;
	protected StvPanel stvPanel = null;
	protected StvFrameActions stvFrameActions = null;

	public String selectedFileName = null;
	public JTree tree = null;
	public TreeNode nextSibling = null;
	public boolean currNodeOn = true;

	public StvFrameBase(String title) {
		super(title);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible) {
			// setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}

	public void showTraceHeaderDialog() {
		if (traceHeaderDialog == null) {
			HeaderDef[] traceHeaders = getTraceIO().getTraceHeaders();
			if (traceHeaders == null) {
				return;
			}
			traceHeaderDialog = new TraceHeaderDialog(this, "Trace header monitor", false, traceHeaders);
			traceHeaderDialog.showDialog();
		} else {
			traceHeaderDialog.setVisible(true);
		}
	}

	public void updateTraceHeaderDialogHeaderValues(int traceIndex) {
		if (traceHeaderDialog != null && traceHeaderDialog.isShowing()) {
			Header[] headerValues = getTraceBuffer().headerValues(traceIndex);
			traceHeaderDialog.updateValues(headerValues);
		}
	}

	public void showTraceSampleDialog() {
		if (traceSampleDialog == null) {
			traceSampleDialog = new TraceSampleDialog(this, "Trace sample monitor", false);
			traceSampleDialog.showDialog();
		} else {
			traceSampleDialog.setVisible(true);
		}
	}

	public void updateTraceSampleDialogSampleValues(int traceIndex) {
		if (traceSampleDialog != null && traceSampleDialog.isShowing()) {
			traceSampleDialog.updateValues(traceIndex);
		}
	}

	public double getSampleInt() {
		return traceContext == null ? 1.0 : traceContext.getSampleInt();
	}

	public ISeismicTraceBuffer getTraceBuffer() {
		return traceContext == null ? null : traceContext.getTraceBuffer();
	}

	public void createSnapShot(boolean frameIncluded, boolean sideLabelIncluded, String folderName) {
		Rectangle rect = null;
		if (frameIncluded) {
			rect = this.getBounds();
		} else if (sideLabelIncluded) {
			rect = stvPanel.getDisplayPanel().getVisibleRect();
		} else {
			rect = stvPanel.getDisplayPanel().getViewPort().getVisibleRect();
		}
		BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		if (frameIncluded) {
			this.paintAll(image.createGraphics());
		} else if (sideLabelIncluded) {
			stvPanel.getDisplayPanel().paintAll(image.createGraphics());
		} else {
			viewPanel.paintAll(image.createGraphics());
		}
		if (image == null) {
			return;
		}

		if (folderName == null) {
			ImageSelection imgSel = new ImageSelection(image);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
			return;
		}

		FileNameExtensionFilter exts[] = new FileNameExtensionFilter[] {
				new FileNameExtensionFilter("PNG (*.png)", "png")
		};

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String name = folderName + File.separator + "ss" + dateFormat.format(timestamp) + ".png";
		String fileName = saveFileUsingJFileChooser(exts, name);
		if (fileName == null)
			return;
		else {
			String selectedExt = FilenameUtils.getExtension(fileName);
			int k = -1;
			for (int i = 0; i < exts.length; i++) {
				String[] possibleExt = exts[i].getExtensions();
				for (int j = 0; j < possibleExt.length; j++) {
					if (selectedExt.equals(possibleExt[j])) {
						k = i;
						j = possibleExt.length + 1;
						i = exts.length + 1;
					}
				}
			}
			if (k >= 0) {
				File f = new File(fileName);
				try {
					ImageIO.write(image, "png", f);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	protected class ImageSelection implements Transferable {
		private Image image;

		public ImageSelection(Image image) {
			this.image = image;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!DataFlavor.imageFlavor.equals(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return image;
		}
	}

	protected class MtiTreeNode extends DefaultMutableTreeNode {
		private boolean _enable;
		private String _label;

		public MtiTreeNode(boolean enable, String label) {
			super(label);
			_enable = enable;
			_label = label;
		}

		public String getLabel() {
			return _label;
		}

		public boolean getEnable() {
			return _enable;
		}

		public void setLabel(String label) {
			_label = label;
		}

		public void setEnable(boolean enable) {
			_enable = enable;
		}
	}

	// http://www.javalobby.org/forums/thread.jspa?threadID=16052&tstart=0
	protected static class FileTreeNode implements TreeNode {
		private File file;
		private File[] children;
		private TreeNode parent;
		private boolean isFileSystemRoot = false;

		public FileTreeNode(File file, boolean isFileSystemRoot, TreeNode parent) {
			this.file = file;
			this.isFileSystemRoot = isFileSystemRoot;
			this.parent = parent;
			this.children = this.file.listFiles();
			if (this.children == null)
				this.children = new File[0];
		}

		public FileTreeNode(File[] children) {
			this.file = null;
			this.parent = null;
			this.children = children;
		}

		public Enumeration<?> children() {
			final int elementCount = this.children.length;
			return new Enumeration<File>() {
				int count = 0;

				public boolean hasMoreElements() {
					return this.count < elementCount;
				}

				public File nextElement() {
					if (this.count < elementCount) {
						return FileTreeNode.this.children[this.count++];
					}
					throw new NoSuchElementException("Vector Enumeration");
				}
			};

		}

		public boolean getAllowsChildren() {
			return true;
		}

		public int getChildCount() {
			return this.children.length;
		}

		public TreeNode getParent() {
			return this.parent;
		}

		public File getFile() {
			return file;
		}

		public TreeNode getChildAt(int childIndex) {
			return new FileTreeNode(this.children[childIndex],
					this.parent == null, this);
		}

		public int getIndex(TreeNode node) {
			FileTreeNode ftn = (FileTreeNode) node;
			for (int i = 0; i < this.children.length; i++) {
				if (ftn.file.equals(this.children[i]))
					return i;
			}
			return -1;
		}

		public boolean isLeaf() {
			return (this.getChildCount() == 0);
		}
	}

	protected static FileSystemView fsv = FileSystemView.getFileSystemView();

	protected static class FileTreeCellRenderer extends DefaultTreeCellRenderer {
		private Map<String, Icon> iconCache = new HashMap<String, Icon>();
		private Map<File, String> rootNameCache = new HashMap<File, String>();

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			FileTreeNode ftn = (FileTreeNode) value;
			File file = ftn.file;
			String filename = "";
			if (file != null) {
				if (ftn.isFileSystemRoot) {
					// long start = System.currentTimeMillis();
					filename = this.rootNameCache.get(file);
					if (filename == null) {
						filename = fsv.getSystemDisplayName(file);
						this.rootNameCache.put(file, filename);
					}
					// long end = System.currentTimeMillis();
					// System.out.println(filename + ":" + (end - start));
				} else {
					filename = file.getName();
				}
			}
			JLabel result = (JLabel) super.getTreeCellRendererComponent(tree,
					filename, sel, expanded, leaf, row, hasFocus);
			// if (file != null) {
			// Icon icon = this.iconCache.get(filename);
			// if (icon == null) {
			// // System.out.println("Getting icon of " + filename);
			// icon = fsv.getSystemIcon(file);
			// this.iconCache.put(filename, icon);
			// }
			// result.setIcon(icon);
			// }
			return result;
		}
	}
}
