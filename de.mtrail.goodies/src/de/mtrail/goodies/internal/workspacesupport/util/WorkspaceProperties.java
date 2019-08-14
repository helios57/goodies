package de.mtrail.goodies.internal.workspacesupport.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Properties wrapper which sorts the keys before writing the file to disk.
 */
public class WorkspaceProperties {

	private byte[] EQUALS = new String("=").getBytes();
	private byte[] LINEFEED = new String("\n").getBytes();

	private final Map<String, String> properties = new HashMap<>();

	public void put(String key, String value) {
		properties.put(key, value);
	}

	public void write(String filename) {
		try (FileOutputStream outputstream = new FileOutputStream(filename)) {

			// sort keys
			List<String> keys = new ArrayList<>(properties.keySet());
			keys.sort((k1, k2) -> k1.compareTo(k2));

			for (String key : keys) {
				outputstream.write(key.getBytes());
				outputstream.write(EQUALS);
				String value = properties.get(key);
				if (value != null) {
					outputstream.write(value.getBytes());
				}
				outputstream.write(LINEFEED);
			}

			outputstream.flush();
			outputstream.close();
			
		} catch (IOException e) {
			ErrorHandler.handle(e);
		}
	}
}
