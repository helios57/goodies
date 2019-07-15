/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import de.mtrail.goodies.GoodiesPlugin;

public enum IntegrationTestArgument {

  JUNIT_VM_ARGUMENT("-Drcs.process.configlocations=${rcs.process.configlocations}"); //$NON-NLS-1$

  private final String defaultValue;

  private IntegrationTestArgument(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getId() {
    return "de.mtrail.goodies.rcsjunitplugintest." + name(); //$NON-NLS-1$
  }

  public String getDefault() {
    return this.defaultValue;
  }

  public String getPreference() {
    return GoodiesPlugin.getDefault().getPreferenceStore().getString(name());
  }

  public String getValue(ILaunchConfiguration config) throws CoreException {
    return config.getAttribute(getId(), getPreference());
  }
}