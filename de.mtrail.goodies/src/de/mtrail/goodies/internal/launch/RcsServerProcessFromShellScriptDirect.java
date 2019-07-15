/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;

/**
 * Shortcut, um RCS-Processe anhand der Parameter eines Shell-Scripts direkt zu starten.
 */
public class RcsServerProcessFromShellScriptDirect extends RcsServerProcessFromShellScriptBase {

  @Override
  protected void doLaunch(ILaunchConfiguration config, String mode) {
    DebugUITools.launch(config, mode);
  }

}
