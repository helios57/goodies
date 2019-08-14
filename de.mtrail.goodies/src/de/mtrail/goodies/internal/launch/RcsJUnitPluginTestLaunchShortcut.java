package de.mtrail.goodies.internal.launch;

import org.eclipse.pde.ui.launcher.JUnitWorkbenchLaunchShortcut;

public class RcsJUnitPluginTestLaunchShortcut extends JUnitWorkbenchLaunchShortcut {

	public RcsJUnitPluginTestLaunchShortcut() {
		super();
	}

	@Override
	protected String getLaunchConfigurationTypeId() {
		return "de.mtrail.goodies.rcsJUnitPluginTest"; //$NON-NLS-1$
	}
}