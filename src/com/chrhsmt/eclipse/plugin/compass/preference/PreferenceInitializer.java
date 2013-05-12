package com.chrhsmt.eclipse.plugin.compass.preference;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.chrhsmt.eclipse.plugin.compass.Activator;

/**
 * Preference Initializer.
 * @author chr
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private static final String EMPTY = "";

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {

		Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "initialize!!!"));
		CompassPreferenceStore.setRubyPath(EMPTY);
		CompassPreferenceStore.setCompassPath("compass");
		CompassPreferenceStore.setGemBinPath(EMPTY);
		CompassPreferenceStore.setOtherPath("");
	}
}
