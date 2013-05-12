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
	public static final String PREF_KEY_GEM_BIN_PATH = "GEM_PATH";
	public static final String PREF_KEY_COMPASS_PATH = "COMPASS_PATH";
	public static final String PREF_KEY_OTHER_PATH = "OTHER_PATH";

	/**
	 * Get Path Map.
	 * @return
	 */
	public static Map<String, String> getPathMap() {
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		StringBuilder sb = new StringBuilder("$PATH");
		if (store.getString(PREF_KEY_RUBY_PATH) != null) {
			sb.append(":").append(store.getString(PREF_KEY_RUBY_PATH));
		}
		if (store.getString(PREF_KEY_GEM_BIN_PATH) != null) {
			sb.append(":").append(store.getString(PREF_KEY_GEM_BIN_PATH));
		}
		if (store.getString(PREF_KEY_OTHER_PATH) != null) {
			sb.append(":").append(store.getString(PREF_KEY_OTHER_PATH));
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("PATH", sb.toString());
		return map;
	}

	/**
	 * Get Compass Command.
	 * @return
	 */
	public static String getCompassCommand() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(PREF_KEY_COMPASS_PATH);
	}

	public static void setRubyPath(String value) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(CompassPreferenceStore.PREF_KEY_RUBY_PATH, value);
	}

	public static void setCompassPath(String value) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(CompassPreferenceStore.PREF_KEY_COMPASS_PATH, value);
	}

	public static void setGemBinPath(String value) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(CompassPreferenceStore.PREF_KEY_GEM_BIN_PATH, value);
	}

	public static void setOtherPath(String value) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(CompassPreferenceStore.PREF_KEY_OTHER_PATH, value);
	}

	public static String getRubyPath() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(CompassPreferenceStore.PREF_KEY_RUBY_PATH);
	}

	public static String getCompassPath() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(CompassPreferenceStore.PREF_KEY_COMPASS_PATH);
	}

	public static String getGemBinPath() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(CompassPreferenceStore.PREF_KEY_GEM_BIN_PATH);
	}

	public static String getOtherPath() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(CompassPreferenceStore.PREF_KEY_OTHER_PATH);
	}
}
