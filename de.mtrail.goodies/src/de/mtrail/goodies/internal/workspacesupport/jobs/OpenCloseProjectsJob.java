package de.mtrail.goodies.internal.workspacesupport.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.internal.PartSite;

import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.model.State;
import de.mtrail.goodies.internal.workspacesupport.model.WorkspaceProject;
import de.mtrail.goodies.internal.workspacesupport.util.AbstractWorkspaceJobWithSummary;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollector;
import de.mtrail.goodies.internal.workspacesupport.util.WorkspaceUtility;

@SuppressWarnings("restriction")
public class OpenCloseProjectsJob extends AbstractWorkspaceJobWithSummary {

	private final static String CLOSE_PROJECT = "closeProject";
	private final static String OPEN_PROJECT = "openProject";

	private final Map<String, IAction> actionIdx = new HashMap<>();

	private final Map<String, BundleConfig> workspaceConfiguration;

	public OpenCloseProjectsJob(final SummaryCollector summaryCollector,
			final Map<String, BundleConfig> workspaceConfiguration) {
		super("Open/close projects", summaryCollector);
		this.workspaceConfiguration = workspaceConfiguration;
	}

	@Override
	protected IStatus run_internal(final SubMonitor subMonitor) {

		// declare containers to distinguish between projects to open and close
		final List<WorkspaceProject> projectsToOpen = new ArrayList<>();
		final List<WorkspaceProject> projectsToClose = new ArrayList<>();

		Map<String, IProject> projectIdx = WorkspaceUtility.createWorkspaceProjectIndex();

		findProjectsToCloseAndOpen(workspaceConfiguration, projectIdx, projectsToClose, projectsToOpen);

		subMonitor.setWorkRemaining(projectsToClose.size() + projectsToClose.size());

		if (!projectsToOpen.isEmpty()) {
			syncApplyAction(OPEN_PROJECT, projectsToOpen);
			summaryCollector.addChildStatuses(projectsToOpen, "Opened: ");
		}

		subMonitor.setWorkRemaining(projectsToClose.size());

		if (!projectsToClose.isEmpty()) {
			syncApplyAction(CLOSE_PROJECT, projectsToClose);
			summaryCollector.addChildStatuses(projectsToClose, "Closed: ");
		}

		subMonitor.setWorkRemaining(0);
		subMonitor.done();

		return Status.OK_STATUS;
	}

	private void syncApplyAction(final String id, final List<WorkspaceProject> projects) {
		Display.getDefault().syncExec(() -> applyAction(id, projects));
	}

	private void applyAction(final String id, final List<WorkspaceProject> projects) {
		if (projects.isEmpty()) {
			return; // done
		}
		final IProject[] pArr = projects.stream().//
				map(WorkspaceProject::getProject).//
				collect(Collectors.toList()).//
				toArray(new IProject[0]);

		final IAction action = getAction(id);
		// we need to apply a custom selection, but both actions handle these
		// separately.
		if (action instanceof BaseSelectionListenerAction) { // thats the baseclass for the CloseResourceAction
			final BaseSelectionListenerAction selectionAction = (BaseSelectionListenerAction) action;
			selectionAction.selectionChanged(new StructuredSelection(pArr));
			action.run();
		} else if (action instanceof SelectionDispatchAction) { // thats the baseclass of the OpenResourceAction
			final SelectionDispatchAction dispatchAction = (SelectionDispatchAction) action;
			dispatchAction.run(new StructuredSelection(pArr));
		}
	}

	private void findProjectsToCloseAndOpen(final Map<String, BundleConfig> bundleConfigIdx,
			final Map<String, IProject> projectIdx, final List<WorkspaceProject> projectsToClose,
			final List<WorkspaceProject> projectsToOpen) {
		// Für alle Projekte in den workspace properties entsprechende Aktion merken und
		// die Parameter-Listen füllen
		for (final BundleConfig bundleConfig : bundleConfigIdx.values()) {
			final IProject aProject = projectIdx.get(bundleConfig.getBundleName());
			// consider properties as a text-only and failure-containing source
			if (aProject == null) {
				continue;
			}

			// Projekte schließen, die explizit als "closed" gekennzeichnet sind, Jene die
			// explizit als "open" gekennzeichnet sind, werden geöffnet.
			if (State.closed.equals(bundleConfig.getState())) {
				if (aProject.isOpen()) { // Nur schließen, wenn nicht schon geschehen
					projectsToClose.add(new WorkspaceProject(aProject, bundleConfig.getWorkingSetName()));
				}
			}
			if (State.open.equals(bundleConfig.getState())) {
				if (!aProject.isOpen()) { // Nur öffnen, wenn nicht schon geschehen
					projectsToOpen.add(new WorkspaceProject(aProject, bundleConfig.getWorkingSetName()));
				}
			}
		}
	}

	private IAction getAction(final String actionID) {
		return actionIdx.computeIfAbsent(actionID, k -> getActionInternal(actionID));
	}

	private IAction getActionInternal(final String actionID) {
		// to understand this shit, you must have failed in obtaining the ViewPart
		// I promise to find out, why this is an issue in the IDE.
		// but for now, we need this extra method to have a clear point of failure
		final IViewPart viewPart = findViewPart();
		final SubActionBars bars = (SubActionBars) ((PartSite) viewPart.getSite()).getActionBars();
		return bars.getGlobalActionHandler(actionID);
	}

	private IViewPart findViewPart() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final int c = workbench.getWorkbenchWindowCount();
		if (c == 0) {
			System.err.println("fail?"); // haha
		}

		IViewPart viewPart = null;
		final IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
		for (final IWorkbenchWindow window : workbenchWindows) {
			final IWorkbenchPage aPage = window.getActivePage();
			if (aPage != null) {
				viewPart = aPage.findView("org.eclipse.jdt.ui.PackageExplorer");
				if (viewPart != null) {
					break;
				}
			}
		}
		return viewPart;
	}
}