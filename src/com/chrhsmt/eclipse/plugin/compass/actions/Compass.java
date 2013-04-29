package com.chrhsmt.eclipse.plugin.compass.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.chrhsmt.eclipse.plugin.compass.Activator;
import com.chrhsmt.eclipse.plugin.compass.console.ConsoleLogger;
import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;
import com.chrhsmt.eclipse.plugin.compass.process.ThreadProcess;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class Compass implements IWorkbenchWindowActionDelegate {

	private static final String CONFIG_FILE_NAME = "config.rb";

	public static final String PREF_KEY_RUBY_PATH = "RUBY_PATH";
	public static final String PREF_KEY_GEM_PATH = "GEM_PATH";
	public static final String PREF_KEY_COMPASS_PATH = "COMPASS_PATH";
	public static final String PREF_KEY_OTHER_PATH = "OTHER_PATH";

	private IWorkbenchWindow window;
	private IWorkspaceRoot root;
	
	private List<IProject> targetProjects = new ArrayList<IProject>();

	private ThreadProcess process;

	/**
	 * The constructor.
	 */
	public Compass() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {

		this.root = ResourcesPlugin.getWorkspace().getRoot();

//		MessageDialog.openInformation(
//		window.getShell(),
//		"Compass",
//		"Hello, Eclipse world");

		if (action.isChecked()) {
			// start
			PluginLogger.log("start");
			ConsoleLogger.output("compass", "start");
			
			// getCommandPath
			String path = this.getCommandPathes();

			// check config.rb
			this.check();

			// start
			try {
				this.startCommand(path, this.targetProjects.get(0).getLocation().toOSString());
			} catch (IOException | InterruptedException e) {
				PluginLogger.log(e.getMessage(), e);
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
				ErrorDialog.openError(
						this.window.getShell(),
						"command 'compass' runtime error",
						e.getMessage(),
						status);
			}

//			IProject[] projects = root.getProjects();
//			for (IProject project : projects) {
//				try {
//					Map<QualifiedName, String> props = project.getPersistentProperties();
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
//			}

		} else {
			// stop
			this.stopCommand();
		}
	}

	private String getCommandPathes() {
		// search compass script from PreferenceStore.
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		StringBuilder builder = new StringBuilder();
		builder.append(":")
		       .append(store.getString(PREF_KEY_RUBY_PATH));
		String otherPath = store.getString(PREF_KEY_OTHER_PATH);
		if (otherPath != null && otherPath.length() > 0) {
			builder.append(":")
			       .append(otherPath);
		}
		return builder.toString();
	}

	/**
	 * specify target project.
	 */
	private void check() {
		for (IProject project : this.root.getProjects()) {
			if (project.exists() && project.isOpen()) {
				IFile file = project.getFile(CONFIG_FILE_NAME);
				if (!file.exists() || !file.isAccessible()) {
					continue;
				}
				this.targetProjects.add(project);
//				this.readProperties(file.getLocation().toOSString());
			} else {
				continue;
			}
		}
	}

	@SuppressWarnings("unused")
	private Properties readProperties(String path) {
		try {
			FileInputStream in = new FileInputStream(path);
			Properties prop = new Properties();
			prop.load(in);
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			PluginLogger.log(e.getMessage(), e);
			return null;
		}
	}

	private void startCommand(final String path, final String projectPath) throws IOException, InterruptedException {

		Map<String, String> env = new HashMap<>();
		env.put("PATH", path);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		process = new ThreadProcess(env, store.getString(PREF_KEY_COMPASS_PATH), "watch", projectPath);
//		this.process.start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					ProcessBuilder builder = new ProcessBuilder(store.getString(PREF_KEY_COMPASS_PATH), "watch", projectPath);
					builder.redirectErrorStream(true);
					
					String pathKey = null;
					for (String key : builder.environment().keySet()) {
						if (key.equalsIgnoreCase("path")) {
							pathKey = key;
						}
					}
					
					// set path
					String tmpPath = builder.environment().get(pathKey);
					String newPath = path + ":" + tmpPath;
					builder.environment().put(pathKey, newPath);
					builder.environment().put("GEM_PATH", store.getString(PREF_KEY_GEM_PATH));

					Process process = builder.start();
					IProcess iProcess = DebugPlugin.newProcess(new Launch(null, ILaunchManager.RUN_MODE, null), process, "");
					iProcess.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener() {
						@Override
						public void streamAppended(String text, IStreamMonitor monitor) {
							ConsoleLogger.output("compass", text);
						}
					});

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
							PluginLogger.log(line);
						}
						PluginLogger.log("end");
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
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	/**
	 * stop process.
	 */
	private void stopCommand() {
		if (this.process != null) {
			this.process.stop();
		}
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		PluginLogger.log("change");
		PluginLogger.log("empty:" + selection.isEmpty());
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection stSelection = (IStructuredSelection) selection;
			if (stSelection.getFirstElement() != null) {
				PluginLogger.log("firstElement : " + stSelection.getFirstElement().getClass());
			}
		}
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		PluginLogger.log("dispose");
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		PluginLogger.log("init");
		this.window = window;
	}
}