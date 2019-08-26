package de.mtrail.goodies.internal.workspacesupport.handler;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.mtrail.goodies.GoodiesPlugin;
import de.mtrail.goodies.internal.GoodiesPreferenceConstants;
import de.mtrail.goodies.internal.workspacesupport.jobs.ImportProjectsJob;
import de.mtrail.goodies.internal.workspacesupport.jobs.ShowSummaryJob;
import de.mtrail.goodies.internal.workspacesupport.launch.AbstractGoodiesHandler;
import de.mtrail.goodies.internal.workspacesupport.model.BundleConfig;
import de.mtrail.goodies.internal.workspacesupport.util.PreferenceSupport;
import de.mtrail.goodies.internal.workspacesupport.util.SummaryCollector;
import de.mtrail.goodies.internal.workspacesupport.util.WorkspacePropertiesReader;

/**
 * This is a composite handler using the {@link ImportProjectsJob} and the
 * {@link SummaryCollector} to allow users to separately import all projects
 * according their preference settings.
 */
public class ImportProjectsHandler extends AbstractGoodiesHandler {

	@Override
	public Object launch(final ExecutionEvent event) throws ExecutionException {

		final PreferenceSupport preferenceSupport = new PreferenceSupport(
				GoodiesPlugin.getDefault().getPreferenceStore());
		preferenceSupport.checkPreferences();

		final SummaryCollector summaryCollector = new SummaryCollector();

		Map<String, BundleConfig> configuration;
		try {
			configuration = WorkspacePropertiesReader.createConfiguration(preferenceSupport.getPathString());
		} catch (final IOException e) {
			throw new ExecutionException("Cannot read properties file", e);
		}

		final ImportProjectsJob importProjectsJob = new ImportProjectsJob(preferenceSupport.getPathString(),
				configuration, summaryCollector, preferenceSupport);
		importProjectsJob.schedule();

		final ShowSummaryJob showSummary = new ShowSummaryJob(summaryCollector);
		showSummary.schedule();

		return null;
	}

	@Override
	public boolean isEnabled() {
		return GoodiesPlugin.getDefault().getPreferenceStore()
				.getString(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION) != null;
	}
}