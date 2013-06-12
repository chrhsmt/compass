package com.chrhsmt.eclipse.plugin.compass.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.chrhsmt.eclipse.plugin.compass.Activator;
import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;

/**
 * @author chr
 *
 */
public class CompassMenuItem extends MenuItem {

	/**
	 * Constructor.
	 * @param parent
	 * @param name
	 */
	public CompassMenuItem(Menu parent, String name) {
		super(parent, SWT.PUSH);
		this.setText(name);
		this.setEnabled(true);
		this.setImage(Activator.getImageDescriptor("icons/compass_icon.png").createImage());
		this.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PluginLogger.log("push!!!");
				Object data = getData("processed");
				if (data == null || (boolean) data == false) {
					setData("processed", true);
				} else {
					setData("processed", false);
				}
			}
		});
	}

}
