package de.mtrail.goodies.internal.workspacesupport.jobs;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import de.mtrail.goodies.internal.workspacesupport.jobs.operations.ImportProjectsOperation;
import de.mtrail.goodies.internal.workspacesupport.jobs.operations.SummaryCollectorAdapter;
import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.util.PreferenceSupport;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollector;

/**
 * This Job uses the custom {@link #ImportProjectsJob(String, Map, SummaryCollector, PreferenceSupport)} to import all projects not in
 * workspace already.
 */
public class ImportProjectsJob extends AbstractWorkspaceJobWithSummary {

  private final ImportProjectsOperation operation;

  public ImportProjectsJob(final String path, final Map<String, BundleConfig> workspaceConfiguration,
      final SummaryCollector summaryCollector, final PreferenceSupport preferenceSupport) {
    super("Import projects into workspace", summaryCollector);
    operation = new ImportProjectsOperation(//
        path, workspaceConfiguration, new SummaryCollectorAdapter(summaryCollector), preferenceSupport);
  }

  @Override
  protected IStatus run_internal(final SubMonitor subMonitor) {
    operation.init();
    if (operation.getBundlesNotInWorkspace().isEmpty() == false) {
      return operation.run(subMonitor);
    }
    return Status.OK_STATUS;
  }
}