package com.chrhsmt.eclipse.plugin.compass.process;

import org.jvnet.winp.WinProcess;

/**
 * OS Process class.
 * @author c-hashimoto
 *
 */
public abstract class OSProcess {

	private int pid;

	public OSProcess(int pid) {
		this.pid = pid;
	}

	public static OSProcess get(Process process) {

		if (ProcessUtils.isWindows()) {
			return new OSProcess(new WinProcess(process).getPid()) {
				@Override
				public void killRecursively() {
					new WinProcess(super.pid).killRecursively();
				}
			};
		} else {
			return null;
		}
	}

	public abstract void killRecursively();

}
