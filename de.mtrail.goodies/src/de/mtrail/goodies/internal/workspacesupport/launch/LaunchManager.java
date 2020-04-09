package de.mtrail.goodies.internal.workspacesupport.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

/**
 * Work In Progress
 * <p>
 * The {@link LaunchManager} gets notified about launched
 * {@link IWorkspaceLaunch} items and puts these into a LaunchHistory. Users of
 * this class can read the LaunchHistory and can invoke previously added
 * LaunchItems.
 */
public final class LaunchManager {
	private final List<IWorkspaceLaunch> recentlyUsed = new ArrayList<>();

	// Singleton stuff
	private static final LaunchManager INSTANCE;

	public static final IWorkspaceLaunch EMPTY_LAUNCH_LIST_ENTRY = new IWorkspaceLaunch() {

		@Override
		public Object launch(final ExecutionEvent event) throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	};

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

	/**
	 * Returns the most recent {@link IWorkspaceLaunch} entry.
	 *
	 * @return {@link #EMPTY_LAUNCH_LIST_ENTRY} if no item has been launched yet and
	 *         the list is empty.
	 */
	public IWorkspaceLaunch getMostRecentWorkspaceLaunch() {
		if (recentlyUsed.isEmpty()) {
			return EMPTY_LAUNCH_LIST_ENTRY;
		}
		return recentlyUsed.get(0);
	}
}