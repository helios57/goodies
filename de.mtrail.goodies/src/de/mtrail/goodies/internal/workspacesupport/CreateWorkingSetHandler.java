package de.mtrail.goodies.internal.workspacesupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetModel;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.mtrail.goodies.internal.workspacesupport.model.FeaturePackage;
import de.mtrail.goodies.internal.workspacesupport.util.BundleReader;

/**
 * This handler creates custom WorkingSets from feature.xml descriptions.
 */
@SuppressWarnings("restriction")
public class CreateWorkingSetHandler extends AbstractHandler {

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    ISelection selection = HandlerUtil.getCurrentSelection(event);
    if (selection == null || !(selection instanceof IStructuredSelection)) {
      return null;
    }

    final Object o = ((IStructuredSelection) selection).getFirstElement();
    if (o instanceof IProject) {
      final IProject project = (IProject) o;
      IFile featureXML = project.getFile("feature.xml");
      final Display currentDisplay = Display.getCurrent();

      currentDisplay.asyncExec(new Runnable() {

        @Override
        public void run() {

          try {
            createWorkingSet(featureXML);
          }
          catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
    return null;
  }

  private void createWorkingSet(final IFile featureXML) throws IOException {

    List<Status> childStatuses = new ArrayList<>();

    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    Map<String, IProject> projectIdx = Arrays.<IProject> asList(projects).stream().collect(Collectors.toMap(p -> p.getName(), p -> p));

    FeaturePackage featurePackage = BundleReader.readBundles(featureXML);
    List<IProject> featureElements = new ArrayList<>();
    for (String bundle : featurePackage.getBundleNames()) {
      IProject p = projectIdx.get(bundle);
      if (p == null) {
        Status s = new Status(IStatus.ERROR, "de.mtrail.goodies", "Bundle nicht im Workspace: " + bundle);
        childStatuses.add(s);
      }
      else {
        featureElements.add(p);
      }
    }

    createWorkingSet(featureElements, featurePackage.getFeatureName());

    if (childStatuses.isEmpty() == false) {
      MultiStatus ms = new MultiStatus("de.mtrail.goodies", IStatus.ERROR, //
          childStatuses.toArray(new Status[] {}), "Some bundles could not be found", null);
      ErrorDialog.openError(Display.getDefault().getActiveShell(), "Summary", "These bundles could not be found", ms);
    }
  }

  @Override
  public boolean isEnabled() {
    Object o = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ISelectionService.class);
    ISelectionService selectionService = (ISelectionService) o;
    ISelection selection = selectionService.getSelection();
    if (selection instanceof IStructuredSelection) {
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      return isFeature(structuredSelection);
    }
    return false;
  }

  private boolean isFeature(final IStructuredSelection structuredSelection) {
    if (structuredSelection == null || structuredSelection.isEmpty()) {
      return false;
    }
    final Object o = structuredSelection.getFirstElement();
    if (o instanceof IProject) {
      final IProject project = (IProject) o;
      return project.getFile("feature.xml") != null;
    }
    return false;
  }

  private void createWorkingSet(final List<IProject> elements, final String featureName) {
    final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();

    IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView("org.eclipse.jdt.ui.PackageExplorer");

    PackageExplorerPart packageExplorer = (PackageExplorerPart) viewPart;
    WorkingSetModel peWSModel = packageExplorer.getWorkingSetModel();

    Map<String, IWorkingSet> peWSMap = Arrays.asList(peWSModel.getActiveWorkingSets()).stream()
        .collect(Collectors.toMap(IWorkingSet::getName, w -> w));

    IWorkingSet featureWS = peWSMap.get(featureName);
    if (featureWS == null) {
      featureWS = workingSetManager.createWorkingSet(featureName, new IAdaptable[0]);
      featureWS.setId(IWorkingSetIDs.JAVA);
      workingSetManager.addWorkingSet(featureWS);
      peWSModel.addWorkingSets(new Object[] { featureWS });
    }
    featureWS.setElements(featureWS.adaptElements(elements.toArray(new IAdaptable[0])));
  }
}