package de.mtrail.goodies.contribution.model;

import java.util.Comparator;

public final class Environment {

	  static final Comparator<Environment> COMPARATOR = Comparator//
	      .comparing(Environment::getName)//
	      .thenComparing(Environment::getCluster);

	  private final String name;
	  private final String cluster;

	  Environment(final String name, final String cluster) {
	    this.name = name;
	    this.cluster = cluster;
	  }

	  public String getName() {
	    return this.name;
	  }

	  public String getCluster() {
	    return this.cluster;
	  }

	  public boolean isValid() {
	    return name != null && cluster != null;
	  }

	}
