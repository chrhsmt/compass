package com.chrhsmt.eclipse.plugin.compass.process;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.jvnet.winp.WinProcess;

import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;

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

				@Override
				public List<OSProcess> getChildren() {
					return null;
				}
			};
		} else {
			try {
				Class<?> clazz = Class.forName("java.lang.UNIXProcess");
				Field pidFiled = clazz.getDeclaredField("pid");
				pidFiled.setAccessible(true);
				final Integer pid = (Integer) pidFiled.get(process);
				PluginLogger.log("pid:" + pid);

				return new OSProcess(pid) {

					@Override
					public void killRecursively() {
						ProcessBuilder builder = new ProcessBuilder("kill", "-9", pid.toString());
						try {
							builder.start();
						} catch (IOException e) {
							PluginLogger.log(e.getMessage(), e);
						}
					}

					@Override
					public List<OSProcess> getChildren() {
						return null;
					}
				};
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				PluginLogger.log(e.getMessage(), e);
				return null;
			}
		}
	}

	public abstract void killRecursively();

	public abstract List<OSProcess> getChildren();
}
