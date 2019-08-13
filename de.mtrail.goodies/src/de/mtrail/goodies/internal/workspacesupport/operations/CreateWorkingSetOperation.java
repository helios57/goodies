package de.mtrail.goodies.internal.workspacesupport.operations;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkingSet;

import de.mtrail.goodies.internal.workspacesupport.util.WorkspaceUtility;

/**
 * Creates a WorkingSet for a given project instance and adds it to the workingSetManager.
 */
public class CreateWorkingSetOperation {

	/**
	 * Creates a WorkingSet, if not existing and adds all the referenced plugins of
	 * the feature project to it.
	 * 
	 * @param featureProject an instance of a Project of Feature Nature.
	 */
	public void createWorkingSetFromFeatureProject(IProject featureProject) {
		IWorkingSet featureWorkingSet = WorkspaceUtility.createOrGetWorkingSet(featureProject.getName());
		IProject[] referencedPlugins = WorkspaceUtility.getReferencedPlugins(featureProject);
		
		featureWorkingSet.setElements(featureWorkingSet.adaptElements(referencedPlugins));
	}
}