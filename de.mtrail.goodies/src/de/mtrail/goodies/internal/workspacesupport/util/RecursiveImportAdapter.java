package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.internal.wizards.datatransfer.RecursiveImportListener;
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;

@SuppressWarnings("restriction")
public abstract class RecursiveImportAdapter implements RecursiveImportListener {

	@Override
	public void projectCreated(final IProject project) {
	}

	@Override
	public void projectConfigured(final IProject project, final ProjectConfigurator configurator) {
	}

	@Override
	public void errorHappened(final IPath location, final Exception ex) {
	}
}