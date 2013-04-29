package com.chrhsmt.eclipse.plugin.compass.actions;

import java.io.FileInputStream;
import java.io.IOException;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.chrhsmt.eclipse.plugin.compass.Activator;
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

		if (action.isChecked()) {
			// start
			PluginLogger.log("start");
			
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
		this.process.start();
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
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		PluginLogger.log("dispose");
		this.stopCommand();
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		PluginLogger.log("init");
		this.window = window;
		
		// stop compass process when workbench shutdown.
		this.window.addPageListener(new IPageListener() {
			@Override
			public void pageOpened(IWorkbenchPage page) {
			}
			
			@Override
			public void pageClosed(IWorkbenchPage page) {
				stopCommand();
			}
			
			@Override
			public void pageActivated(IWorkbenchPage page) {
			}
		});
	}
}