/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.pde.internal.core.iproduct.IProduct;
import org.eclipse.pde.internal.core.iproduct.IProductFeature;
import org.eclipse.pde.internal.core.product.WorkspaceProductModel;

/**
 * PDE-Black-Magic um alle Plugins eines Features zu ermitteln. Leider werden dazu interne PDE APIs verwendet.
 */
@SuppressWarnings("restriction")
class PluginResolver {

  private final Set<String> pluginIds;

  private final Set<String> unknownProducts;
  private final Set<String> unknownFeatures;

  PluginResolver(final Collection<String> productIds, final Collection<String> featureIds) throws CoreException {
    this.unknownProducts = new HashSet<>();
    this.unknownFeatures = new HashSet<>();
    this.pluginIds = new HashSet<>();
    for (String productId : productIds) {
      getAllProductPlugins(productId, this.pluginIds);
    }
    for (String featureId : featureIds) {
      this.getAllFeaturePlugins(featureId, this.pluginIds);
    }
  }

  /**
   * Liefert alle Plugins, die im Workspace vorliegen.
   */
  public Set<String> getWorkspacePlugins() {
    final Set<String> result = new HashSet<String>();
    for (final String pluginId : pluginIds) {
      final ModelEntry entry = PluginRegistry.findEntry(pluginId);
      if (entry != null && entry.getWorkspaceModels().length > 0) {
        result.add(pluginId);
      }
    }
    return result;
  }

  /**
   * Liefert alle Plugins, die im Target sind aber nicht im Workspace.
   */
  public Set<String> getTargetPlugins() {
    final Set<String> result = new HashSet<String>();
    for (final String pluginId : pluginIds) {
      final ModelEntry entry = PluginRegistry.findEntry(pluginId);
      if (entry != null && entry.getExternalModels().length > 0 && entry.getWorkspaceModels().length == 0) {
        result.add(pluginId);
      }
    }
    return result;
  }

  /**
   * Liefert alle Products, die nicht im Workspace sind.
   */
  public Set<String> getUnknownProducts() {
    return unknownProducts;
  }

  /**
   * Liefert alle Features, die weder im Workspace noch im Target sind.
   */
  public Set<String> getUnknownFeatures() {
    return unknownFeatures;
  }

  /**
   * Liefert alle Plugins, die weder im Workspace noch im Target sind.
   */
  public Set<String> getUnknownPlugins() {
    final Set<String> result = new HashSet<String>();
    for (final String pluginId : pluginIds) {
      final ModelEntry entry = PluginRegistry.findEntry(pluginId);
      if (entry == null || (entry.getExternalModels().length == 0 && entry.getWorkspaceModels().length == 0)) {
        result.add(pluginId);
      }
    }
    return result;
  }

  // === Plugins aus Features ermitteln ===

  private void getAllFeaturePlugins(final String featureId, final Set<String> result) {
    final IFeatureModel feature = PDECore.getDefault().getFeatureModelManager().findFeatureModel(featureId);
    if (feature == null) {
      unknownFeatures.add(featureId);
    }
    else {
      getAllFeaturePlugins(feature.getFeature(), result);
    }
  }

  private void getAllFeaturePlugins(final IFeature feature, final Set<String> result) {
    for (IFeaturePlugin plugin : feature.getFeature().getPlugins()) {
      if (checkEnvironment(plugin)) {
        result.add(plugin.getId());
      }
    }
    for (IFeatureChild child : feature.getIncludedFeatures()) {
      getAllFeaturePlugins(child.getFeature(), result);
    }
  }

  private boolean checkEnvironment(final IFeaturePlugin plugin) {
    return nullOrEquals(plugin.getOS(), TargetPlatform.getOS()) && //
        nullOrEquals(plugin.getArch(), TargetPlatform.getOSArch()) && //
        nullOrEquals(plugin.getWS(), TargetPlatform.getWS());
  }

  private boolean nullOrEquals(final String value, final String reference) {
    return value == null || value.equals(reference);
  }

  // === Plugins aus Products ermitteln ===

  private void getAllProductPlugins(final String productId, final Set<String> result) throws CoreException {
    final IProduct product = findProduct(productId);
    if (product == null) {
      unknownProducts.add(productId);
    }
    else {
      getAllProductPlugins(product, result);
    }
  }

  private void getAllProductPlugins(final IProduct product, final Set<String> result) {
    for (IProductFeature feature : product.getFeatures()) {
      getAllFeaturePlugins(feature.getId(), result);
    }
  }

  /**
   * Produkte werden per Namenskonvention im Workspace gesucht.
   */
  private IProduct findProduct(final String id) throws CoreException {
    IProject productProject = ResourcesPlugin.getWorkspace().getRoot().getProject(id);
    if (!productProject.exists()) {
      return null;
    }
    IFile productFile = productProject.getFile(id + ".product");
    if (!productFile.exists()) {
      return null;
    }
    WorkspaceProductModel product = new WorkspaceProductModel(productFile, false);
    product.load();
    return product.getProduct();
  }

}
