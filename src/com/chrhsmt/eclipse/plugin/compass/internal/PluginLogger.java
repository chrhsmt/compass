package com.chrhsmt.eclipse.plugin.compass.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.chrhsmt.eclipse.plugin.compass.Activator;

/**
 * Plugin Internal Logger.
 * @author chr
 *
 */
public class PluginLogger {

	public static final void log(String message) {
		ILog log = Activator.getDefault().getLog();
		log.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message));
	}

	public static final void log(String message, Throwable e) {
		ILog log = Activator.getDefault().getLog();
		log.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message, e));
	}
}
