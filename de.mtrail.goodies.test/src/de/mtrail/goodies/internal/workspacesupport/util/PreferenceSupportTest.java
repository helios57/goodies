package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.internal.ui.text.PreferencesAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.mtrail.goodies.internal.GoodiesPreferenceConstants;

public class PreferenceSupportTest {
	private static final String A_VALID_VALUE = "A_VALID_VALUE";

	IPreferenceStore storeMock;
	private PreferenceSupport supportUnderTest;

	@Before
	public void before() {
		storeMock = new PreferencesAdapter();
		supportUnderTest = new PreferenceSupport(storeMock);
	}

	@Test
	public void test1() throws ExecutionException {
		// prepare
		storeMock.setValue(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION, A_VALID_VALUE);

		// test
		supportUnderTest.checkPreferences();

		// verify
		Assert.assertThat(supportUnderTest.getPathString(), CoreMatchers.is(A_VALID_VALUE));
	}
}
