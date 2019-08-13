package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import de.mtrail.goodies.internal.workspacesupport.operations.ImportProjectsOperation;

/**
 * The {@link SummaryCollectorAdapter} allows you to combine the Listening to operational feedback from the {@link ImportProjectsOperation}
 * while directly reporting these feedbacks to the summary collector of the plugin.
 */
public class SummaryCollectorAdapter extends RecursiveImportAdapter {

  private final SummaryCollector collector;

  public SummaryCollectorAdapter(final SummaryCollector collector) {
    this.collector = collector;
  }

  @Override
  public void errorHappened(final IPath location, final Exception ex) {
    collector.addChildStatus("Error while importing project: ", location, ex);
  }

  @Override
  public void projectCreated(final IProject project) {
    collector.addChildSatus("Successfully imported project: " + project.getName());
  }
}