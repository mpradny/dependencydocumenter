package test.com.magerman.depdencytracker;

import java.io.File;

import junit.framework.TestCase;
import lotus.domino.AgentContext;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import com.magerman.depdencytracker.DependencyAnalyser;

public class TestOnceDatabaseIsSelected extends TestCase {
	Database db;
	Session session;

	protected void setUp() {

		NotesThread.sinitThread();
		AgentContext ctx = null;

		try {
			session = NotesFactory.createSession();
			db = session
					.getDatabase("albis",
							"Development\\DependencyTrackerr\\Efsr_(3_0)_Field_Service_Local_Dev.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}

	}

	protected void tearDown() {
		NotesThread.stermThread();
	}

	public void testONceDbisselected() {

		DependencyAnalyser da = new DependencyAnalyser(db);
		String pathToDotExe;
		try {
			pathToDotExe = session.getEnvironmentString("pathtographvizdotexe");
			if (pathToDotExe.equals("")) {
				System.out.println("no path to dotexe");
			} else {
				File f = new File(pathToDotExe);
				if (f.exists() && !f.isDirectory()) {
					da.setPathToDotExe(pathToDotExe);
					da.run();
				}
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
