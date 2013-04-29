package com.chrhsmt.eclipse.plugin.compass.preference;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.chrhsmt.eclipse.plugin.compass.Activator;
import com.chrhsmt.eclipse.plugin.compass.actions.Compass;

/**
 * Preference Initializer.
 * @author chr
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {

		Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "initialize!!!"));
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Compass.PREF_KEY_RUBY_PATH, "");
		store.setDefault(Compass.PREF_KEY_COMPASS_PATH, "compass");
		store.setDefault(Compass.PREF_KEY_GEM_PATH, "");
		store.setDefault(Compass.PREF_KEY_OTHER_PATH, "");
	}
}
