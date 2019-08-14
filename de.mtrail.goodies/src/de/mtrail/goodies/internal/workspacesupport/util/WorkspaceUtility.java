package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * Collection of helper methods to deal with items from the current workspace.
 */
// We are using lots PDECore magic here
@SuppressWarnings("restriction")
public class WorkspaceUtility {

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
}