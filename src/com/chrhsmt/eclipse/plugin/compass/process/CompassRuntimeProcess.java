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
	 * get system {@link Process}.
	 */
	public Process getSystemProcess() {
		return super.getSystemProcess();
	}

	/**
	 * kill recursively and terminate processes.
	 */
	@Override
	public void terminate() throws DebugException {
		OSProcess osProcess = OSProcess.get(this.getSystemProcess());
		if (osProcess != null) {
			osProcess.killRecursively();
		}
		super.terminate();
	}
}
