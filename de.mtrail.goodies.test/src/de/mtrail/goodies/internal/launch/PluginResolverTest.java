package de.mtrail.goodies.internal.launch;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

public class PluginResolverTest {

	@Test
	public void testGetWorkspacePlugins() throws CoreException {
		// prepare
		final PluginResolver resolver = new PluginResolver(Arrays.asList("de.mtrail.goodies.product"),
				Arrays.asList("de.mtrail.goodies.feature"));

		// test - There are no featues or products in the test workspace
		final Set<String> workspacePlugins = resolver.getWorkspacePlugins();

		// verify
		Assert.assertEquals(0, workspacePlugins.size());
	}
}
