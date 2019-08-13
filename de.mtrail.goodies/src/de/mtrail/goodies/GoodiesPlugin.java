package de.mtrail.goodies;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle ue08113
 */
public class GoodiesPlugin extends AbstractUIPlugin {

  // The shared instance of the plug-in
  private static GoodiesPlugin plugin;

  /** The plug-in ID */
  public static final String PLUGIN_ID = "de.mtrail.goodies"; //$NON-NLS-1$

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static GoodiesPlugin getDefault() {
    return plugin;
  }

  /**
   * The constructor
   */
  public GoodiesPlugin() {
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
   */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Logged das gegebene Throwable als Fehler.
   * 
   * @param throwable
   *          Throwable zum Loggen
   */
  public void logError(Throwable throwable) {
    getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, throwable.toString(), throwable));
  }

  /**
   * Liefert einen {@link ImageDescriptor} zu dem gegebenen Pfad.
   * 
   * @param path
   *          Pfad innerhalb des Plugins
   * @return zugehoeriger ImageDescriptor
   */
  public ImageDescriptor getImageDescriptor(String path) {
    ImageRegistry reg = getImageRegistry();
    ImageDescriptor descr = reg.getDescriptor(path);
    if (descr == null && reg.get(path) == null) {
      reg.put(path, descr = imageDescriptorFromPlugin(getBundle().getSymbolicName(), path));
    }
    return descr;
  }

  /**
   * Liefert das Bild mit dem gegebenen Pfad.
   * 
   * @param path
   *          Pfad innerhalb des Plugins
   * @return zugehoeriges Bild
   */
  public Image getImage(String path) {
    getImageDescriptor(path);
    return getImageRegistry().get(path);
  }

}