package de.mtrail.goodies.internal.workspacesupport.launch;

public interface IWorkbenchLaunchListener {

	/**
	 * This method will be called, if the launch of the parameter
	 * {@link IWorkspaceLaunch} has been executed. You cannot obtain the parameter
	 * {@link IWorkspaceLaunch} inatance from the LaunchManager, because it will
	 * most likely being added after this method has been notified.
	 *
	 * @param recentWorkspaceLaunch the not null, recently executed WorkspaceLaunch
	 *                              object.
	 */
	void notify(IWorkspaceLaunch recentWorkspaceLaunch);

}
