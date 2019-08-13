package de.mtrail.goodies.internal.workspacesupport.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class ErrorHandler {

	public static void handle(Exception e) {
	      ResourcesPlugin.getPlugin().getLog().log(//
	              new Status(IStatus.ERROR, "de.mtrail.goodies", //
	                  "Something went wrong: " + e.getLocalizedMessage() + " " + e.getCause(), e));

	          final MessageBox dialog = new MessageBox(//
	        		  Display.getDefault().getActiveShell(), //
	        		  SWT.ICON_ERROR | SWT.OK);
	          
	          dialog.setText("Something went wrong: " + e.getLocalizedMessage() + " " + e.getCause());
	          dialog.setMessage(e.getLocalizedMessage());
	          dialog.open();
	}
}
