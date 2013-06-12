package org.eclipse.debug.core.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;

import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;
import com.chrhsmt.eclipse.plugin.compass.process.OSProcess;

/**
 * @author c-hashimoto
 *
 */
public class ProcessTreeTerminateListener implements IDebugEventSetListener {

	public void start() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			if (event.getKind() == DebugEvent.TERMINATE) {
				Object obj = event.getSource();
				PluginLogger.log(obj.getClass().toString());
//				OSProcess.get(process.getSystemProcess()).killRecursively();
				DebugPlugin.getDefault().removeDebugEventListener(this);
				break;
			}
		}
	}
}
