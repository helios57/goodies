package de.mtrail.goodies.internal.workspacesupport.util;

import java.util.Iterator;
import java.util.List;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IFile;

import de.mtrail.goodies.internal.workspacesupport.model.FeaturePackage;

/**
 * A BundleReader can read all the bundles from a feature.xml file.
 * <p>
 * TODO: Use Eclipse JDT parsing capabilities to do that.
 */
public final class BundleReader {

  public static FeaturePackage readBundles(final IFile featureXML) throws IOException {

    FeaturePackage result = new FeaturePackage();

    Path path = FileSystems.getDefault().getPath(featureXML.getLocation().toString());
    final List<String> allLines = Files.readAllLines(path);

    for (Iterator<String> stringIterator = allLines.iterator(); stringIterator.hasNext();) {
      String line = stringIterator.next();

      if (line.indexOf("<feature") != -1) {
        String featureNameLine = stringIterator.next();
        String featureName = featureNameLine.substring(featureNameLine.indexOf("\"") + 1, featureNameLine.lastIndexOf("\""));
        result.setFeatureName(featureName);
      }
      else if (line.indexOf("<plugin") != -1) { // next line contains bundle name
        String bundleNameLine = stringIterator.next();
        // id="de.mtrail.goodies"
        String bundleName = bundleNameLine.substring(bundleNameLine.indexOf("\"") + 1, bundleNameLine.lastIndexOf("\""));
        result.getBundleNames().add(bundleName);
      }
    }

    return result;
  }

}
