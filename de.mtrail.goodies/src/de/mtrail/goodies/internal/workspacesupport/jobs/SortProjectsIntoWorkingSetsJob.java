package de.mtrail.goodies.internal.workspacesupport.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.util.AbstractWorkspaceJobWithSummary;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollector;
import de.mtrail.goodies.internal.workspacesupport.util.WorkspaceUtility;

/**
 * FIXME: Translate
 *
 */
@SuppressWarnings("restriction")
public class SortProjectsIntoWorkingSetsJob extends AbstractWorkspaceJobWithSummary {

  private final Map<String, BundleConfig> workspaceConfiguration;

  public SortProjectsIntoWorkingSetsJob(final SummaryCollector summaryCollector, final Map<String, BundleConfig> workspaceConfiguration) {
    super("Sort projects into working sets", summaryCollector);
    this.workspaceConfiguration = workspaceConfiguration;
  }

  @Override
  protected IStatus run_internal(final SubMonitor subMonitor) {
    sortInWorkingSets(subMonitor);
    return Status.OK_STATUS;
  }

  private void sortInWorkingSets(final SubMonitor subMonitor) {
    // obtain all exising WorkingSets
    final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    final Map<String, IWorkingSet> existingWorkingIdx = createExistingWorkingSetsIdx(workingSetManager);

    Map<String, IProject> projectIdx = WorkspaceUtility.createWorkspaceProjectIndex();

    // Create "workIndex", or all workingSet Names from the workspace configuration mapped to the IProject instances of thw workspace
    final Map<String, List<IProject>> workingSetsToIProjectsIdx = createWsIndex(workspaceConfiguration, projectIdx);

    Set<String> wsNames = workingSetsToIProjectsIdx.keySet();
    subMonitor.setWorkRemaining(wsNames.size());

    for (final String wsName : wsNames) {
      final IWorkingSet aWorkingSet = findOrCreate(wsName, workingSetManager, existingWorkingIdx);
      aWorkingSet.setElements(aWorkingSet.adaptElements(workingSetsToIProjectsIdx.get(wsName).toArray(new IAdaptable[0])));

      subMonitor.worked(1);
    }

    subMonitor.done();
  }

  private Map<String, IWorkingSet> createExistingWorkingSetsIdx(final IWorkingSetManager workingSetManager) {
    final List<IWorkingSet> workingSets = Arrays.asList(workingSetManager.getAllWorkingSets());
    final Map<String, IWorkingSet> existingWorkingSets = workingSets.stream().collect(Collectors.toMap(IWorkingSet::getName, w -> w));
    return existingWorkingSets;
  }

  /**
   * Index mit "WorkingSet" Name und Liste der dazugehörigen IProject Instanzen
   *
   * @param workspaceConfiguration
   *          Index für die BundleConfigs (hier steckt der konfigurierte WorkingSet Name)
   * @param projectIdx
   *          Index der Projekte, die tatsächlich im Workspace sind
   * @return Map mit WorkingSetName -> Liste der IProjects
   */
  private Map<String, List<IProject>> createWsIndex(final Map<String, BundleConfig> workspaceConfiguration,
      final Map<String, IProject> projectIdx) {
    final Map<String, List<IProject>> wsProjectsIdx = new HashMap<String, List<IProject>>();
    addToIdx(wsProjectsIdx, workspaceConfiguration.values(), projectIdx);
    return wsProjectsIdx;
  }

  private void addToIdx(final Map<String, List<IProject>> workingSetProjectsIdx, final Collection<BundleConfig> bundleConfigurations,
      final Map<String, IProject> projectIdx) {
    for (final BundleConfig bundleConfig : bundleConfigurations) {
      // Wichtig: Nur IProject Instanzen in WorkingSets sortieren, die auch im Workspace (also im projectIdx sind)
      if (projectIdx.containsKey(bundleConfig.getBundleName())) {
        final IProject iProject = projectIdx.get(bundleConfig.getBundleName());
        workingSetProjectsIdx.computeIfAbsent(bundleConfig.getWorkingSetName(), v -> new ArrayList<IProject>()).add(iProject);
      }
    }
  }

  private IWorkingSet findOrCreate(final String wsName, final IWorkingSetManager workingSetManager,
      final Map<String, IWorkingSet> existingWorkingIdx) {
    if (existingWorkingIdx.containsKey(wsName)) {
      return existingWorkingIdx.get(wsName);
    }
    // create a new workingSet and add it to the workingSetManager (items will be added somewhere else)
    final IWorkingSet workingSet = workingSetManager.createWorkingSet(wsName, new IAdaptable[0]);
    workingSet.setId(IWorkingSetIDs.JAVA);
    existingWorkingIdx.put(wsName, workingSet);
    workingSetManager.addWorkingSet(workingSet);
    return workingSet;
  }
}