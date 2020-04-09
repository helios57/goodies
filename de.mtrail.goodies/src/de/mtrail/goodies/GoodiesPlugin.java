package de.mtrail.goodies;

import static org.eclipse.jface.resource.ResourceLocator.imageDescriptorFromBundle;

import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class GoodiesPlugin extends AbstractUIPlugin {

	private static GoodiesPlugin plugin;

	public static final String PLUGIN_ID = "de.mtrail.goodies"; //$NON-NLS-1$

	public static GoodiesPlugin getDefault() {
		return plugin;
	}

	public GoodiesPlugin() {
		plugin = this;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Logs a given Throwable as Error by using {@link Plugin#getLog()}
	 *
	 * @param throwable the throwable to logged as error
	 */
	public void logError(final Throwable throwable) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, throwable.toString(), throwable));
	}

	/**
	 * Creates an {@link ImageDescriptor} from a string relative to this bundle
	 * location.
	 *
	 * @param path relative path to the image.
	 * @return an instance of {@link ImageDescriptor}.
	 */
	public ImageDescriptor getImageDescriptor(final String path) {
		final ImageRegistry reg = getImageRegistry();
		final ImageDescriptor descr = reg.getDescriptor(path);
		if (descr == null && reg.get(path) == null) {
			final Optional<ImageDescriptor> optional = imageDescriptorFromBundle(getBundle().getSymbolicName(), path);
			if (optional.isPresent()) {
				reg.put(path, optional.get());
			}
		}
		return descr;
	}

	/**
	 * Returns in Image instance by implicitly adding the desired
	 * {@link ImageDescriptor} to the internal {@link ImageRegistry}.
	 *
	 * @param path Plug-in relative path to the image file.
	 * @return a new or cached instance of the image.
	 */
	public Image getImage(final String path) {
		getImageDescriptor(path);
		return getImageRegistry().get(path);
	}
}