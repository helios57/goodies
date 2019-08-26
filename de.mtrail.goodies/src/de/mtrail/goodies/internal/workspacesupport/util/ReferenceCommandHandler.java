package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.mtrail.goodies.internal.workspacesupport.launch.IWorkbenchLaunchListener;
import de.mtrail.goodies.internal.workspacesupport.launch.IWorkspaceLaunch;
import de.mtrail.goodies.internal.workspacesupport.launch.LaunchManager;

public final class ReferenceCommandHandler extends AbstractHandler /* =see below= implements IElementUpdater */ {

	List<IWorkbenchLaunchListener> listeners = new ArrayList<>();

	public void addWorkbenchLaunchListener(final IWorkbenchLaunchListener listener) {
		listeners.add(listener);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkspaceLaunch recentWorkspaceLaunch = LaunchManager.getInstance().getMostRecentWorkspaceLaunch();
		if (recentWorkspaceLaunch != null) {
			recentWorkspaceLaunch.launch(event);
			notifyListeners(recentWorkspaceLaunch);
		}
		return null;
	}

	private void notifyListeners(final IWorkspaceLaunch recentWorkspaceLaunch) {
		for (final IWorkbenchLaunchListener l : listeners) {
			l.notify(recentWorkspaceLaunch);
		}
	}

	@Override
	public boolean isEnabled() {
		final IWorkspaceLaunch recentWorkspaceLaunch = LaunchManager.getInstance().getMostRecentWorkspaceLaunch();
		if (recentWorkspaceLaunch != null) {
			return recentWorkspaceLaunch.isEnabled();
		}
		return false;
	}

	// Used later for menu/command/handler update propagations
//	@Override
//	public void updateElement(final UIElement element, final Map parameters) {
//		final IWorkspaceLaunch recentWorkspaceLaunch = LaunchManager.getInstance().getMostRecentWorkspaceLaunch();
//		if (recentWorkspaceLaunch != null) {
//		}
//	}
}