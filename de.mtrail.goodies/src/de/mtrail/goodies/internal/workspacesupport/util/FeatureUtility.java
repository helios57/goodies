package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class to help dealing with Feature Plug-ins.
 */
public final class FeatureUtility {

	private static final IProject[] EMPTY_PROJECT_ARRAY = new IProject[] {};

	private static final String FEATURE_NATURE_ID = "org.eclipse.pde.FeatureNature";

	/**
	 * @return <code>true</code>, if the current selection is one or more instances
	 *         of a Feature Project.
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
		final ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(ISelectionService.class);
		final ISelection selection = selectionService.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return null;
	}

	public static boolean isFeature(final Object o) {
		if (o instanceof IProject) {
			final IProject project = (IProject) o;
			try {
				return project.isOpen() && project.isNatureEnabled(FEATURE_NATURE_ID);
			} catch (final CoreException e) {
				ErrorHandler.handle(e);
			}
		}
		return false;
	}

	/**
	 * Finds the {@link IFeatureModel} to the given parameter Project and creates an
	 * array of {@link IProject} instances from the plugins of the feature.
	 * 
	 * @param featureProject A Project of Feature Nature
	 * @return an empty array if no plugins are found in the workspace, or an array
	 *         with existing plugins from the workspace matching the
	 *         {@link IFeaturePlugin} of the parameter Feature project.
	 */
	public static IProject[] getReferencedPlugins(IProject featureProject) {
		// Find Feature Model and its feature plugins
		final IFeatureModel featureModel = PDECore.getDefault().getFeatureModelManager()
				.findFeatureModel(featureProject.getName());
		IFeaturePlugin[] featurePlugins = featureModel.getFeature().getPlugins();

		if (featurePlugins == null || featurePlugins.length == 0) {
			return EMPTY_PROJECT_ARRAY;
		}

		// Create an Index to lookup the actual projects
		Map<String, IProject> projectIndex = WorkspaceUtility.createWorkspaceProjectIndex();

		// create IProject array from the featureplugin instances
		List<IProject> referencedProjects = new ArrayList<IProject>();
		for (IFeaturePlugin featurePlugin : featurePlugins) {
			IProject project = projectIndex.get(featurePlugin.getId());
			if (project != null) {
				referencedProjects.add(project);
			}
		}
		return referencedProjects.toArray(EMPTY_PROJECT_ARRAY);
	}
}
