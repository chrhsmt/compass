package com.chrhsmt.eclipse.plugin.compass.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chr
 *
 */
public class ThreadProcess implements Runnable {

	private static final Logger logger = Logger.getLogger(ThreadProcess.class.getSimpleName());

	private Map<String, String> additionalEnv;
	private String[] command;

	private Thread thread;
	private Process process;

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

		this.process.destroy();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		ProcessBuilder builder = new ProcessBuilder(this.command);
		builder.redirectErrorStream(true);
		
		// set path
		for (String key : this.additionalEnv.keySet()) {
			
			for (String envKey : builder.environment().keySet()) {
				if (envKey.equalsIgnoreCase(key)) {
					String tmp = builder.environment().get(envKey);
					tmp += this.additionalEnv.get(key);
					builder.environment().put(envKey, tmp);
					continue;
				}
			}
			builder.environment().put(key, this.additionalEnv.get(key));
		}
		
		Process process = null;
		try {
			process = builder.start();
			process.waitFor();

			InputStream in = null;
			InputStreamReader reader = null;
			BufferedReader br = null;
			try {
				in = process.getInputStream();
				reader = new InputStreamReader(in);
				br = new BufferedReader(reader);
				String line;
				while ((line = br.readLine()) != null) {
					logger.info(line);
				}
				logger.info("end");
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

		} catch (IOException | InterruptedException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
