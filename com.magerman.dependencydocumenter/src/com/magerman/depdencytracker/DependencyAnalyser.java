package com.magerman.depdencytracker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lotus.domino.Database;
import lotus.domino.DxlExporter;
import lotus.domino.DxlImporter;
import lotus.domino.NoteCollection;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.Stream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Analyses the dependencies between Script Libraries in a Notes Database.
 * 
 * @author Andrew Magerman
 * 
 */
public class DependencyAnalyser {
	/**
	 * Encoding for the DOMParser.
	 */
	private static final String ENCODING = "UTF-8";
	/**
	 * The database we want to analyse.
	 */
	private final Database db;

	/** The designelements. */
	private final ListDesignElements designelements = new ListDesignElements();

	private File tempPictureGif = null;

	/**
	 * The path to the Graphviz executable.
	 */

	private String pathToDotExe = "";

	public String getPathToDotExe() {
		return pathToDotExe;
	}

	public void setPathToDotExe(String pathToDotExe) {
		this.pathToDotExe = pathToDotExe;
	}

	/**
	 * Constructor.
	 * 
	 * @param inputdb
	 *            the input database
	 */
	public DependencyAnalyser(final Database inputdb) {
		this.db = inputdb;
	}

	/**
	 * Convert gif to base t64.
	 * 
	 * @param filePicture
	 *            the file picture
	 * @return the string
	 */
	public final String convertGifToBaseT64(final File filePicture) {
		// TODO Auto-generated method stub
		String output = "";
		Base64 b = new Base64();
		try {
			b.encode(FileUtils.readFileToByteArray(filePicture));
			output = new String(b.encode(FileUtils
					.readFileToByteArray(filePicture)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * Creates the documentation page.
	 * 
	 * @param inputDXLFile
	 *            the input dxl file
	 */
	public final void createDocumentationPageFromDXLFile(final File inputDXLFile) {

		DxlImporter importer = null;

		try {
			Session session = NotesFactory.createSession();

			// Get DXL file
			Stream stream = session.createStream();
			if (stream.open(inputDXLFile.getPath(), "ISO-8859-1")
					&& (stream.getBytes() > 0)) {
				// Create new database - replace if it already exists

				// Import DXL from file to new database
				importer = session.createDxlImporter();
				importer.setDesignImportOption(DxlImporter.DXLIMPORTOPTION_REPLACE_ELSE_CREATE);
				importer.setReplaceDbProperties(false);
				importer.setInputValidationOption(DxlImporter.DXLVALIDATIONOPTION_VALIDATE_NEVER);
				importer.setExitOnFirstFatalError(true);
				importer.setAclImportOption(DxlImporter.DXLIMPORTOPTION_REPLACE_ELSE_IGNORE);

				importer.setReplicaRequiredForReplaceOrUpdate(false);
				// this
				// means
				// that
				// the
				// replicaID
				// of the
				// target
				// database
				// must
				// match
				// that of
				// the dxl
				// file
				importer.importDxl(stream, db);

				stream.close();
			} else {
				System.out.println(inputDXLFile.getPath()
						+ " does not exist or is empty");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			// Print importer log
			try {
				System.out.println(importer.getLog());
				importer.recycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public final void createDocumentationPageFromStringBuilder(
			final StringBuilder sbinput) {
		DxlImporter importer = null;

		try {
			Session session = NotesFactory.createSession();

			// Import DXL from file to new database
			importer = session.createDxlImporter();
			importer.setDesignImportOption(DxlImporter.DXLIMPORTOPTION_REPLACE_ELSE_CREATE);
			importer.setReplaceDbProperties(false);
			importer.setInputValidationOption(DxlImporter.DXLVALIDATIONOPTION_VALIDATE_NEVER);
			importer.setExitOnFirstFatalError(true);
			importer.setAclImportOption(DxlImporter.DXLIMPORTOPTION_REPLACE_ELSE_IGNORE);

			importer.setReplicaRequiredForReplaceOrUpdate(false); // this
			// means
			// that
			// the
			// replicaID
			// of the
			// target
			// database
			// must
			// match
			// that of
			// the dxl
			// file
			importer.importDxl(sbinput.toString(), db);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				importer.recycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public StringBuilder createDXLStringBuilderFromImage(final File image) {

		BufferedImage bimg;
		StringBuilder sb = new StringBuilder();
		try {
			bimg = ImageIO.read(image);
			int width = bimg.getWidth();
			int height = bimg.getHeight();

			sb.append("<?xml version='1.0' encoding='utf-8'?>");
			sb.append("<!DOCTYPE page SYSTEM 'xmlschemas/domino_8_5_3.dtd'>");
			sb.append("<page name='ZZ DEVELOPER DOCUMENTATION - Script Library Dependency Tree' xmlns='http://www.lotus.com/dxl'");
			sb.append(" version='8.5' maintenanceversion='3.0' replicaid='C1257C05002A444C' publicaccess='false'");
			sb.append(" designerversion='8.5.3' renderpassthrough='true'>");
			sb.append("<noteinfo noteid='662' unid='B8A2B4B364AD28A0C1257C970073CA24' sequence='0'>");
			sb.append("<created><datetime>20140310T220443,88+01</datetime></created>");
			sb.append("<modified><datetime>20140310T220443,93+01</datetime></modified>");
			sb.append("<revised><datetime>20140310T220444,03+01</datetime></revised>");
			sb.append("<lastaccessed><datetime>20140310T220443,92+01</datetime></lastaccessed>");
			sb.append("<addedtofile><datetime>20140310T220443,92+01</datetime></addedtofile></noteinfo>");
			sb.append("<updatedby><name>CN=Andrew Magerman/OU=Magerman/O=NotesNet</name></updatedby>");
			sb.append("<wassignedby><name>CN=Andrew Magerman/OU=Magerman/O=NotesNet</name></wassignedby>");
			sb.append("<body><richtext>");
			sb.append("<pardef id='1'/>");
			// sb.append("<par def='1'/>");
			// sb.append("<par def='1'>Script Library Dependencies</par>");
			// sb.append("<par def='1'/>");

			sb.append("<par def='1'><picture width='" + width + "px' height='"
					+ height + "px'><gif>");

			sb.append(this.convertGifToBaseT64(image));

			sb.append("</gif></picture></par></richtext></body>");
			sb.append("<item name='$$ScriptName' summary='false' sign='true'><text>ZZ DEVELOPER DOCUMENTATION - Script Library Dependency Tree</text></item>");
			// sb.append("<item name='$REF' summary='true'>");
			// sb.append("<rawitemdata type='4'>");
			// sb.append("AQDQhIWlDsZk1cBWNgAvfCXB");
			// sb.append("</rawitemdata></item>");
			sb.append("</page>");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * From the list of design elements, generate a graphviz-compatible list.
	 * 
	 * @return true if successful
	 */
	public final boolean generateGraphvizlistAndConvertToGifFile() {

		GraphViz gv = new GraphViz();

		if (pathToDotExe.equals("")) {
			return false;
		}

		gv.setPathToDotExe(pathToDotExe);
		gv.addln(gv.start_graph());

		designelements.createTree();
		designelements.removeOrphans();

		/**
		 * The final String which one would want to paste into a Graphviz
		 * client.
		 */
		StringBuffer sbListOfConnectionsAtoB = new StringBuffer();

		for (Relationship r : designelements.getRelationships()) {
			sbListOfConnectionsAtoB.append(r.getDotLine());
		}

		// I want to specify the shape of all the design elements; for this they
		// must be listed separately
		// e.g. node [shape=box,fontname=Verdana];"Class: Batch Name Changes";
		gv.add("node [shape=box,fontname=Verdana];");

		gv.addln(designelements.getDotListOfNodes().toString());
		gv.addln(designelements.getClusteredSubgraphs());

		// The following contains a list like this:
		// ".magerman.gec.utilities"->"OpenLogFunctions";
		// ".magerman.gec.utilities"->"Class: Profile Documents";

		gv.addln(designelements.getListOfUnclusteredRelationships());
		gv.addln("overlap=false;");
		gv.addln("fontname=Verdana;");
		gv.addln("fontsize=12;");
		try {
			gv.addln("label=\""
					+ db.getTitle()
					+ " Script Library Dependencies\n laid out by Graphviz and openntf DDE Plug-in Dependency Documenter\"");
		} catch (NotesException e1) {
			e1.printStackTrace();
		}

		gv.addln(gv.end_graph());

		String dotSource = gv.getDotSource();
		byte[] imageasBytes = gv.getGraph(dotSource, "gif");

		gv.writeGraphToFile(imageasBytes, getFilePicture());
		return true;

	}

	/**
	 * Gets the file picture.
	 * 
	 * @return the file picture
	 */
	public final File getFilePicture() {
		if (tempPictureGif == null) {
			try {
				tempPictureGif = File.createTempFile("outputgif", ".gif");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tempPictureGif;

	}

	/**
	 * Prints the all design elements.
	 */
	public final void printAllDesignElements() {
		for (DesignElement d : designelements.getElements()) {
			System.out.println(d);
		}
	}

	/**
	 * The entry point for the processing.
	 */
	public final void run() {
		// Exporting DXL for databases with Java web services
		// Today I needed to export a database application containing a web
		// service written in Java as DXL. How stupid of me to think that I
		// could do this with only Notes.jar on my build path... Doing such an
		// export will make your code throw java.lang.ClassNotFoundExceptions
		// unless you have the following libraries on your build path:
		//
		// jvm/lib/ext/notes.jar (not much surprise there)
		// jvm/lib/ext/websvc.jar (due to missing
		// lotus.domino.websvc.client.Service)

		Session session;
		try {
			session = NotesFactory.createSession();

			// Create note collection
			NoteCollection nc = db.createNoteCollection(false);
			nc.selectAllCodeElements(false);
			nc.setSelectScriptLibraries(true);
			nc.buildCollection();

			// Export note collection as DXL
			DxlExporter exporter = session.createDxlExporter();
			exporter.setOutputDOCTYPE(false);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			try {
				org.w3c.dom.Document domDoc = factory.newDocumentBuilder()
						.parse(new ByteArrayInputStream(exporter.exportDxl(nc)
								.getBytes(ENCODING)));
				Element rootElement = domDoc.getDocumentElement();
				scanDOMNodes(rootElement.getChildNodes());
				if (generateGraphvizlistAndConvertToGifFile()) {
					StringBuilder hi = this
							.createDXLStringBuilderFromImage(this
									.getFilePicture());
					this.createDocumentationPageFromStringBuilder(hi);

					this.getFilePicture().delete();

				}

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}

		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	/**
	 * I am scanning through the nodeList and when I find a scriptlibrary I
	 * process it with the DesignElement object.
	 * 
	 * @param nodeList
	 *            all the nodes of the DOM generated by the DXL Export
	 */
	final void scanDOMNodes(final NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeName().equals("scriptlibrary")) {
				DesignElement d = new DesignElement();
				d.loadFromNode(childNode);
				designelements.addDesignElement(d);
			}

			NodeList children = childNode.getChildNodes();
			if (children != null) {
				scanDOMNodes(children);
			}
		}
	}

}
