package de.mtrail.goodies.internal.workspacesupport.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

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

  public static Map<String, IProject> createWorkspaceProjectIndex() {
    return //
    Arrays.<IProject> asList(ResourcesPlugin.getWorkspace().getRoot().getProjects())//
        .stream()//
        .collect(Collectors.toMap(p -> p.getName(), p -> p));
  }
}
