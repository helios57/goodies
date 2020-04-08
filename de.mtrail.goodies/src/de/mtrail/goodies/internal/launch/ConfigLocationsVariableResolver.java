/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.jface.preference.IPreferenceStore;

import de.mtrail.goodies.GoodiesPlugin;

public class ConfigLocationsVariableResolver extends AbstractPreferenceInitializer implements IDynamicVariableResolver {

	public static final String RCS_PROCESS_ENVIRONMENT = "rcs.process.environment"; //$NON-NLS-1$
	public static final String RCS_PROCESS_CLUSTER = "rcs.process.cluster"; //$NON-NLS-1$
	public static final String RCS_PROCESS_CONFIGLOCATIONS = "rcs.process.configlocations"; //$NON-NLS-1$

	@Override
	public String resolveValue(final IDynamicVariable variable, final String argument) throws CoreException {
		if (RCS_PROCESS_CONFIGLOCATIONS.equals(variable.getName())) {
			return GoodiesPlugin.getDefault().getPreferenceStore().getString(RcsServerArgument.RCS_CONFIG_DIR.name());
		}
		if (RCS_PROCESS_ENVIRONMENT.equals(variable.getName())) {
			return GoodiesPlugin.getDefault().getPreferenceStore().getString(RCS_PROCESS_ENVIRONMENT);
		}
		if (RCS_PROCESS_CLUSTER.equals(variable.getName())) {
			return GoodiesPlugin.getDefault().getPreferenceStore().getString(RCS_PROCESS_CLUSTER);
		}
		return null;
	}

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore preferenceStore = GoodiesPlugin.getDefault().getPreferenceStore();
		preferenceStore.setDefault(RCS_PROCESS_ENVIRONMENT, "environment"); //$NON-NLS-1$
		preferenceStore.setDefault(RCS_PROCESS_CLUSTER, "clustername"); //$NON-NLS-1$
	}
}
