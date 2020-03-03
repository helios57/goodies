/*
 * Project: RCS - Rail Control System
 *
 * © Copyright by SBB AG, Alle Rechte vorbehalten
 */
package de.mtrail.goodies.internal.launch;

import java.util.HashMap;
import java.util.Map;

/**
 * Predefined argument values that can be used in the script
 */
class RcsServerArgumentPresets {

	private static Map<String, String> PRESETS = new HashMap<String, String>();

	static {
		PRESETS.put("$PRESET_GC_DEFAULT", ""); //$NON-NLS-1$ //$NON-NLS-2$
		PRESETS.put("$PRESET_GC_THROUGHPUT", "-XX:+UseParallelOldGC"); //$NON-NLS-1$ //$NON-NLS-2$
		PRESETS.put("$PRESET_GC_CACHE", "-XX:+UseParallelOldGC"); //$NON-NLS-1$ //$NON-NLS-2$
		PRESETS.put("$PRESET_GC_LOGGING", //
				"-Xloggc:${workspace_loc}/../rcs-server-runtime/log/$RCS_PROCESS_NAME-gc.log " //
						+ "-XX:+PrintGCDetails -XX:+PrintGCDateStamps "//
						+ "-XX:+UseGCLogFileRotation "//
						+ "-XX:NumberOfGCLogFiles=2 "//
						+ "-XX:GCLogFileSize=1000M "//
						+ "-XX:+PrintTenuringDistribution "//
						+ "-XX:+PrintGCApplicationStoppedTime "//
						+ "-XX:+PrintGCApplicationConcurrentTime");
	}

	public static String resolve(String value) {
		for (Map.Entry<String, String> preset : PRESETS.entrySet()) {
			value = value.replace(preset.getKey(), preset.getValue());
		}
		return value;
	}

}
