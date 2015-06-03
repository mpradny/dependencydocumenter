package com.magerman.depdencytracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * The Class ListDesignElements.
 */
public class ListDesignElements {

    /** The all design elements. */
    private HashMap<String, DesignElement> allDesignElements = new HashMap<String, DesignElement>();

    /**
     * Adds the design element.
     * 
     * @param d
     *            the d
     */
    public final void addDesignElement(final DesignElement d) {
	allDesignElements.put(d.getName(), d);
    }

    /** The relationships. */
    private ArrayList<Relationship> relationships = new ArrayList<Relationship>();

    /**
     * Gets the relationships.
     * 
     * @return the relationships
     */
    public final ArrayList<Relationship> getRelationships() {
	return relationships;
    }

    /**
     * Creates the tree.
     */
    public final void createTree() {
	for (DesignElement d : allDesignElements.values()) {
	    if (!d.getParentReferences().isEmpty()) {
		for (String parentReference : d.getParentReferences()) {
		    DesignElement potentialParent;
		    potentialParent = allDesignElements.get(parentReference);
		    if (potentialParent != null) {
			d.parents.add(potentialParent);
			potentialParent.setLinkedToOthers(true);
			d.setLinkedToOthers(true);
			relationships.add(new Relationship(potentialParent, d));
		    }
		}
	    }
	}
    }

    /**
     * Removes the orphans.
     */
    public final void removeOrphans() {
	Iterator<Entry<String, DesignElement>> it = allDesignElements
		.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, DesignElement> designElementToPotentiallyRemove = it
		    .next();
	    if (!((DesignElement) designElementToPotentiallyRemove.getValue())
		    .isLinkedToOthers()) {
		it.remove();
	    }
	}
    }

    /**
     * Gets the elements.
     * 
     * @return the elements
     */
    public final Collection<DesignElement> getElements() {
	return allDesignElements.values();
    }

    /**
     * Gets the dot list of nodes.
     * 
     * @return the dot list of nodes
     */
    public final StringBuffer getDotListOfNodes() {
	StringBuffer sbUniqueDesignElementNames = new StringBuffer();
	for (DesignElement d : allDesignElements.values()) {
	    sbUniqueDesignElementNames.append(String.format("\"%s\";",
		    d.getName()));
	}
	return sbUniqueDesignElementNames;

    }

    /**
     * Gets the list of unclustered relationships.
     *
     * @return the list of unclustered relationships
     */
    public final String getListOfUnclusteredRelationships() {
	StringBuffer sbListOfConnectionsAtoB = new StringBuffer();
	for (Relationship r : this.getRelationships()) {
	    if (!r.hasCluster()) {
		sbListOfConnectionsAtoB.append(r.getDotLine());
	    }
	}
	return sbListOfConnectionsAtoB.toString();
    }

    /**
     * Gets the clustered subgraphs.
     * 
     * @return the clustered subgraphs
     */
    public final String getClusteredSubgraphs() {
	// make unique list of clusters
	StringBuffer temp = new StringBuffer();
	HashSet<String> clusternames = new HashSet<String>();
	for (Relationship r : this.getRelationships()) {
	    if (r.hasCluster()) {
		clusternames.add(r.getCluster());
	    }
	}
	int i = 0;
	
	for (String cname : clusternames) {
	    temp.append(String.format("subgraph cluster%d {", i));
	    for (Relationship r : this.getRelationships()) {
		if (r.hasCluster()) {
		    if (r.getCluster().equals(cname)) {
			temp.append(r.getDotLine());
		    }
		}
	    }
	    temp.append("label = \"inherited from external database: " + cname + "\";");
	    temp.append("fontname=Verdana;");
	    temp.append("fontsize=12;");	    	    
	    temp.append("}");
	    i++;
	}
	return temp.toString();
    }
}
