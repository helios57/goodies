package de.mtrail.goodies.internal.workspacesupport.model;

/**
 * A Bundle configuration combines a bundle name, a designated working set name and a {@link State}.
 */
public final class BundleConfig {

  private final String bundleName;
  private State state;
  private String workingSetName;

  public BundleConfig(final String bundleName) {
    this.bundleName = bundleName;
  }

  public String getBundleName() {
    return bundleName;
  }

  public void setState(final State state) {
    this.state = state;
  }

  public State getState() {
    return state;
  }

  public String getWorkingSetName() {
    return this.workingSetName;
  }

  public void setWorkingSetName(final String workingSetName) {
    this.workingSetName = workingSetName;
  }

  @Override
  public String toString() {
    return bundleName + " state:" + state + " ws:" + workingSetName;
  }
}