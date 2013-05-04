package com.chrhsmt.eclipse.plugin.compass.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.chrhsmt.eclipse.plugin.compass.console.ConsoleLogger;
import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;

/**
 * Process wrapped in Thread.
 * @author chr
 *
 */
public class ThreadProcess implements Runnable {

	private Map<String, String> additionalEnv;
	private String[] command;

	private Thread thread;
	private Process process;
	private boolean isActive = false;

	public ThreadProcess(Map<String, String> env, String...command) {
		this.additionalEnv = env;
		this.command = command;
	}

	/**
	 * start.
	 */
	public void start() {

		this.thread = new Thread(this);
		this.thread.start();
	}

	/**
	 * Stop process.
	 */
	public void stop() {
		this.isActive = false;
		if (this.process != null) {
			this.process.destroy();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		this.isActive = true;

		ProcessBuilder builder = new ProcessBuilder(this.command);
		builder.redirectErrorStream(true);
		
		builder.environment().putAll(ProcessUtils.join(builder.environment(), additionalEnv));

		try {
			this.process = builder.start();

			InputStream in = null;
			InputStreamReader reader = null;
			BufferedReader br = null;
			try {
				in = process.getInputStream();
				reader = new InputStreamReader(in);
				br = new BufferedReader(reader);
				String line;
				while ((line = br.readLine()) != null && this.isActive) {
					ConsoleLogger.output("compass", line);
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
			}

		} catch (IOException e) {
			PluginLogger.log(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
