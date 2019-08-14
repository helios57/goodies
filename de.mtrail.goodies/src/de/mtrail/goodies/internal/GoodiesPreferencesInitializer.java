/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal;

import static de.mtrail.goodies.internal.GoodiesPreferenceConstants.TEXT_DECORATIONS_FLAG;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.mtrail.goodies.GoodiesPlugin;
import de.mtrail.goodies.internal.launch.RcsServerArgument;

/**
 * Initializer für die Default-Preferences.
 */
public class GoodiesPreferencesInitializer extends AbstractPreferenceInitializer {

  public static Map<String, String> getInitializationEntries() {

    Map<String, String> entries = new HashMap<>();
    // Text Decorations anschalten
    entries.put(TEXT_DECORATIONS_FLAG, Boolean.toString(true));
    return entries;
  }

  @Override
  public void initializeDefaultPreferences() {
    final IPreferenceStore store = GoodiesPlugin.getDefault().getPreferenceStore();

    // RCS Server Process Launcher:
    for (RcsServerArgument arg : RcsServerArgument.values()) {
      store.setDefault(arg.name(), arg.getDefault());
    }

    // RCS Goodies Decoration Defaults:
    for (Map.Entry<String, String> entry : getInitializationEntries().entrySet()) {
      store.setDefault(entry.getKey(), entry.getValue());
    }
  }

}
