package de.mtrail.goodies.internal.workspacesupport.launch;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public abstract class AbstractGoodiesHandler extends AbstractHandler implements IWorkspaceLaunch {

	@Override
	public final Object execute(final ExecutionEvent event) throws ExecutionException {
		final Object result = launch(event);
		LaunchManager.getInstance().addWorkspaceLaunch(this);
		return result;
	}
}