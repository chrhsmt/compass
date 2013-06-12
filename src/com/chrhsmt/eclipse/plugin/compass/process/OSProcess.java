package com.chrhsmt.eclipse.plugin.compass.process;

import java.lang.reflect.Field;

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
			try {
				Class<?> clazz = Class.forName("java.lang.UNIXProcess");
				Field pidFiled = clazz.getDeclaredField("pid");
				pidFiled.setAccessible(true);
				Integer pid = (Integer) pidFiled.get(process);
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public abstract void killRecursively();

}
