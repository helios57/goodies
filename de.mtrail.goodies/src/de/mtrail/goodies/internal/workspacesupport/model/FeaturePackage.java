package de.mtrail.goodies.internal.workspacesupport.model;

import java.util.ArrayList;
import java.util.List;

/**
 * FeaturePackage is a list of bundlenames along with a feature name.
 */
public final class FeaturePackage {

  private final List<String> bundleNames = new ArrayList<>();
  private String featureName;

  public List<String> getBundleNames() {
    return this.bundleNames;
  }

  public String getFeatureName() {
    return this.featureName;
  }

  public void setFeatureName(final String featureName) {
    this.featureName = featureName;
  }
}
