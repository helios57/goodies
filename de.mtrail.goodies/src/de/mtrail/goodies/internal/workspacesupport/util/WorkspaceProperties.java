package de.mtrail.goodies.internal.workspacesupport.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Properties wrapper which sorts the keys before writing the file to
 * disk.
 */
public class WorkspaceProperties {

	private final byte[] EQUALS = new String("=").getBytes();
	private final byte[] LINEFEED = new String("\n").getBytes();

	private final Map<String, String> properties = new HashMap<>();

	public void put(final String key, final String value) {
		properties.put(key, value);
	}

	public void write(final String filename) {
		try (FileOutputStream outputstream = new FileOutputStream(filename)) {

			// sort keys
			final List<String> keys = new ArrayList<>(properties.keySet());
			keys.sort(KeyComparator::compare);

			for (final String key : keys) {
				outputstream.write(key.getBytes());
				outputstream.write(EQUALS);
				final String value = properties.get(key);
				if (value != null) {
					outputstream.write(value.getBytes());
				}
				outputstream.write(LINEFEED);
			}

			outputstream.flush();
			outputstream.close();

		} catch (final IOException e) {
			ErrorHandler.handle(e);
		}
	}

	final private static class KeyComparator {

		static int compare(final String key1, final String key2) {
			return correctKey(key1).compareTo(correctKey(key2));
		}

		private static String correctKey(final String key1) {
			return key1.substring(0, key1.lastIndexOf('.'));
		}
	}
}