package com.ghc.app.resources;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This class keeps a cache of all Images, Icons, and ImageIcons loaded by the
 * application. Once an image has been loaded, it is put in the cache, and never removed.
 * If you're only using an image once in your object, you should not need to keep a local
 * pointer to it. It is safe to use the GhcImageLoader.get???() call as your reference.
 *
 */

public class GhcImageLoader {

	private static final long serialVersionUID = -1;
	public final static String BLUE_SKY = "icons/blueSky.jpg";
	public final static String BEACHBALL_RED = "icons/beachBall_red.png";
	public final static String STV_APP = "icons/stv_app.png";
	public final static String BULLET_GREEN = "icons/bullet_green.png";

	/**
	 * This constant is the package path to the image loader using the java ClassLoader
	 * resource notion, i.e. "/" instead of "." between package names.
	 *
	 * @see #getImageURL
	 */
	public static final String IMAGE_RESOURCE_PATH_PREFIX = GhcImageLoader.class.getPackage().getName().replace('.', '/') + "/";

	/**
	 * This class exists for static access. We don't want people making their own copy, so
	 * the constructor is private. This method does nothing.
	 **/
	private GhcImageLoader() {
	}

	/**
	 * @param fileName A path to the desired image.
	 * @return An Image of the desired image. Will return null if the image is not found.
	 **/
	public static Image getImage(String fileName) {
		Image rtn = null;
		ImageIcon icon = null;

		if ((fileName != null) && ((icon = GhcImageLoader.getImageIcon(fileName)) != null))
			rtn = icon.getImage();

		return rtn;
	}

	/**
	 * Images files for the new 'src/main/resources/images'.
	 * 
	 * @param clazz
	 * @param fileName
	 * @return JavaFx Image file structure.
	 * @throws IOException
	 */
	public static javafx.scene.image.Image getJavaFxImage(String fileName) throws IOException {
		fileName = "/" + fileName;
		return new javafx.scene.image.Image(GhcImageLoader.class.getResourceAsStream(fileName));
	}

	/**
	 * @param fileName A path to the desired icon.
	 * @return An Icon of the desired icon. Will return null if the image is not found.
	 **/
	public static Icon getIcon(String fileName) {
		return GhcImageLoader.getImageIcon(fileName);
	}

	/**
	 * @param fileName A path to the desired image.
	 * @return An ImageIcon of the desired image. Will return null if the image is not
	 *         found.
	 **/
	public static ImageIcon getImageIcon(String fileName) {
		ImageIcon icon = null;
		String realPath = IMAGE_RESOURCE_PATH_PREFIX + fileName;

		// try to find a copy by name. return null if we fail.
		try {
			java.io.BufferedInputStream is = new java.io.BufferedInputStream(
					GhcImageLoader.class.getClassLoader().getResourceAsStream(realPath));

			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(buffer));
			is.close();
		} catch (IOException ioe) {
		}

		return icon;
	}

	/**
	 * This method returns an instace of URL representing the specified file as a resource
	 * in the classpath. This URL can be used in Swing components that support HTML
	 * rendering.
	 *
	 * @param imageFileName the name of the image file
	 *
	 * @return a URL representing the requested image
	 *
	 */
	public static URL getImageURL(final String imageFileName) {
		String resourcePath = IMAGE_RESOURCE_PATH_PREFIX + (imageFileName == null ? " " : imageFileName);
		final URL systemResource = GhcImageLoader.class.getClassLoader().getResource(resourcePath.trim());
		return systemResource;
	}
}
