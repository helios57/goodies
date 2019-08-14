/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.mtrail.goodies.GoodiesPlugin;

/**
 * Preferences zum Launchen von RCS-Server Prozessen.
 * <p>
 * "Field" Komponenten werden mit den Preference-Keys (bzw. Names)
 * initialisiert.
 * <p>
 * Wir speichern die Pfadangaben pauschal unter dem Namen
 * {@link RcsServerArgument#RCS_CONFIG_DIR} ab. Dort befinden sich dann entweder
 * die RCS- oder die RTEX-Default-Pfade.
 */
public class RcsServerProcessLaunchingPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public static final String LAUNCHING_PREFERENCES_ID = "de.mtrail.goodies.launching.rcsserverprocess"; //$NON-NLS-1$

	private static final String PREF_ISRCSCONFIG = "isRcsConfig";

	private boolean isRcsConfig = true;

	private EditEnabledListEditor editor;

	public RcsServerProcessLaunchingPreferencePage() {
		super(GRID);
		setPreferenceStore(GoodiesPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(RcsServerArgument.RCS_INSTALLED_FEATURES.name(), "Installed features:", //$NON-NLS-1$
				getFieldEditorParent()));
		addField(new StringFieldEditor(RcsServerArgument.RCS_SERVICE_LAUNCHER_ID.name(), "Service launcher id:", //$NON-NLS-1$
				getFieldEditorParent()));
		addField(new StringFieldEditor(RcsServerArgument.RCS_WORKING_DIR.name(), "Working directory:", //$NON-NLS-1$
				getFieldEditorParent()));
		addField(new StringFieldEditor(ConfigLocationsVariableResolver.RCS_PROCESS_ENVIRONMENT,
				"rcs.process.environment:", //$NON-NLS-1$
				getFieldEditorParent()));

		addField(new StringFieldEditor(ConfigLocationsVariableResolver.RCS_PROCESS_CLUSTER, "rcs.process.cluster:", //$NON-NLS-1$
				getFieldEditorParent()));

		Composite configParent = new Composite(getFieldEditorParent(), SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(configParent);

		// Defaults im Store hinterlegen
		getPreferenceStore().setDefault(PREF_ISRCSCONFIG, true);

		// Möglicherweise bereits gesetzte Value auslesen
		isRcsConfig = getPreferenceStore().getBoolean(PREF_ISRCSCONFIG);

		editor = new EditEnabledListEditor(RcsServerArgument.RCS_CONFIG_DIR.name(), "Configuration path:", configParent,
				"Configuration", "Configuration path entry:");

		addField(editor);

		final Button button = createButton(configParent,
				String.format("Switch to %s Config", isRcsConfig ? "RTEX" : "RCS"));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				isRcsConfig = !isRcsConfig;
				button.setText(String.format("Switch to %s Config", isRcsConfig ? "RTEX" : "RCS"));
				switchConfig(isRcsConfig);
			}
		});
	}

	private void switchConfig(final boolean isRcs) {
		String defaults = "";
		if (isRcs) {
			defaults = RcsServerArgument.RCS_CONFIG_DIR.getDefault();
		} else /* rtex */ {
			defaults = RcsServerArgument.RTEX_CONFIG_DIR.getDefault();
		}
		getPreferenceStore().putValue(PREF_ISRCSCONFIG, Boolean.toString(isRcs));

		editor.addItems(editor.parseString(defaults));
	}

	private Button createButton(final Composite parent, final String label) {
		Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		setButtonLayoutData(button);
		return button;
	}

	private static class EditEnabledListEditor extends ListEditor {

		private final Button edit;
		private final String title;
		private final String messageBoxText;

		protected EditEnabledListEditor(final String name, final String labelText, final Composite parent,
				final String title, final String messageBoxText) {
			super(name, labelText, parent);
			this.title = title;
			this.messageBoxText = messageBoxText;
			edit = new Button(getButtonBoxControl(parent), SWT.NONE);
			GridDataFactory.fillDefaults().applyTo(edit);
			edit.setText("Edit..."); //$NON-NLS-1$
			edit.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseUp(final MouseEvent e) {
					int selectionIndex = getList().getSelectionIndex();
					String item = getList().getItem(selectionIndex);
					final InputDialog dialog = new InputDialog(getShell(), title, messageBoxText, item, null);
					if (dialog.open() == Dialog.OK) {
						getList().setItem(selectionIndex, dialog.getValue());
						selectionChanged();
					}
				}
			});
			selectionChanged();
		}

		void addItems(final String[] items) {
			getList().removeAll();
			for (String dir : items) {
				getList().add(dir);
			}
		}

		@Override
		protected void selectionChanged() {
			if (edit != null) {
				edit.setEnabled(getList() != null && getList().getSelectionCount() == 1);
			}
			super.selectionChanged();
		}

		@Override
		protected String getNewInputObject() {
			final InputDialog dialog = new InputDialog(getShell(), title, messageBoxText, "", null); //$NON-NLS-1$
			dialog.open();
			return dialog.getValue();
		}

		@Override
		protected String createList(final String[] items) {
			final StringBuilder sb = new StringBuilder();
			for (final String i : items) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(i);
			}
			return sb.toString();
		}

		@Override
		protected String[] parseString(final String stringList) {
			return stringList.split(","); //$NON-NLS-1$
		}
	}
}