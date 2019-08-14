package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A simple Eclipse Job, which <br>
 * <ul>
 * <li>holds access to a summary collector instance to provide summary
 * information of this job during its execution.</li>
 * <li>Defines a mutex job rule to avoid parallel execution of the jobs.</li>
 * </ul>
 * <br>
 * Subclasses may erase the previously set jobrule to enable simultanous
 * execution of the job. This can only be applied for jobs not dependant on
 * results of other jobs of this plugin.
 */
public abstract class AbstractWorkspaceJobWithSummary extends Job {

	protected final SummaryCollector summaryCollector;

	public AbstractWorkspaceJobWithSummary(final String name, final SummaryCollector summaryCollector) {
		this(name, summaryCollector, Job.LONG);
	}

	public AbstractWorkspaceJobWithSummary(final String name, final SummaryCollector summaryCollector,
			final int priority) {
		super(name);
		this.summaryCollector = summaryCollector;
		setRule(WorkspaceMutexRule.RULE);
		setPriority(priority);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		return run_internal(SubMonitor.convert(monitor));
	}

	/**
	 * Must be implemented by subclass. The monitor must not be converted or in any
	 * other way being manipulated except by IDE actions/handlers used within the
	 * method.
	 */
	protected abstract IStatus run_internal(final SubMonitor subMonitor);
}