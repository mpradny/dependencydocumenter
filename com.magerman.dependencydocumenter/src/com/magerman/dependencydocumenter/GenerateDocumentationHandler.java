package com.magerman.dependencydocumenter;

import java.io.File;

import lotus.domino.Database;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ui.commons.extensions.DesignerDesignElementSelection;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.magerman.depdencytracker.DependencyAnalyser;

public class GenerateDocumentationHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ISelection selection = window.getSelectionService().getSelection();

		String serverName = null;
		String dbPath = null;

		Shell shell = window.getShell();

		DesignerDesignElementSelection ddes = DesignerResource
				.getDesignerSelection(selection);

		if (ddes.isDominoProjectSelected()) {

			try {
				DesignerProject dp = ddes.getSelectedDesignerProject();
				serverName = dp.getServerName();
				dbPath = dp.getDatabaseName();

				try {
					NotesThread.sinitThread();

					try {
						Session sess = NotesFactory.createSession();
						Database db = sess.getDatabase(serverName, dbPath);

						if (db != null) {
							DependencyAnalyser da = new DependencyAnalyser(db);
							String pathToDotExe = "";

							// Graphviz must be installed for it to work.
							// First I see if the notes.ini has been correctly
							// configured
							pathToDotExe = sess
									.getEnvironmentString("pathtographvizdotexe");
							if (pathToDotExe.equals("")) {
								MessageDialog
										.openInformation(
												shell,
												"Configuration error",
												"Please insert into notes ini the path to the Graphviz dot.exe executable\nexample:\n$pathtographvizdotexe=C:\\Program Files (x86)\\Graphviz2.36\\bin\\dot.exe");
							} else {
								File f = new File(pathToDotExe);
								if (f.exists() && !f.isDirectory()) {
									MessageDialog
											.openInformation(
													shell,
													"Start of Documentation",
													"The script Libraries of the database "
															+ db.getTitle()
															+ " will be analyzed and a new page inserted into the design with a graphical representation of the relationships.\nPlease be patient, process can last long.");
									da.setPathToDotExe(pathToDotExe);
									da.run();
									MessageDialog
											.openInformation(
													shell,
													"End of Documentation",
													"done.\nPlease Refresh your Pages.\nYou should see a new page called 'ZZ DEVELOPER DOCUMENTATION - Script Library Dependency Tree'");
									// TODO Implement lookup of pages
									// discovered.
								} else {
									MessageDialog
											.openInformation(
													shell,
													"Configuration error",
													"Could not find the file defined in the notes.ini variable $pathtographvizdotexe:\n"
															+ pathToDotExe);
								}
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception b) {
					b.printStackTrace();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;

	}
}
