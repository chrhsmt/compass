package com.chrhsmt.eclipse.plugin.compass.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import com.chrhsmt.eclipse.plugin.compass.Activator;
import com.chrhsmt.eclipse.plugin.compass.actions.Compass;
import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;

/**
 * Process utility.
 * @author chr
 *
 */
public class ProcessUtils {
	
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
	 * @param targetProjects
	 * @param pathes
	 * @return
	 */
	public static String getArguments(List<IProject> targetProjects, String...pathes) {
		String pathPhrase = buildEnviromentPathPhrase(pathes);
		StringBuilder sb = new StringBuilder()
		.append(getExecuteCommandOption())
		.append(" \"")
		.append(getEnvCommand())
		.append(" ")
		.append(pathPhrase)
		.append("; ")
		.append(buildExecuteCommandPhrase(targetProjects))
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
	 * Build PATH phrase of setting. 
	 * @param pathes
	 * @return
	 */
	private static String buildEnviromentPathPhrase(String...pathes) {
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
	private static String buildExecuteCommandPhrase(List<IProject> projects) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String command = store.getString(Compass.PREF_KEY_COMPASS_PATH);
		if (command == null || command.length() <= 0) {
			command = "compass";
		}
		StringBuilder sb = new StringBuilder();
		for (IProject project : projects) {
			sb.append(command)
			  .append(" watch ")
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
	 * @deprecated debug only. not to use for production.
	 */
	public static void main(String[] args) {
		System.out.println(getEnvCommand());
	}
}
