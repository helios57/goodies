/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.PreferencesUtil;

import de.mtrail.goodies.GoodiesPlugin;

/**
 * Tab zum Bearbeiten der Launcher-Argumente.
 */
class RcsServerProcessApplicationTab extends AbstractLaunchConfigurationTab {

  private static final String TAB_IMAGE = "icons/obj16/application_tab.gif"; //$NON-NLS-1$

  private final Map<RcsServerArgument, String> arguments;

  private TableViewer argumentsViewer;
  private Link environmentInfo;
  private Button clearWorkspace;

  RcsServerProcessApplicationTab() {
    arguments = new HashMap<RcsServerArgument, String>();
  }

  @Override
  public String getName() {
    return "Application";
  }

  @Override
  public Image getImage() {
    return GoodiesPlugin.getDefault().getImage(TAB_IMAGE);
  }

  @Override
  public void createControl(Composite parent) {
    final Composite comp = new Composite(parent, SWT.NONE);
    setControl(comp);
    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);
    new Label(comp, SWT.NONE).setText("Application arguments:");

    environmentInfo = new Link(comp, SWT.NONE);
    updateEnvironmentInfo();
    environmentInfo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        openRcsLaunchPreferences();
      }
    });
    GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(environmentInfo);

    argumentsViewer = new TableViewer(comp, SWT.BORDER | SWT.FULL_SELECTION);
    GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(argumentsViewer.getTable());
    argumentsViewer.getTable().setHeaderVisible(true);
    argumentsViewer.getTable().setLinesVisible(true);
    argumentsViewer.setContentProvider(new ArrayContentProvider());

    TableViewerColumn keyColumn = new TableViewerColumn(argumentsViewer, SWT.LEFT);
    keyColumn.getColumn().setText("Argument");
    keyColumn.setLabelProvider(new CellLabelProvider() {

      @Override
      public void update(ViewerCell cell) {
        final RcsServerArgument arg = (RcsServerArgument) cell.getElement();
        cell.setText(arg.name());
      }
    });

    TableViewerColumn valueColumn = new TableViewerColumn(argumentsViewer, SWT.LEFT);
    valueColumn.getColumn().setText("Value");
    valueColumn.setLabelProvider(new CellLabelProvider() {

      @Override
      public void update(ViewerCell cell) {
        final RcsServerArgument arg = (RcsServerArgument) cell.getElement();
        final String value = arguments.get(arg);
        cell.setText(value);
        if (arg.getPreference().equals(value)) {
          cell.setForeground(argumentsViewer.getTable().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        }
        else {
          cell.setForeground(null);
        }
      }
    });
    valueColumn.setEditingSupport(new EditingSupport(argumentsViewer) {

      @Override
      protected void setValue(Object element, Object value) {
        arguments.put((RcsServerArgument) element, (String) value);
        argumentsViewer.refresh(element);
        setDirty(true);
        updateLaunchConfigurationDialog();
      }

      @Override
      protected Object getValue(Object element) {
        return arguments.get(element);
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(argumentsViewer.getTable());
      }

      @Override
      protected boolean canEdit(Object element) {
        return true;
      }
    });
    argumentsViewer.setInput(RcsServerArgument.values());
    GridDataFactory.swtDefaults().hint(1, SWT.DEFAULT).grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1)
        .applyTo(argumentsViewer.getControl());

    clearWorkspace = new Button(comp, SWT.CHECK);
    clearWorkspace.setText("Clear working directory");
    clearWorkspace.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        setDirty(true);
        updateLaunchConfigurationDialog();
      }
    });
    GridDataFactory.swtDefaults().span(2, 1).applyTo(clearWorkspace);
  }

  @Override
  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
  }

  @Override
  public void initializeFrom(ILaunchConfiguration configuration) {
    for (RcsServerArgument arg : RcsServerArgument.values()) {
      try {
        arguments.put(arg, arg.getValue(configuration));
      }
      catch (CoreException e) {
        GoodiesPlugin.getDefault().logError(e);
      }
    }
    argumentsViewer.refresh();
    packColumns();
    try {
      clearWorkspace.setSelection(configuration.getAttribute(IPDELauncherConstants.DOCLEAR, false));
    }
    catch (CoreException e) {
      GoodiesPlugin.getDefault().logError(e);
    }
  }

  @Override
  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    for (RcsServerArgument arg : RcsServerArgument.values()) {
      final String value = arguments.get(arg);
      if (arg.getPreference().equals(value)) {
        configuration.removeAttribute(arg.getId());
      }
      else {
        configuration.setAttribute(arg.getId(), value);
      }
    }
    configuration.setAttribute(IPDELauncherConstants.DOCLEAR, clearWorkspace.getSelection());
  }

  private void packColumns() {
    for (TableColumn c : argumentsViewer.getTable().getColumns()) {
      c.pack();
    }
  }

  private void openRcsLaunchPreferences() {
    final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(),
        RcsServerProcessLaunchingPreferencePage.LAUNCHING_PREFERENCES_ID,
        new String[] { RcsServerProcessLaunchingPreferencePage.LAUNCHING_PREFERENCES_ID }, null);
    dialog.open();
    updateEnvironmentInfo();
  }

  private void updateEnvironmentInfo() {
    final IPreferenceStore store = GoodiesPlugin.getDefault().getPreferenceStore();
    final String environment = store.getString(ConfigLocationsVariableResolver.RCS_PROCESS_ENVIRONMENT);
    final String cluster = store.getString(ConfigLocationsVariableResolver.RCS_PROCESS_CLUSTER);
    environmentInfo.setText(NLS.bind("<a>Environment:</a> {0}  <a>Cluster:</a> {1}", environment, cluster));
    environmentInfo.getParent().layout(); // Labelgroesse kann sich aendern
  }

}
