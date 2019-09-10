/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal;

import java.util.ArrayList;
import java.util.List;

public class SuccessfulDecoratedResources {

	private final List<Object> resources = new ArrayList<Object>();

	public void addResource(final Object resource) {
		if (!resources.contains(resource)) {
			resources.add(resource);
		}
	}

	public void removeResource(final Object resource) {
		if (resources.contains(resource)) {
			resources.remove(resource);
		}
	}

	public List<Object> getResources() {
		return resources;
	}

	public void dispose() {
		resources.clear();
	}
}
