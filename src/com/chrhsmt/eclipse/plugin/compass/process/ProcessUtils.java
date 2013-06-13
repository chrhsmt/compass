package com.chrhsmt.eclipse.plugin.compass.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.environment.Constants;

import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;
import com.chrhsmt.eclipse.plugin.compass.preference.CompassPreferenceStore;

/**
 * Process utility.
 * @author chr
 *
 */
public class ProcessUtils {

	/**
	 * Get path variable.
	 * @return
	 */
	public static String getPathVariable() {
		if (isWindows()) {
			return "%PATH%";
		} else {
			return "$PATH";
		}
	}

	/**
	 * Get path separator.
	 * @return
	 */
	public static String getPathSeparator() {
		if (isWindows()) {
			return ";";
		} else {
			return ":";
		}
	}

	/**
	 * Get execute command.
	 * @return
	 */
	public static String getExecuteCommand() {
		if (isWindows()) {
			return getFullPath("cmd", null);
		} else {
			return getFullPath("sh", null);
		}
	}

	/**
	 * Get command arguments.
	 * @param targetProject
	 * @param pathes
	 * @return
	 */
	public static String getArguments(IProject targetProject) {
		StringBuilder sb = new StringBuilder()
		.append(getExecuteCommandOption())
		.append(" \"")
		.append(buildExecuteCommandPhrase(targetProject))
		.append("\"");
		return sb.toString();
	}

	/**
	 * Get execute command option.
	 * @return
	 */
	private static String getExecuteCommandOption() {
		if (isWindows()) {
			return "/c";
		} else {
			return "-c";
		}
	}

	/**
	 * Get Enviroment setting command.
	 * @return
	 */
	private static String getEnvCommand() {
		if (ProcessUtils.isWindows()) {
			return getFullPath("set", null);
		} else {
			return "export";
		}
	}

	/**
	 * buildExecuteCommandPhrase.
	 * @param project
	 * @return
	 */
	private static String buildExecuteCommandPhrase(IProject project) {
		String command = CompassPreferenceStore.getCompassCommand();
		if (command == null || command.length() <= 0) {
			command = "compass";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(command)
		  .append(" watch ")
		  .append(project.getLocation().toOSString());
		return sb.toString();
	}

	/**
	 * Get full path of such command.
	 * @param command
	 * @param env
	 * @return
	 */
	public static String getFullPath(String command, Map<String, String> env) {
		try {
			ProcessBuilder builder = new ProcessBuilder(getWhichCommand(command));
			if (env != null) {
				builder.environment().putAll(join(builder.environment(), env));
			}
			builder.redirectErrorStream(true);

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
	 * @throws IOException 
	 */
    private static final String[] getWhichCommand(String origin) throws IOException {
		String[] cmd = null;
		if (ProcessUtils.isWindows()) {
			cmd = new String[]{FileLocator.resolve(ProcessUtils.class.getResource("which.bat")).getPath(), origin};
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
		return Platform.getOS().equals(Constants.OS_WIN32);
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
	 * @deprecated debug only. not to use for production.
	 */
	public static void main(String[] args) {
		System.out.println(getEnvCommand());
	}
}
