package de.mtrail.goodies.internal.workspacesupport.model;

import org.eclipse.core.resources.IProject;

/**
 * A WorkspaceProject is a reference to an @link {@link IProject} instance as well as a name to a workingset.
 */
public final class WorkspaceProject {

  private final IProject project;
  private final String workingSetName;

  public WorkspaceProject(final IProject project, final String workingSetName) {
    this.project = project;
    this.workingSetName = workingSetName;
  }

  public IProject getProject() {
    return this.project;
  }

  public String getWorkingSetName() {
    return this.workingSetName;
  }

  @Override
  public String toString() {
    return project.getName() + " WorkingSet: " + workingSetName;
  }
}
