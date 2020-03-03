/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.ui.PlatformUI;

/**
 * Shortcut to launch an RCS server process based on parameters from a shell script
 * with a pre-filled dialog.
 */
public class RcsServerProcessFromShellScriptDialog extends RcsServerProcessFromShellScriptBase {

	private static final String MODE_RUN = "run"; //$NON-NLS-1$

	private static final String GROUP_RUN = "org.eclipse.debug.ui.launchGroup.run"; //$NON-NLS-1$
	private static final String GROUP_DEBUG = "org.eclipse.debug.ui.launchGroup.debug"; //$NON-NLS-1$

	@Override
	protected void doLaunch(ILaunchConfiguration config, String mode) {
		final String group = MODE_RUN.equals(mode) ? GROUP_RUN : GROUP_DEBUG;
		DebugUITools.openLaunchConfigurationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				config, group, null);
	}

}
