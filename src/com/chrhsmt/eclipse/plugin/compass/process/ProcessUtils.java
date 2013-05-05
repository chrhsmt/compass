package com.chrhsmt.eclipse.plugin.compass.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;

/**
 * Process utility.
 * @author chr
 *
 */
public class ProcessUtils {

	/**
	 * Get Enviroment setting command.
	 * @return
	 */
	public static String getEnvCommand() {
		if (ProcessUtils.isWindows()) {
			return getFullPath("set", null);
		} else {
			return getFullPath("env", null);
		}
	}

	/**
	 * Build PATH phrase of setting. 
	 * @param pathes
	 * @return
	 */
	public static String buildEnviromentPathPhrase(String...pathes) {
		StringBuilder sb = new StringBuilder("PATH=");
		for (String path : pathes) {
			if (path != null && path.length() > 0) {
				sb.append(path).append(":");
			}
		}
		sb.append("$PATH");
		return sb.toString();
	}

	/**
	 * TODO:
	 * @param projects
	 * @return
	 */
	public static String buildExecuteCommandPhrase(List<IProject> projects) {
		StringBuilder sb = new StringBuilder();
		for (IProject project : projects) {
			sb.append("compass watch ")
			  .append(project.getLocation().toOSString())
			  .append(" ");
		}
		return sb.toString();
	}

	/**
	 * Get full path of such command.
	 * @param command
	 * @param env
	 * @return
	 */
	public static String getFullPath(String command, Map<String, String> env) {
		ProcessBuilder builder = new ProcessBuilder(getWhichCommand(command));
		if (env != null) {
			builder.environment().putAll(ProcessUtils.join(builder.environment(), env));
		}
		builder.redirectErrorStream(true);

		try {
			Process process = builder.start();
			boolean ret = process.waitFor() == 0;
			InputStream in = null;
			InputStreamReader inReader = null;
			BufferedReader reader = null;
			try {
				in = process.getInputStream();
				inReader = new InputStreamReader(in);
				reader = new BufferedReader(inReader);
				return ret ? reader.readLine() : null;
			} finally {
				if (in != null) {
					in.close();
				}
				if (inReader != null) {
					inReader.close();
				}
				if (reader != null) {
					reader.close();
				}
			}
		} catch (IOException e) {
			PluginLogger.log(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			PluginLogger.log(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * build which command.
	 * @param origin
	 * @return
	 */
	private static final String[] getWhichCommand(String origin) {
		String[] cmd = null;
		if (ProcessUtils.isWindows()) {
			cmd = new String[]{ProcessUtils.class.getResource("which.bat").getPath(), origin};
		} else {
			cmd = new String[]{"which", origin};
		}
		return cmd;
	}

	/**
	 * check OS.
	 * @return
	 */
	public static final boolean isWindows() {
		String os = System.getProperty("os.name");
		return os.toLowerCase().contains("windows");
	}

	/**
	 * Two enviroment variable join each other.
	 * @param origin
	 * @param other
	 * @return
	 */
	public static Map<String, String> join(Map<String, String> origin, Map<String, String> other) {
		for (String key : other.keySet()) {
			for (String envKey : origin.keySet()) {
				if (envKey.equalsIgnoreCase(key)) {
					String tmp = origin.get(envKey);
					tmp += other.get(key);
					origin.put(envKey, tmp);
					continue;
				}
			}
			origin.put(key, other.get(key));
		}
		return origin;
	}
	
	/**
	 * for debug
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getEnvCommand());
	}
}
