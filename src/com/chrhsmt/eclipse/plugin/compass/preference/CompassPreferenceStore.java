package com.chrhsmt.eclipse.plugin.compass.preference;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import com.chrhsmt.eclipse.plugin.compass.Activator;

/**
 * Compass PreferenceStore.
 * @author chr
 *
 */
public class CompassPreferenceStore {

	public static final String PREF_KEY_RUBY_PATH = "RUBY_PATH";
	public static final String PREF_KEY_GEM_PATH = "GEM_PATH";
	public static final String PREF_KEY_COMPASS_PATH = "COMPASS_PATH";
	public static final String PREF_KEY_OTHER_PATH = "OTHER_PATH";

	public static Map<String, String> getPathMap() {
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		StringBuilder sb = new StringBuilder("$PATH");
		if (store.getString(PREF_KEY_RUBY_PATH) != null) {
			sb.append(":").append(store.getString(PREF_KEY_RUBY_PATH));
		}
		if (store.getString(PREF_KEY_GEM_PATH) != null) {
			sb.append(":").append(store.getString(PREF_KEY_GEM_PATH));
		}
		if (store.getString(PREF_KEY_OTHER_PATH) != null) {
			sb.append(":").append(store.getString(PREF_KEY_OTHER_PATH));
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("PATH", sb.toString());
		return map;
	}
}
