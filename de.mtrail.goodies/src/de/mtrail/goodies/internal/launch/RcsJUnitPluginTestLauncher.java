/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.internal.launching.IPDEConstants;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.pde.launching.JUnitLaunchConfigurationDelegate;
import org.eclipse.swt.SWT;

@SuppressWarnings("restriction")
public class RcsJUnitPluginTestLauncher extends JUnitLaunchConfigurationDelegate {

  public RcsJUnitPluginTestLauncher() {
    super();
  }

  @Override
  public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
      IProgressMonitor monitor) throws CoreException {
    final ILaunchConfigurationWorkingCopy adjusted = configuration.getWorkingCopy();
    adjustVMArguments(configuration, adjusted);
    adjustApplicationArguments(configuration, adjusted);
    super.launch(adjusted, mode, launch, monitor);
  }

private void adjustApplicationArguments(ILaunchConfiguration configuration,
		ILaunchConfigurationWorkingCopy adjusted) throws CoreException {
	  
	  String appArguments = configuration.getAttribute(IPDELauncherConstants.APPLICATION, "");
	  if (appArguments.isEmpty() || IPDEConstants.CORE_TEST_APPLICATION.equals(appArguments) == false) {
		  adjusted.setAttribute(IPDELauncherConstants.APPLICATION, IPDEConstants.CORE_TEST_APPLICATION);
	  }
}

private void adjustVMArguments(ILaunchConfiguration configuration, final ILaunchConfigurationWorkingCopy adjusted)
      throws CoreException {

    String vmArguments = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, ""); //$NON-NLS-1$

    if (vmArguments.indexOf(IntegrationTestArgument.JUNIT_VM_ARGUMENT.getDefault()) == -1) {
      vmArguments += SWT.LF + IntegrationTestArgument.JUNIT_VM_ARGUMENT.getDefault();
      adjusted.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);
    }
  }
}