package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.model.WorkspaceProject;

/**
 * A summary collector collects {@link Status} instances and allows to show those whenever required. We misuse the ErrorDialog here, because
 * it provides the nifty fold-control to show excessive messages.
 */
public final class SummaryCollector {

  private final List<Status> childStatuses = new ArrayList<>();

  public void showSummaryMessage() {
    final MultiStatus multiStatus = new MultiStatus("de.mtrail.goodies", IStatus.INFO, //
        childStatuses.toArray(new Status[] {}), "Workspace configuration completed", null);
    ErrorDialog.openError(Display.getDefault().getActiveShell(), //
        "Summary", "Workspace configurartion completed", multiStatus);
  }

  public void addChildStatuses(final List<WorkspaceProject> projects, final String prefix) {
    projects.forEach(p -> addChildSatus(//
        String.format("%s %s workingSet: %s", prefix, p.getProject().getName(), p.getWorkingSetName())));
  }

  public void addChildStatuses(final List<BundleConfig> bundles) {
    bundles.forEach(b -> addChildSatus(String.format("Import %s", b.getBundleName())));
  }

  public void addChildStatus(final String prefix, final IPath location, final Exception ex) {
    addChildSatus(//
        String.format("%s %s %s", prefix, location.toOSString(), ex.getCause()), IStatus.ERROR);
  }

  public void addChildSatus(final String s) {
    addChildSatus(s, IStatus.INFO);
  }

  public void addChildSatus(final String s, final int iStatus) {
    childStatuses.add(new Status(//
        iStatus, //
        "de.mtrail.goodies", //
        s));
  }

  /**
   * Clears all status messages collected so far.
   */
  public void clear() {
    childStatuses.clear();
  }
}