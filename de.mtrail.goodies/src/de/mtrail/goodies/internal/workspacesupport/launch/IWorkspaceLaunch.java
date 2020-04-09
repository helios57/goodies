package de.mtrail.goodies.internal.workspacesupport.launch;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Work In Progress
 * <p>
 * A IWorkspaceLaunch is an executed or launched Workspace action, such as
 * Preference File creation, WorkingSet creator or any other command/Action,
 * which resides under the pull down Menu of the goodies bundle Menu. <br>
 * Handler should implement this interface if they want to be re-launched, when
 * user press the pulldown Button.
 */
public interface IWorkspaceLaunch {

	/**
	 * Will be invoked, when a user presses the pulldown button, instead of choosing
	 * a specific item from the menu.
	 */
	Object launch(final ExecutionEvent event) throws ExecutionException;

	boolean isEnabled();
}