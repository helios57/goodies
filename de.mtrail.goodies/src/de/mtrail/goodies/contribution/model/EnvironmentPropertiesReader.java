package de.mtrail.goodies.contribution.model;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnvironmentPropertiesReader {

	  public static List<Environment> read(final Properties goodiesProperties) {
	    return IntStream.range(0, goodiesProperties.size())//
	        .mapToObj(i -> getEnvironment(i, goodiesProperties))//
	        .filter(Environment::isValid)//
	        .sorted(Environment.COMPARATOR)//
	        .collect(Collectors.toList());
	  }

	  private static String getEnvString(final int i) {
	    return "environment[" + i + "]";
	  }

	  private static String getEnviornmentName(final int i, final Properties goodiesProperties) {
	    return goodiesProperties.getProperty(getEnvString(i) + ".name");
	  }

	  private static String getEnviornmentCluster(final int i, final Properties goodiesProperties) {
	    return goodiesProperties.getProperty(getEnvString(i) + ".cluster");
	  }

	  private static Environment getEnvironment(final int i, final Properties goodiesProperties) {
	    return new Environment(getEnviornmentName(i, goodiesProperties), getEnviornmentCluster(i, goodiesProperties));
	  }

	}

