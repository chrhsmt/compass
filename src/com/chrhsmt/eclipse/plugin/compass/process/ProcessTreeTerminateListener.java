package com.chrhsmt.eclipse.plugin.compass.process;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;

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
				
				// 既にparent processはkillされている....
				CompassRuntimeProcess process = (CompassRuntimeProcess) event.getSource();
				OSProcess.get(process.getSystemProcess()).killRecursively();
				DebugPlugin.getDefault().removeDebugEventListener(this);
				break;
			}
		}
	}
}
