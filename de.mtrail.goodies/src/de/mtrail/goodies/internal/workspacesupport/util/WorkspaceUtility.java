package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * Collection of helper methods to deal with items from the current workspace.
 */
// We are using lots PDECore magic here
@SuppressWarnings("restriction")
public class WorkspaceUtility {

	private static final IProject[] EMPTY_PROJECT_ARRAY = new IProject[] {};

	/**
	 * @return a Map with all projects of the workspace mapped to their name.
	 */
	public static Map<String, IProject> createWorkspaceProjectIndex() {
		return //
		Arrays.<IProject>asList(ResourcesPlugin.getWorkspace().getRoot().getProjects())//
				.stream()//
				.collect(Collectors.toMap(p -> p.getName(), p -> p));
	}

	/**
	 * Creates a new WorkingSet with parameter String as name and its Id set to
	 * {@link IWorkingSetIDs#JAVA}.
	 * 
	 * @param newWorkingSetName the name of the WorkingSet to create
	 * 
	 * @return the newly created WorkingSet or the existing one with the parameter
	 *         name.
	 */
	public static IWorkingSet createOrGetWorkingSet(String newWorkingSetName) {
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet aNewWorkingSet = workingSetManager.getWorkingSet(newWorkingSetName);

		// no existing WorkingSet found, create one...
		if (aNewWorkingSet == null) {
			aNewWorkingSet = workingSetManager.createWorkingSet(newWorkingSetName, new IAdaptable[0]);
			aNewWorkingSet.setId(IWorkingSetIDs.JAVA);
			workingSetManager.addWorkingSet(aNewWorkingSet);
		}
		return aNewWorkingSet;
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