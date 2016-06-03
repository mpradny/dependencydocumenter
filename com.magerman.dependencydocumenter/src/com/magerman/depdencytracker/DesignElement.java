package com.magerman.depdencytracker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class to represent a Notes Design Element.
 * 
 * @author Andrew Magerman
 * 
 */
/**
 * @author Andrew Magerman
 * 
 */
public class DesignElement {
	
	/**
	 * Matching patterns - comments
	 */
	private static Pattern patt_comment_single=Pattern.compile("'.*");
	private static Pattern patt_comment_multi=Pattern.compile("(%REM.*?%END REM",Pattern.DOTALL+Pattern.CASE_INSENSITIVE);
	private static Pattern patt_use = Pattern.compile("Use \"(.*?)\"",Pattern.CASE_INSENSITIVE);
	
    /**
     * The name of the node, e.g. something like 'scriptlibrary'
     */
    private String nodename;

    /**
     * The given name of the design element, e.g. 'Class HelloWorld'
     */
    private String name = "";

    /**
     * The complete content of /code/event/declarations.
     */
    private String declarations = "";

    /**
     * A list of all the design elements needed as parents, i.e. the list of
     * references using 'Use'
     */
    private HashSet<String> parentReferences = new HashSet<String>();

    /** The parents. */
    public ArrayList<DesignElement> parents = new ArrayList<DesignElement>();

    /** The from template. */
    private String fromTemplate;

    /**
     * Gets the from template.
     * 
     * @return the from template
     */
    public final String getFromTemplate() {
	return fromTemplate;
    }

    /**
     * Sets the from template.
     * 
     * @param inputfromTemplate
     *            the new from template
     */
    public final void setFromTemplate(final String inputfromTemplate) {
	this.fromTemplate = inputfromTemplate;
    }

    /** The comment. */
    private String comment;

    /** The Linked to others. */
    private boolean linkedToOthers;

    /**
     * The constructor.
     */
    public DesignElement() {
    }

    /**
     * loads the interesting bits of the node into the object, then runs
     * parsedeclarations.
     * 
     * @param node
     *            the input node, for the moment only script libraries
     */
    public final void loadFromNode(final Node node) {
	this.nodename = node.getNodeName();
	NamedNodeMap attributes = node.getAttributes();
	// Node attributeName = attributes.getNamedItem("name");
	// this.name = attributeName.getTextContent();
	// Node attributefromtemplate = attributes.getNamedItem("fromtemplate");
	// if (attributefromtemplate != null){
	// this.fromTemplate = attributefromtemplate.getTextContent();
	// }

	this.fromTemplate = getAttributeTextContext(attributes, "fromtemplate");
	this.name = getAttributeTextContext(attributes, "name");
	this.comment = getAttributeTextContext(attributes, "comment");

	NodeList childNodes = node.getChildNodes();
	for (int i = 0; i < childNodes.getLength(); i++) {
	    Node childnode = childNodes.item(i);
	    if (childnode.getNodeName().equalsIgnoreCase("code")) {
		if (childnode.getAttributes().getNamedItem("event")
			.getTextContent().equalsIgnoreCase("options")) {
		    Node lsnode = childnode.getFirstChild();
		    declarations = lsnode.getTextContent();
		}
	    }
	}

	parsedeclarations();

    }

    /**
     * Gets the attribute text context.
     * 
     * @param inputNodeMap
     *            the input node map
     * @param attributeName
     *            the attribute name
     * @return the attribute text context
     */
    private String getAttributeTextContext(final NamedNodeMap inputNodeMap,
	    final String attributeName) {
	Node thisNode = inputNodeMap.getNamedItem(attributeName);
	if (thisNode != null) {
	    return thisNode.getTextContent();
	}
	return "";
    }

    /**
     * Using cunning regexes, finds out the name of the parent references.
     */
    public final void parsedeclarations() {
    //remove comments
    declarations=patt_comment_single.matcher(declarations).replaceAll("");
    declarations=patt_comment_multi.matcher(declarations).replaceAll("");
    
	Matcher m = patt_use.matcher(declarations);
	StringBuffer sb = new StringBuffer(declarations.length());
	while (m.find()) {
	    String foundtext = m.group(1);
	    // I want to avoid the case of a design element referring to itself.
	    // This happens, for instance, when there is a comment in the
	    // declarations section.
	    if (!foundtext.equalsIgnoreCase(getName())) {
		getParentReferences().add(foundtext);
	    }
	}
	m.appendTail(sb);
    }

    /**
     * @return the name of the design element
     */
    public final String getName() {
	return name;
    }

    /**
     * @return a list of parent references, i.e. the name of elements that are
     *         'Use'd
     */
    public final HashSet<String> getParentReferences() {
	return parentReferences;
    }

    @Override
    public final String toString() {
	return "DesignElement [nodename=" + nodename + ", name=" + name
		+ ", parentReferences=" + parentReferences + ", fromTemplate="
		+ fromTemplate + ", comment=" + comment + "]";
    }

    /**
     * Checks if is linked to others.
     * 
     * @return true, if is linked to others
     */
    public final boolean isLinkedToOthers() {
	return linkedToOthers;
    }

    /**
     * Sets the linked to others.
     * 
     * @param inputlinkedToOthers
     *            the new linked to others
     */
    public final void setLinkedToOthers(final boolean inputlinkedToOthers) {
	linkedToOthers = inputlinkedToOthers;
    }

}
