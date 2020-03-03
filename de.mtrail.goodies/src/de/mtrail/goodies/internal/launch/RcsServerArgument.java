/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import de.mtrail.goodies.GoodiesPlugin;

/**
 * RCS server process arguments like in the launch scripts.
 */
public enum RcsServerArgument {

	RCS_PROCESS_NAME(""), //$NON-NLS-1$

	RCS_START_BUNDLE_IDS(""), //$NON-NLS-1$

	JVM_MEMORY_OPTIONS(""), //$NON-NLS-1$

	JVM_GC_OPTIONS(""), //$NON-NLS-1$

	JVM_PROGRAM_ARGS("-consoleLog"), //$NON-NLS-1$

	RCS_CONFIG_OPTIONS(""), //$NON-NLS-1$

	PLATFORM_JVM_ARGS(""), //$NON-NLS-1$

	RCS_INSTALLED_PRODUCTS("ch.sbb.rcsd.server.product"), //$NON-NLS-1$

	RCS_INSTALLED_FEATURES(""), //$NON-NLS-1$

	RCS_SERVICE_LAUNCHER_ID("ch.sbb.rcsd.server.service.launcher"), //$NON-NLS-1$

	RCS_WORKING_DIR("${workspace_loc}/../rcs-server-runtime"), //$NON-NLS-1$

	RCS_CONFIG_DIR("${resource_loc:/ch.sbb.rcsd.server.config/config/environment/${rcs.process.environment}/},"//$NON-NLS-1$
			+ "${resource_loc:/ch.sbb.rcsd.server.config/config/server/${rcs.process.cluster}-localdev/},"//$NON-NLS-1$
			+ "${resource_loc:/ch.sbb.rcsd.server.config/config/cluster/${rcs.process.cluster}/},"//$NON-NLS-1$
			+ "${resource_loc:/ch.sbb.rcsd.server.config/config/common/}"), //$NON-NLS-1$

	RTEX_CONFIG_DIR("${resource_loc:/be.infrabel.tms.rtex.config/config/environment/${rcs.process.environment}/},"
			+ "${resource_loc:/be.infrabel.tms.rtex.config/config/server/${rcs.process.cluster}-localdev/},"//$NON-NLS-1$
			+ "${resource_loc:/be.infrabel.tms.rtex.config/config/cluster/${rcs.process.cluster}/},"//$NON-NLS-1$
			+ "${resource_loc:/be.infrabel.tms.rtex.config/config/common/},"//$NON-NLS-1$
			+ "${resource_loc:/be.infrabel.tms.config/config/common/}"); //$NON-NLS-1$

	private final String defaultValue;

	private RcsServerArgument(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return "de.mtrail.goodies.rcsserverprocess." + name(); //$NON-NLS-1$
	}

	public String getDefault() {
		return this.defaultValue;
	}

	public String getPreference() {
		return GoodiesPlugin.getDefault().getPreferenceStore().getString(name());
	}

	public String getValue(final ILaunchConfiguration config) throws CoreException {
		return config.getAttribute(getId(), getPreference());
	}

	public static String substitute(final String value, final ILaunchConfiguration config) throws CoreException {
		String result = value;
		if (result != null) {
			for (RcsServerArgument argument : values()) {
				final String argName = "$" + argument.name();
				if (result.contains(argName)) {
					result = result.replace(argName, argument.getValue(config));
				}
			}
		}
		return result;
	}
}
