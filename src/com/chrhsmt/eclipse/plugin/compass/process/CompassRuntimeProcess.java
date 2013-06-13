package com.chrhsmt.eclipse.plugin.compass.process;

import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;

/**
 * CompassRuntimeProcess.
 * @author c-hashimoto
 *
 */
public class CompassRuntimeProcess extends RuntimeProcess {

	/**
	 * @param launch
	 * @param process
	 * @param name
	 * @param attributes
	 */
	public CompassRuntimeProcess(ILaunch launch, final Process process, String name,
			@SuppressWarnings("rawtypes") Map attributes) {
		super(launch, process, name, attributes);
	}

	/**
	 * 
	 */
	public Process getSystemProcess() {
		return super.getSystemProcess();
	}

	@Override
	public void terminate() throws DebugException {
		OSProcess.get(this.getSystemProcess()).killRecursively();
		super.terminate();
	}
}
