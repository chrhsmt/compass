package com.chrhsmt.eclipse.plugin.compass.preference;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.chrhsmt.eclipse.plugin.compass.Activator;

/**
 * Compass Preference Page.
 * @author chr
 *
 */
public class WorkbenchPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private DirectoryFieldEditor rubyPathEditor;
//	private DirectoryFieldEditor compassPathEditor;
	private DirectoryFieldEditor gemPathEditor;
	private FileFieldEditor compassFileEditor;
	private DirectoryFieldEditor otherPathEditor;

	/**
	 * 
	 */
	public WorkbenchPreferencePage() {
	}

	/**
	 * @param title
	 */
	public WorkbenchPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public WorkbenchPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridData column3 = new GridData(GridData.FILL_HORIZONTAL);
		column3.horizontalSpan = 3;
		column3.heightHint = 30;

		{
			rubyPathEditor = new DirectoryFieldEditor("rubyPath", "RUBY PATH (optional)", composite);
			String value = getValue(CompassPreferenceStore.PREF_KEY_RUBY_PATH);
			if (value != null) {
				rubyPathEditor.setStringValue(value);
			}
			Label label = new Label(composite, SWT.LEFT);
			label.setText("set path to ruby (ex: /usr/bin/)");
			label.setLayoutData(column3);
		}
		{
//			compassPathEditor = new DirectoryFieldEditor("compassyPath", "COMPASS PATH", composite);
			compassFileEditor = new FileFieldEditor("compassPath", "COMPASS PATH", composite);
			String value = getValue(CompassPreferenceStore.PREF_KEY_COMPASS_PATH);
			if (value != null) {
//				compassPathEditor.setStringValue(value);
				compassFileEditor.setStringValue(value);
			}
			Label label = new Label(composite, SWT.LEFT);
			label.setText("set path to compass executable file (ex: /usr/bin/compass)");
			label.setLayoutData(column3);
		}
		{
			gemPathEditor = new DirectoryFieldEditor("gemPath", "GEM PATH (optional)", composite);
			String value = getValue(CompassPreferenceStore.PREF_KEY_GEM_PATH);
			if (value != null) {
				gemPathEditor.setStringValue(value);
			}
			Label label = new Label(composite, SWT.LEFT);
			label.setText("set path to gem home (ex: ~/.gem/ruby/version/)");
			label.setLayoutData(column3);
		}
		{
			otherPathEditor = new DirectoryFieldEditor("otherPath", "OTHER PATH (optional)", composite);
			String value = getValue(CompassPreferenceStore.PREF_KEY_OTHER_PATH);
			if (value != null) {
				otherPathEditor.setStringValue(value);
			}
			Label label = new Label(composite, SWT.LEFT);
			label.setText("set any other necessary path (ex: ~/.gem/ruby/version/bin:~/any/other/path)");
			label.setLayoutData(column3);
		}
		return composite;
	}

	/**
	 * get preference value.
	 * @param name
	 * @return
	 */
	private String getValue(String name) {
		return this.getPreferenceStore().getString(name);
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = this.getPreferenceStore();
		store.setValue(CompassPreferenceStore.PREF_KEY_RUBY_PATH, this.rubyPathEditor.getStringValue());
//		store.setValue(Compass.PREF_KEY_COMPASS_PATH, this.compassPathEditor.getStringValue());
		store.setValue(CompassPreferenceStore.PREF_KEY_COMPASS_PATH, this.compassFileEditor.getStringValue());
		store.setValue(CompassPreferenceStore.PREF_KEY_GEM_PATH, this.gemPathEditor.getStringValue());
		store.setValue(CompassPreferenceStore.PREF_KEY_OTHER_PATH, this.otherPathEditor.getStringValue());
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() {
		IPreferenceStore store = this.getPreferenceStore();
		this.rubyPathEditor.setStringValue(store.getDefaultString(CompassPreferenceStore.PREF_KEY_RUBY_PATH));
		this.compassFileEditor.setStringValue(store.getDefaultString(CompassPreferenceStore.PREF_KEY_COMPASS_PATH));
		this.gemPathEditor.setStringValue(store.getDefaultString(CompassPreferenceStore.PREF_KEY_GEM_PATH));
		this.otherPathEditor.setStringValue(store.getDefaultString(CompassPreferenceStore.PREF_KEY_OTHER_PATH));
	}
}
