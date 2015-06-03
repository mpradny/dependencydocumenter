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

public class TestGifCreation extends TestCase {
    Database db;

    protected void setUp() {
	Session session;
	NotesThread.sinitThread();
	AgentContext ctx = null;

	try {
	    session = NotesFactory.createSession();
	    db = session
		    .getDatabase("albis",
			    "Development\\DependencyTrackerr\\Efsr_(3_0)_Field_Service_Local_Dev.nsf");
	} catch (NotesException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    protected void tearDown() {
	NotesThread.stermThread();
    }

    public void testGifCreation() {

	// File f = new
	// File("H:\\Current Projects\\dependencytracker\\ExportDxl.xml");
	File f = new File("H:\\Current Projects\\dependencytracker\\test1.dxl");

	DependencyAnalyser d = new DependencyAnalyser(db);
	d.createDocumentationPageFromDXLFile(f);

    }

    public void testGenerationDXLFromImage() {
	DependencyAnalyser d = new DependencyAnalyser(db);
//	StringBuilder hi = d.createDXLStringBuilderFromImage(new File("H:\\Current Projects\\dependencytracker\\gifs\\To Do - Button.gif"));
	StringBuilder hi = d.createDXLStringBuilderFromImage(new File("H:\\Current Projects\\dependencytracker\\gifs\\out.gif"));
//	StringBuilder hi = d.createDXLStringBuilderFromImage(new File("H:\\Current Projects\\dependencytracker\\gifs\\close.gif"));
	d.createDocumentationPageFromStringBuilder(hi);
    }

    public void testGifToBase64() {
	Session session;

	DependencyAnalyser d = new DependencyAnalyser(db);
	System.out.println(d.convertGifToBaseT64(d.getFilePicture()));

    }
}
