package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * All jobs in this plugin are considered to modify more or less the current workspace. The default rule for all those jobs is based on the
 * workspace root. Using this rule will <b>prevent paralllel execution</b> of those jobs.
 */
public final class WorkspaceMutexRule implements ISchedulingRule {

  public static final IWorkspaceRoot RULE = ResourcesPlugin.getWorkspace().getRoot();

  @Override
  public boolean contains(final ISchedulingRule rule) {
    return rule == RULE;
  }

  @Override
  public boolean isConflicting(final ISchedulingRule rule) {
    return rule == RULE;
  }
}