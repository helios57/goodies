package de.mtrail.goodies.internal.workspacesupport;

import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelection;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import de.mtrail.goodies.internal.workspacesupport.operations.CreateWorkingSetOperation;
import de.mtrail.goodies.internal.workspacesupport.util.FeatureUtility;

/**
 * This handler creates WorkingSets from selected feature projects. Supports
 * multi-select.
 */
public class CreateWorkingSetHandler extends AbstractHandler {

	private final FeatureUtility featureEnablement = new FeatureUtility();
	private final CreateWorkingSetOperation createWorkingSetOperation = new CreateWorkingSetOperation();

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IStructuredSelection iStructuredSelection = (IStructuredSelection) getCurrentSelection(event);
		for (Iterator<?> iterator = iStructuredSelection.iterator(); iterator.hasNext();) {
			IProject project = (IProject) iterator.next();
			Display.getCurrent().asyncExec(() -> {
				createWorkingSetOperation.createWorkingSetFromFeatureProject(project);
			});
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return featureEnablement.isFeatureProjectSelected();
	}
}