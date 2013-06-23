package com.chrhsmt.eclipse.plugin.compass.process;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IProcessFactory;
import org.eclipse.debug.core.model.IProcess;

/**
 * Compass Process Factory.
 * @author c-hashimoto
 *
 */
public class ProcessFactory implements IProcessFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IProcessFactory#newProcess(org.eclipse.debug.core.ILaunch, java.lang.Process, java.lang.String, java.util.Map)
	 */
	@Override
	public IProcess newProcess(ILaunch launch, Process process, String label,
			@SuppressWarnings("rawtypes") Map attributes) {
		return new CompassRuntimeProcess(launch, process, label, attributes);
	}
}
