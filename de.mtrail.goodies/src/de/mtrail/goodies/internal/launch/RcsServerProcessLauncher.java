/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.pde.launching.EquinoxLaunchConfiguration;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.ui.PlatformUI;

import de.mtrail.goodies.GoodiesPlugin;

/**
 * Launcher for RCS server processes
 */
public class RcsServerProcessLauncher extends LaunchConfigurationDelegate {

	private static final String OSGI_ID = "org.eclipse.osgi"; //$NON-NLS-1$

	private static final String START_DEFAULT = "@default:default"; //$NON-NLS-1$
	private static final String START_OSGI = "@-1:true"; //$NON-NLS-1$
	private static final String START_LAUNCHER = "@default:true"; //$NON-NLS-1$

	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
			final IProgressMonitor monitor) throws CoreException {

		clearWorkspace(configuration, monitor);

		final ILaunchConfigurationWorkingCopy adjusted = configuration.getWorkingCopy();
		prepareForEquinoxLauncher(adjusted);

		final EquinoxLaunchConfiguration equinox = new EquinoxLaunchConfiguration();
		equinox.launch(adjusted, mode, launch, monitor);
	}

	@Override
	public boolean preLaunchCheck(final ILaunchConfiguration config, final String mode, final IProgressMonitor monitor)
			throws CoreException {
		PluginResolver resolver = resolvePlugins(config);
		final Set<String> unknownProducts = resolver.getUnknownProducts();
		if (!unknownProducts.isEmpty() && !confirmMissing("Missing Products",
				"The products %s are missing. Launch anyway?", unknownProducts)) {
			return false;
		}
		final Set<String> unknownFeatures = resolver.getUnknownFeatures();
		if (!unknownFeatures.isEmpty() && !confirmMissing("Missing Features",
				"The features %s are missing. Launch anyway?", unknownFeatures)) {
			return false;
		}
		final Set<String> unknownPlugins = resolver.getUnknownPlugins();
		if (!unknownPlugins.isEmpty() && !confirmMissing("Missing Plug-ins",
				"The plug-ins %s are missing. Launch anyway?", unknownPlugins)) {
			return false;
		}
		return super.preLaunchCheck(config, mode, monitor);
	}

	@SuppressWarnings("restriction")
	private void clearWorkspace(final ILaunchConfiguration config, final IProgressMonitor monitor) {
		try {
			final String workspace = substitute(config, RcsServerArgument.RCS_WORKING_DIR);
			org.eclipse.pde.internal.launching.launcher.LauncherUtils.clearWorkspace(config, workspace, monitor);
		} catch (CoreException e) {
			GoodiesPlugin.getDefault().logError(e);
		}
	}

	private void prepareForEquinoxLauncher(final ILaunchConfigurationWorkingCopy config) throws CoreException {
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				RcsServerArgument.RCS_WORKING_DIR.getValue(config));
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, createVMArgs(config));
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				RcsServerArgument.JVM_PROGRAM_ARGS.getValue(config));

		PluginResolver resolver = resolvePlugins(config);
		String launcherId = RcsServerArgument.RCS_SERVICE_LAUNCHER_ID.getValue(config);
		config.setAttribute(IPDELauncherConstants.TARGET_BUNDLES,
				createPluginList(resolver.getTargetPlugins(), launcherId));
		config.setAttribute(IPDELauncherConstants.WORKSPACE_BUNDLES,
				createPluginList(resolver.getWorkspacePlugins(), launcherId));
		config.setAttribute(IPDELauncherConstants.AUTOMATIC_ADD, false);
		config.setAttribute(IPDELauncherConstants.DEFAULT_AUTO_START, false);
		config.setAttribute(IPDELauncherConstants.CONFIG_CLEAR_AREA, true);
	}

	private PluginResolver resolvePlugins(final ILaunchConfiguration config) throws CoreException {
		final List<String> productIds = split(RcsServerArgument.RCS_INSTALLED_PRODUCTS.getValue(config));
		final List<String> featureIds = split(RcsServerArgument.RCS_INSTALLED_FEATURES.getValue(config));
		return new PluginResolver(productIds, featureIds);
	}

	private List<String> split(final String value) {
		List<String> result = new ArrayList<>();
		for (final String s : value.split(",")) {
			final String trimmed = s.trim();
			if (!trimmed.isEmpty()) {
				result.add(trimmed);
			}
		}
		return result;
	}

	private String createVMArgs(final ILaunchConfiguration config) throws CoreException {
		final StringBuilder sb = new StringBuilder();
		addSystemProperty(sb, "rcs.process.name", substitute(config, RcsServerArgument.RCS_PROCESS_NAME)); //$NON-NLS-1$
		addSystemProperty(sb, "rcsd.process.UUID", UUID.randomUUID().toString()); //$NON-NLS-1$
		addSystemProperty(sb, "rcs.process.startbundles", substitute(config, RcsServerArgument.RCS_START_BUNDLE_IDS)); //$NON-NLS-1$
		addSystemProperty(sb, "rcs.process.configlocations", substitute(config, RcsServerArgument.RCS_CONFIG_DIR)); //$NON-NLS-1$

		addSystemProperty(sb, "osgi.classloader.singleThreadLoads", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		addSystemProperty(sb, "osgi.classloader.lock", "classname"); //$NON-NLS-1$//$NON-NLS-2$
		addSystemProperty(sb, "eclipse.ignoreApp", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		addSystemProperty(sb, "osgi.noShutdown", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		addSystemProperty(sb, "osgi.framework.activeThreadType", "normal"); //$NON-NLS-1$ //$NON-NLS-2$

		addToken(sb, substitute(config, RcsServerArgument.RCS_CONFIG_OPTIONS));
		addToken(sb, substitute(config, RcsServerArgument.JVM_GC_OPTIONS));
		addToken(sb, substitute(config, RcsServerArgument.JVM_MEMORY_OPTIONS));
		addToken(sb, substitute(config, RcsServerArgument.PLATFORM_JVM_ARGS));

		return sb.toString();
	}

	private void addSystemProperty(final StringBuilder sb, final String key, final String value) {
		if (value != null) {
			addToken(sb, String.format("-D%s=%s", key, value)); //$NON-NLS-1$
		}
	}

	private void addToken(final StringBuilder sb, final String value) {
		if (value != null) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(value);
		}
	}

	private String createPluginList(final Set<String> pluginsIds, final String launcherId) {
		final StringBuilder sb = new StringBuilder();
		for (final String pluginId : pluginsIds) {
			addPlugin(sb, pluginId, getStart(pluginId, launcherId));
		}
		return sb.toString();
	}

	private String getStart(final String pluginId, final String launcherId) {
		if (OSGI_ID.equals(pluginId)) {
			return START_OSGI;
		}
		if (launcherId.equals(pluginId)) {
			return START_LAUNCHER;
		}
		return START_DEFAULT;
	}

	private void addPlugin(final StringBuilder sb, final String id, final String start) {
		if (sb.length() > 0) {
			sb.append(',');
		}
		sb.append(id).append(start);
	}

	private static String substitute(final ILaunchConfiguration config, final RcsServerArgument argument)
			throws CoreException {
		String value = argument.getValue(config);
		final IStringVariableManager mgr = VariablesPlugin.getDefault().getStringVariableManager();
		value = mgr.performStringSubstitution(value);
		value = RcsServerArgumentPresets.resolve(value);
		value = RcsServerArgument.substitute(value, config);
		return value;
	}

	private boolean confirmMissing(final String title, final String message, final Set<String> missing) {
		final boolean result[] = new boolean[1];
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				result[0] = MessageDialog.openConfirm(null, title, String.format(message, missing));
			}
		});
		return result[0];
	}

}
