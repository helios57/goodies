/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.internal.launching.IPDEConstants;
import org.eclipse.pde.internal.ui.launcher.JUnitProgramBlock;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.pde.ui.launcher.AbstractLauncherTab;
import org.eclipse.pde.ui.launcher.ConfigurationTab;
import org.eclipse.pde.ui.launcher.JUnitTabGroup;
import org.eclipse.pde.ui.launcher.PluginJUnitMainTab;
import org.eclipse.pde.ui.launcher.PluginsTab;
import org.eclipse.pde.ui.launcher.TestTab;
import org.eclipse.pde.ui.launcher.TracingTab;
import org.eclipse.swt.SWT;

/**
 * Erweitert die Standard Plug-In Test Tab um die custom JVM Arguments Tab und
 * ProgramBlockTab ("Main")
 *
 */
@SuppressWarnings("restriction")
public class RcsJUnitPluginTestTabGroup extends JUnitTabGroup {

	public RcsJUnitPluginTestTabGroup() {
		super();
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = null;
		JavaArgumentsTab javaArgumentsTab = new RcsJavaArgumentsTab();

		tabs = new ILaunchConfigurationTab[] { new TestTab(), new RcsPluginJUnitMainTab(), javaArgumentsTab,
				new PluginsTab(), new ConfigurationTab(true), new TracingTab(), new EnvironmentTab(), new CommonTab() };
		setTabs(tabs);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
	}

	/**
	 * Korrigiert die Main-Attribute, wie z.B. "Use Application/Product" und
	 * "Headless" usw.
	 *
	 */
	private static class RcsPluginJUnitMainTab extends PluginJUnitMainTab {

		class RcsJUnitProgramBlock extends JUnitProgramBlock {

			public RcsJUnitProgramBlock(AbstractLauncherTab tab) {
				super(tab);
			}

			@Override
			public void setDefaults(ILaunchConfigurationWorkingCopy config) {
				config.setAttribute(IPDELauncherConstants.USE_PRODUCT, false);
				config.setAttribute(IPDELauncherConstants.APPLICATION, IPDEConstants.CORE_TEST_APPLICATION);
			}

			@Override
			public void initializeFrom(ILaunchConfiguration configuration) throws CoreException {
				ILaunchConfigurationWorkingCopy adjusted;
				try {
					adjusted = configuration.getWorkingCopy();
					if (configuration.getAttribute(IPDELauncherConstants.USE_PRODUCT, false) == true) {
						adjusted.setAttribute(IPDELauncherConstants.USE_PRODUCT, false);
					}

					String appAttribute = configuration.getAttribute(IPDELauncherConstants.APPLICATION, "");
					if (appAttribute.isEmpty() || appAttribute.equals(IPDEConstants.CORE_TEST_APPLICATION) == false) {
						adjusted.setAttribute(IPDELauncherConstants.APPLICATION, IPDEConstants.CORE_TEST_APPLICATION);
					}
					super.initializeFrom(adjusted);
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		protected void createProgramBlock() {
			fProgramBlock = new RcsJUnitProgramBlock(this);
		}
	}

	/**
	 * Verdrahtet die RCS JVM Arguments
	 */
	private static class RcsJavaArgumentsTab extends JavaArgumentsTab {

		@Override
		public void initializeFrom(ILaunchConfiguration configuration) {
			ILaunchConfigurationWorkingCopy adjusted;
			try {
				adjusted = configuration.getWorkingCopy();
				String vmArguments = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
						""); //$NON-NLS-1$
				if (vmArguments.indexOf(IntegrationTestArgument.JUNIT_VM_ARGUMENT.getDefault()) == -1) {
					vmArguments += SWT.LF + IntegrationTestArgument.JUNIT_VM_ARGUMENT.getDefault();
					adjusted.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);
				}
				super.initializeFrom(adjusted);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}
}