package de.mtrail.goodies.internal.workspacesupport.operations;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.wizards.datatransfer.SmartImportJob;

import de.mtrail.goodies.internal.GoodiesPreferenceConstants;
import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.model.State;
import de.mtrail.goodies.internal.workspacesupport.util.PreferenceSupport;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollectorAdapter;
import de.mtrail.goodies.internal.workspacesupport.util.WorkspaceUtility;

/**
 * This Operation is responsible to import all projects mentionened in the workspace configuration file, but aren't imported yet into the
 * workspace. Its based on the {@link SmartImportJob}, which does *ALL* the work. This operation simply compares the content of the
 * workspace with the elements from the workspace configuration file.
 * <p>
 * An {@link SummaryCollectorAdapter} will be injected to the {@link SmartImportJob} to gain more information about progress and errors.
 */
// We are using internal eclipse.ui, which is discouraged
@SuppressWarnings("restriction")
public final class ImportProjectsOperation {

  private final Map<String, BundleConfig> workspaceConfiguration;
  private final String directoryPath;
  private List<BundleConfig> bundlesNotInWorkspace;
  private final SummaryCollectorAdapter summaryCollectorAdapter;
  private final PreferenceSupport preferenceSupport;
  private Map<String, IProject> projectIdx;

  /**
   * Creates a new ImportProject instance.
   *
   * @param path
   *          a path to a file in the workspace or the path to the workspace directory (must end with a File.separator)
   * @param workspaceConfiguration
   *          the workspace configuration file
   * @param summaryCollectorAdapter
   *          Collector for all status information presented to the user
   * @param preferenceSupport
   *          Goodies Preference Support for fine-tuning the import operation
   */
  public ImportProjectsOperation(final String path, final Map<String, BundleConfig> workspaceConfiguration,
      final SummaryCollectorAdapter summaryCollectorAdapter, final PreferenceSupport preferenceSupport) {
    this.preferenceSupport = preferenceSupport;
    this.directoryPath = directoryPath(path);
    this.workspaceConfiguration = workspaceConfiguration;
    this.summaryCollectorAdapter = summaryCollectorAdapter;
  }

  public void init() {

    projectIdx = WorkspaceUtility.createWorkspaceProjectIndex();

    bundlesNotInWorkspace = workspaceConfiguration.values().stream().//
        filter(b -> shouldBeImported(b)).collect(Collectors.toList());
  }

  private boolean shouldBeImported(final BundleConfig b) {
    if (preferenceSupport.getBoolean(GoodiesPreferenceConstants.IMPORT_OPEN_ONLY_PROJECTS)) {
      if (State.closed.equals(b.getState())) {
        return false;
      }
    }
    return !projectIdx.containsKey(b.getBundleName());
  }

  public IStatus run(final IProgressMonitor monitor) {
    if (bundlesNotInWorkspace.isEmpty()) {
      return Status.OK_STATUS; // phew, nuttin to do
    }

    final SmartImportJob importJob = new SmartImportJob(new File(directoryPath), null, false, true);
    importJob.setDirectoriesToImport(createFileSet(directoryPath, bundlesNotInWorkspace));
    importJob.setListener(summaryCollectorAdapter);
    return importJob.run(monitor);
  }

  private Set<File> createFileSet(final String rootPath, final List<BundleConfig> bundles) {
    return bundles.stream().//
        map(b -> new File(rootPath + File.separator + b.getBundleName())).//
        collect(Collectors.toSet());
  }

  protected String directoryPath(final String pathString) {
    return pathString.substring(0, pathString.lastIndexOf(File.separator));
  }

  public List<BundleConfig> getBundlesNotInWorkspace() {
    return Collections.unmodifiableList(bundlesNotInWorkspace);
  }
}