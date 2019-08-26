package de.mtrail.goodies.internal.workspacesupport.handler;

import static de.mtrail.goodies.internal.workspacesupport.util.WorkspacePropertiesReader.createConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;

import de.mtrail.goodies.GoodiesPlugin;
import de.mtrail.goodies.internal.GoodiesPreferenceConstants;
import de.mtrail.goodies.internal.workspacesupport.jobs.ImportProjectsJob;
import de.mtrail.goodies.internal.workspacesupport.jobs.OpenCloseProjectsJob;
import de.mtrail.goodies.internal.workspacesupport.jobs.ShowSummaryJob;
import de.mtrail.goodies.internal.workspacesupport.jobs.SortProjectsIntoWorkingSetsJob;
import de.mtrail.goodies.internal.workspacesupport.launch.AbstractGoodiesHandler;
import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.util.ErrorHandler;
import de.mtrail.goodies.internal.workspacesupport.util.PreferenceSupport;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollector;

/**
 * This handler launches a bunch of jobs to (pre-)configure a RCS based
 * workspace.
 */
public class WorkspaceConfigurationHandler extends AbstractGoodiesHandler {

	private final SummaryCollector summaryCollector = new SummaryCollector();
	private final PreferenceSupport preferenceSupport = new PreferenceSupport(
			GoodiesPlugin.getDefault().getPreferenceStore());

	@Override
	public Object launch(final ExecutionEvent event) throws ExecutionException {

		preferenceSupport.checkPreferences(); // PreferenceSupport exits with Exception if not properly configured

		summaryCollector.clear();

		try {
			launchJobs(preferenceSupport.getPathString());
		}
		// Things might happen. We catch, until we know who is the receiver of our
		// problems.
		catch (final Exception e) {
			ErrorHandler.handle(e);
		}
		return null;
	}

	private boolean launchJobs(final String pathString) throws IOException {
		final List<Job> jobs = createJobList(pathString, createConfiguration(pathString.trim()));
		if (!jobs.isEmpty()) {
			jobs.forEach(j -> j.schedule());
		}
		return true;
	}

	/**
	 * This is an import point of execution.
	 * <p>
	 * We distinguish between to kinds of jobs:
	 *
	 * <pre>
	 * LONG jobs are for longer running background jobs. They run only after INTERACTIVE and SHORT jobs have been run.
	 * BUILD jobs are for jobs associated with building tasks. They are a lower priority than LONG. BUILD jobs only run when all LONG jobs are complete.
	 * </pre>
	 *
	 * All 'real' configuration jobs, like {@link ImportProjectsJob},
	 * {@link OpenCloseProjectsJob} and {@link SortProjectsIntoWorkingSetsJob} are
	 * "LONG", while the show summary is "BUILD", to ensure, that this jobs runs
	 * definitely only when those other jobs are finished.
	 */
	private List<Job> createJobList(final String pathString, final Map<String, BundleConfig> workspaceConfiguration) {
		final List<Job> workspaceConfigJobs = new ArrayList<>();

		if (preferenceSupport.getBoolean(GoodiesPreferenceConstants.IMPORT_PROJECTS)) {
			workspaceConfigJobs.add(
					new ImportProjectsJob(pathString, workspaceConfiguration, summaryCollector, preferenceSupport));
		}
		workspaceConfigJobs.add(new OpenCloseProjectsJob(summaryCollector, workspaceConfiguration));

		if (preferenceSupport.getBoolean(GoodiesPreferenceConstants.USE_WORKING_SETS)) {
			workspaceConfigJobs.add(new SortProjectsIntoWorkingSetsJob(summaryCollector, workspaceConfiguration));
		}
		workspaceConfigJobs.add(new ShowSummaryJob(summaryCollector));

		return workspaceConfigJobs;
	}
}