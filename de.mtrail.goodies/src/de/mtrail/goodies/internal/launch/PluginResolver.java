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
 * PDE-Black-Magic to retrieve plugins from a feature.
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
		for (final String productId : productIds) {
			getAllProductPlugins(productId, this.pluginIds);
		}
		for (final String featureId : featureIds) {
			this.getAllFeaturePlugins(featureId, this.pluginIds);
		}
	}

	/**
	 * Returns the ids of all plugins present in the workspace.
	 */
	public Set<String> getWorkspacePlugins() {
		final Set<String> result = new HashSet<>();
		for (final String pluginId : pluginIds) {
			final ModelEntry entry = PluginRegistry.findEntry(pluginId);
			if (entry != null && entry.getWorkspaceModels().length > 0) {
				result.add(pluginId);
			}
		}
		return result;
	}

	/**
	 * Returns the ids of all plugins present in the Target Platform, but not in the workspace.
	 */
	public Set<String> getTargetPlugins() {
		final Set<String> result = new HashSet<>();
		for (final String pluginId : pluginIds) {
			final ModelEntry entry = PluginRegistry.findEntry(pluginId);
			if (entry != null && entry.getExternalModels().length > 0 && entry.getWorkspaceModels().length == 0) {
				result.add(pluginId);
			}
		}
		return result;
	}

	/**
	 * Returns all products that are not in the workspace.
	 */
	public Set<String> getUnknownProducts() {
		return unknownProducts;
	}

	/**
	 * Returns all features that are neither in the workspace, nor in the Target Platform.
	 */
	public Set<String> getUnknownFeatures() {
		return unknownFeatures;
	}

	/**
	 * Returns all plugin ids that are neither in the workspace, nor in the Target Platform.
	 */
	public Set<String> getUnknownPlugins() {
		final Set<String> result = new HashSet<>();
		for (final String pluginId : pluginIds) {
			final ModelEntry entry = PluginRegistry.findEntry(pluginId);
			if (entry == null || (entry.getExternalModels().length == 0 && entry.getWorkspaceModels().length == 0)) {
				result.add(pluginId);
			}
		}
		return result;
	}

	// === Extract plugins from features ===

	private void getAllFeaturePlugins(final String featureId, final Set<String> result) {
		final IFeatureModel feature = PDECore.getDefault().getFeatureModelManager().findFeatureModel(featureId);
		if (feature == null) {
			unknownFeatures.add(featureId);
		} else {
			getAllFeaturePlugins(feature.getFeature(), result);
		}
	}

	private void getAllFeaturePlugins(final IFeature feature, final Set<String> result) {
		for (final IFeaturePlugin plugin : feature.getFeature().getPlugins()) {
			if (checkEnvironment(plugin)) {
				result.add(plugin.getId());
			}
		}
		for (final IFeatureChild child : feature.getIncludedFeatures()) {
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

	// === Extract plugins from products ===

	private void getAllProductPlugins(final String productId, final Set<String> result) throws CoreException {
		final IProduct product = findProduct(productId);
		if (product == null) {
			unknownProducts.add(productId);
		} else {
			getAllProductPlugins(product, result);
		}
	}

	private void getAllProductPlugins(final IProduct product, final Set<String> result) {
		for (final IProductFeature feature : product.getFeatures()) {
			getAllFeaturePlugins(feature.getId(), result);
		}
	}

	/**
	 * Search the workspace for products using the naming convention.
	 */
	private IProduct findProduct(final String id) throws CoreException {
		final IProject productProject = ResourcesPlugin.getWorkspace().getRoot().getProject(id);
		if (!productProject.exists()) {
			return null;
		}
		final IFile productFile = productProject.getFile(id + ".product");
		if (!productFile.exists()) {
			return null;
		}
		final WorkspaceProductModel product = new WorkspaceProductModel(productFile, false);
		product.load();
		return product.getProduct();
	}

}
