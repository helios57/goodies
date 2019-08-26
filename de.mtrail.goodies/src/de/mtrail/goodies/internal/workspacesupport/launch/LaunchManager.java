package de.mtrail.goodies.internal.workspacesupport.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * The {@link LaunchManager} gets notified about launched
 * {@link IWorkspaceLaunch} items and puts these into a LaunchHistory. Users of
 * this class can read the LaunchHistory and can invoke previously added
 * LaunchItems.
 */
public final class LaunchManager {
	private final List<IWorkspaceLaunch> recentlyUsed = new ArrayList<>();

	// Singleton stuff
	private static final LaunchManager INSTANCE;
	static {
		INSTANCE = new LaunchManager();
	}

	public static LaunchManager getInstance() {
		return INSTANCE;
	}

	private LaunchManager() {
		// Singleton cant be instatiated outside scope
	}

	/**
	 * Adds the given {@link IWorkspaceLaunch} to the beginning of the recently used
	 * items and therefore makes it the most recent used one. All other instances of
	 * this launch class will be removed in prior to the addition.
	 *
	 * @param aLaunch must not be null
	 */
	public void addWorkspaceLaunch(final IWorkspaceLaunch aLaunch) {
		Assert.isNotNull(aLaunch);

		recentlyUsed.removeIf(l -> l.equals(aLaunch));
		recentlyUsed.add(0, aLaunch);
	}

	public IWorkspaceLaunch getMostRecentWorkspaceLaunch() {
		return recentlyUsed.get(0);
	}
}