package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

/**
 * Determines whether the current workbench selection adapts to IProject of nature Feature.
 */
public final class FeatureEnablement {
	
	private static final String FEATURE_NATURE_ID = "org.eclipse.pde.FeatureNature";

	/**
	 * @return <code>true</code>, if the current selection is one or more instances of a Feature Project.
	 */
	public boolean isFeatureProjectSelected() {
		return isFeature(getSelection());
	}
	
	private boolean isFeature(final IStructuredSelection structuredSelection) {
		if (structuredSelection == null || structuredSelection.isEmpty()) {
			return false;
		}

		for (final Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
			final Object selection = iterator.next();
			if (isFeature(selection) == false) {
				return false;
			}
		}
		// All elements are of type Feature, we're good
		return true;
	}
	
	private IStructuredSelection getSelection() {
		final ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ISelectionService.class);
		final ISelection selection = selectionService.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return null;
	}
	
	private boolean isFeature(final Object o)  {
		if (o instanceof IProject) {
			final IProject project = (IProject) o;
			try {
				return project.isNatureEnabled(FEATURE_NATURE_ID);
			} catch (final CoreException e) {
				ErrorHandler.handle(e);
			}
			
		}
		return false;
	}
}
