package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import de.mtrail.goodies.internal.GoodiesPreferenceConstants;

/**
 * This class is responsible to ensure properties required by the Goodies Bundle are being set accordingly. This class will (in the near future) also handle
 * Eclipse specific settings which are benefitial for certain requirements during Developement in RCS.
 */
public class PreferenceSupport {

  private final IPreferenceStore preferenceStore;
  private String pathString;

  public PreferenceSupport(final IPreferenceStore preferenceStore) {
    this.preferenceStore = preferenceStore;
  }

  public void checkPreferences() throws ExecutionException {
    final String pathString = preferenceStore.getString(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION);
    if (pathString == null || pathString.isEmpty()) {
      handleEmptyPath();
      throw new ExecutionException(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION + " is not set.");
    }
    this.pathString = pathString;
  }

  public String getPathString() {
    return this.pathString;
  }

  private void handleEmptyPath() {
    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

    final MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
    dialog.setText("workspace.properties not defined");
    dialog.setMessage("Please set the location of the workspace.properties file!");
    dialog.open();

    // Preferences öffnen...
    final PreferenceDialog prefDialog = PreferencesUtil.createPreferenceDialogOn(shell,
        "de.mtrail.goodies.internal.cimonitor.preferences.CIPreferencePage",
        new String[] { "de.mtrail.goodies.internal.cimonitor.preferences.CIPreferencePage" }, null);
    prefDialog.open();
  }

  public boolean getBoolean(final String name) {
    return preferenceStore.getBoolean(name);
  }
}