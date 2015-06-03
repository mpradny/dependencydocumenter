package com.magerman.depdencytracker;

/**
 * The Class Relationship.
 */
public class Relationship {

    /** The parent. */
    private DesignElement parent;

    /** The child. */
    private DesignElement child;

    /** The cluster. */
    private String cluster = "";

    /**
     * Instantiates a new relationship.
     * 
     * @param inputparent
     *            the inputparent
     * @param inputchild
     *            the inputchild
     */
    public Relationship(final DesignElement inputparent,
	    final DesignElement inputchild) {
	this.parent = inputparent;
	this.child = inputchild;
	if (this.parent.getFromTemplate().equals(this.child.getFromTemplate())) {
	    this.setCluster(this.parent.getFromTemplate());
	}
    }

    /**
     * Gets the dot line.
     * 
     * @return the dot line
     */
    public final String getDotLine() {
	String formattingString = "\"%s\"->\"%s\";\n\r";

	return String.format(formattingString, child.getName(),
		parent.getName());
    }

    /**
     * Gets the cluster.
     * 
     * @return the cluster
     */
    public final String getCluster() {
	return cluster;
    }

    /**
     * Sets the cluster.
     * 
     * @param inputcluster
     *            the new cluster
     */
    public final void setCluster(final String inputcluster) {
	this.cluster = inputcluster;
    }

    /**
     * Checks for cluster.
     * 
     * @return true, if successful
     */
    public final boolean hasCluster() {
	return (!cluster.isEmpty());
    }
}
