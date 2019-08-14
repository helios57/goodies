package de.mtrail.goodies.internal.workspacesupport.jobs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import de.mtrail.goodies.internal.workspacesupport.util.AbstractWorkspaceJobWithSummary;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollector;

/**
 * Displays the Summary Dialog.
 */
public class ShowSummaryJob extends AbstractWorkspaceJobWithSummary {

	public ShowSummaryJob(final SummaryCollector summaryCollector) {
		super("Show summary", summaryCollector, Job.BUILD);
	}

	@Override
	protected IStatus run_internal(final SubMonitor subMonitor) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				summaryCollector.showSummaryMessage();
			}
		});
		return Status.OK_STATUS;
	}
}