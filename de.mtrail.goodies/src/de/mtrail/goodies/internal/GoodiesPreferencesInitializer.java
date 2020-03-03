/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.mtrail.goodies.GoodiesPlugin;
import de.mtrail.goodies.internal.launch.RcsServerArgument;

/**
 * This initializer sets the defaults required for RCS Server Arguments.
 * 
 * @see RcsServerArgument
 */
public class GoodiesPreferencesInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = GoodiesPlugin.getDefault().getPreferenceStore();

		// RCS Server Process Launcher:
		for (final RcsServerArgument arg : RcsServerArgument.values()) {
			store.setDefault(arg.name(), arg.getDefault());
		}
	}
}