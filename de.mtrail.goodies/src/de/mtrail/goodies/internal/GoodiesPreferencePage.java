package de.mtrail.goodies.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.mtrail.goodies.GoodiesPlugin;

/**
 * Preference Page für die Optionen des Goodies Plug-Ins
 */
public class GoodiesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor workspaceConfigLocationEditor;

	private BooleanFieldEditor workingSetEditor;

	private BooleanFieldEditor importProjectsEditor;

	private BooleanFieldEditor importOpenOnlyProjectsEditor;

	public GoodiesPreferencePage() {
		super(GRID);
		setPreferenceStore(GoodiesPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void adjustGridLayout() {
	}

	@Override
	public void createFieldEditors() {

		final Layout layout = getFieldEditorParent().getLayout();
		((GridLayout) layout).numColumns = 1;

		final Group group = new Group(getFieldEditorParent(), SWT.SHADOW_OUT);
		group.setText("Workspace Support");
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

		// Absolute path to the config
		workspaceConfigLocationEditor = new StringFieldEditor(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION,
				"workspace.properties Location:", 70, group);
		workspaceConfigLocationEditor.getLabelControl(group).setToolTipText(
				"The absolute path to the Workspace Properties File e.g.: C:/older/folder/xyz-workspace.properties");
		workspaceConfigLocationEditor
				.setStringValue(getPreferenceStore().getString(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION));
		setUpField(workspaceConfigLocationEditor);

		// Add the fugly BrowseButton
		final Button browse = new Button(group, SWT.PUSH);
		browse.setText("Browse ...");
		browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 0));
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
				dialog.setFilterExtensions(new String[] { "*.properties;*.txt;*.*" });
				final String path = dialog.open();
				if (path != null) {
					workspaceConfigLocationEditor.setStringValue(path);
				}
			}
		});

		// using WorkingSets?
		workingSetEditor = new BooleanFieldEditor(GoodiesPreferenceConstants.USE_WORKING_SETS, "Use WorkingSets?",
				group);
		setUpField(workingSetEditor);

		// automatic import of missing projects?
		importProjectsEditor = new BooleanFieldEditor(GoodiesPreferenceConstants.IMPORT_PROJECTS,
				"Import missing projects automatically?", group);
		setUpField(importProjectsEditor);

		// Import those projects marked as "open" in the properties?
		importOpenOnlyProjectsEditor = new BooleanFieldEditor(GoodiesPreferenceConstants.IMPORT_OPEN_ONLY_PROJECTS,
				"Import those projects being marked with \"open\" in the properties?", group);
		setUpField(importOpenOnlyProjectsEditor);
	}

	private void setUpField(final FieldEditor fieldEditor) {
		addField(fieldEditor);
		fieldEditor.setPreferenceStore(getPreferenceStore());
		fieldEditor.setPage(this);
		fieldEditor.load();
	}

	@Override
	public boolean performOk() {
		final boolean b = super.performOk();

		getPreferenceStore().setValue(GoodiesPreferenceConstants.WORKSPACE_CONFIG_LOCATION,
				workspaceConfigLocationEditor.getStringValue());
		getPreferenceStore().setValue(GoodiesPreferenceConstants.USE_WORKING_SETS, workingSetEditor.getBooleanValue());
		getPreferenceStore().setValue(GoodiesPreferenceConstants.IMPORT_PROJECTS,
				importProjectsEditor.getBooleanValue());
		getPreferenceStore().setValue(GoodiesPreferenceConstants.IMPORT_OPEN_ONLY_PROJECTS,
				importOpenOnlyProjectsEditor.getBooleanValue());
		return b;
	}

	@Override
	public void init(final IWorkbench workbench) {
		/* intentionally empty */
	}
}