/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;

import de.mtrail.goodies.GoodiesPlugin;

/**
 * Abstracte Basisimplementierung, um RCS-Processe anhand der Parameter eines Shell-Scripts zu starten.
 */
abstract class RcsServerProcessFromShellScriptBase implements ILaunchShortcut2 {

  private static final String LAUNCH_TYPE = "de.mtrail.goodies.rcsServerProcess"; //$NON-NLS-1$

  @Override
  public void launch(final ISelection selection, final String mode) {
    try {
      final Map<RcsServerArgument, String> arguments = readArguments(adaptToFile(selection));
      ILaunchConfiguration config = null;

      // 1. Nach bestehenden Konfigurationen suchen:
      ILaunchConfiguration[] existing = getLaunchConfigurations(selection);
      if (existing.length > 0) {
        config = existing[0];
      }

      // 2. Neue Konfiguration anlegen:
      if (config == null) {
        config = createLaunchConfiguration(arguments);
      }
      doLaunch(config, mode);
    }
    catch (CoreException e) {
      GoodiesPlugin.getDefault().logError(e);
    }
    catch (IOException e) {
      GoodiesPlugin.getDefault().logError(e);
    }
  }

  @Override
  public void launch(final IEditorPart editor, final String mode) {
    launch(new StructuredSelection(editor.getEditorInput()), mode);
  }

  @Override
  public ILaunchConfiguration[] getLaunchConfigurations(final ISelection selection) {
    final List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
    try {
      final Map<RcsServerArgument, String> arguments = readArguments(adaptToFile(selection));
      final String processName = arguments.get(RcsServerArgument.RCS_PROCESS_NAME);
      if (processName != null) {
        final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        final ILaunchConfigurationType type = manager.getLaunchConfigurationType(LAUNCH_TYPE);
        for (final ILaunchConfiguration config : manager.getLaunchConfigurations(type)) {
          if (processName.equals(config.getAttribute(RcsServerArgument.RCS_PROCESS_NAME.getId(), (String) null))) {
            result.add(config);
          }
        }
      }
    }
    catch (CoreException e) {
      GoodiesPlugin.getDefault().logError(e);
    }
    catch (IOException e) {
      GoodiesPlugin.getDefault().logError(e);
    }
    return result.toArray(new ILaunchConfiguration[result.size()]);
  }

  @Override
  public ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editor) {
    return getLaunchConfigurations(new StructuredSelection(editor.getEditorInput()));
  }

  @Override
  public IResource getLaunchableResource(final ISelection selection) {
    return adaptToFile(selection);
  }

  @Override
  public IResource getLaunchableResource(final IEditorPart editor) {
    return getLaunchableResource(new StructuredSelection(editor.getEditorInput()));
  }

  private IFile adaptToFile(final ISelection selection) {
    Object element = ((IStructuredSelection) selection).getFirstElement();
    return ((IAdaptable) element).getAdapter(IFile.class);
  }

  /**
   * Neue Launchkonfiguration erstellen.
   */
  private ILaunchConfiguration createLaunchConfiguration(final Map<RcsServerArgument, String> arguments) throws CoreException {
    final ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
    final ILaunchConfigurationType type = lm.getLaunchConfigurationType(LAUNCH_TYPE);
    String name = arguments.get(RcsServerArgument.RCS_PROCESS_NAME);
    if (name == null) {
      name = type.getName();
    }
    name = lm.generateLaunchConfigurationName(name);
    final ILaunchConfigurationWorkingCopy wc = type.newInstance(null, name);
    for (Map.Entry<RcsServerArgument, String> entry : arguments.entrySet()) {
      wc.setAttribute(entry.getKey().getId(), entry.getValue());
    }
    return wc.doSave();
  }

  private Map<RcsServerArgument, String> readArguments(final IFile file) throws CoreException, IOException {
    final Map<RcsServerArgument, String> arguments = new HashMap<RcsServerArgument, String>();
    // Wir laden das Shell-Script einfach als Property-Datei, das passt syntaktisch soweit:
    final Properties properties = new Properties();

    try (final InputStream contents = file.getContents()) {
      properties.load(contents);
    }
    for (RcsServerArgument arg : RcsServerArgument.values()) {
      final String value = properties.getProperty(arg.name());
      if (value != null) {
        arguments.put(arg, trim(value));
      }
    }
    return arguments;
  }

  private String trim(String value) {
    value = value.trim();
    if (value.startsWith("\"")) { //$NON-NLS-1$
      value = value.substring(1);
    }
    if (value.endsWith("\"")) { //$NON-NLS-1$
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  protected abstract void doLaunch(ILaunchConfiguration config, String mode);

}
