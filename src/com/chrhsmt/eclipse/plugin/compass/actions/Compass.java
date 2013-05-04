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
import com.chrhsmt.eclipse.plugin.compass.console.ConsoleLogger;
import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;
import com.chrhsmt.eclipse.plugin.compass.process.ProcessUtils;
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

	private List<ThreadProcess> processes = new ArrayList<ThreadProcess>();

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
			
			this.targetProjects.clear();
			this.processes.clear();

			// start
			PluginLogger.log("start");
			
			// getCommandPath
			String path = this.getCommandPathes();

			if (!this.checkCommandExists(path)) {
				final String message =
						"compass executable command does not exists. please check your path preference.";
				IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
				ErrorDialog.openError(
						this.window.getShell(),
						"command 'compass' runtime error",
						message,
						status);
				return;
			}

			// check config.rb
			this.checkProjects();

			// start
			try {
				for (IProject project : this.targetProjects) {
					ConsoleLogger.output("compass", String.format("In %s", project.getName()));
					this.startCommand(path, project.getLocation().toOSString());
				}
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
	 * check command exists.
	 * @param path
	 * @return
	 */
	private boolean checkCommandExists(String path) {
		Map<String, String> env = new HashMap<>();
		env.put("PATH", path);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return ProcessUtils.exists(store.getString(PREF_KEY_COMPASS_PATH), env);
	}

	/**
	 * specify target project.
	 */
	private void checkProjects() {
		
		for (IProject project : this.root.getProjects()) {
			if (project.exists() && project.isOpen()) {
				IFile file = project.getFile(CONFIG_FILE_NAME);
				if (!file.exists() || !file.isAccessible()) {
					ConsoleLogger.output("compass",
							String.format("Project '%s' does not have config.rb or read it.", project.getName()));
					continue;
				} else {
					ConsoleLogger.output("compass",
							String.format("Project '%s' found config.rb.", project.getName()));
					this.targetProjects.add(project);
//					this.readProperties(file.getLocation().toOSString());
				}
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
		
		ThreadProcess process = new ThreadProcess(env, store.getString(PREF_KEY_COMPASS_PATH), "watch", projectPath);
		process.start();
		processes.add(process);
	}

	/**
	 * stop process.
	 */
	private void stopCommand() {
		if (this.processes != null && !this.processes.isEmpty()) {
			for (ThreadProcess process : this.processes) {
				process.stop();
			}
			this.processes.clear();
		}
		this.targetProjects.clear();
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
	@SuppressWarnings("restriction")
	public void init(IWorkbenchWindow window) {
		PluginLogger.log("init");
		this.window = window;

//		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
////		for (ILaunchConfigurationType type : manager.getLaunchConfigurationTypes()) {
////			System.out.println(type.getIdentifier() + ":" + type.getName());
////		}
//		
//		try {
//			IWorkbench workbench = 
//				    PlatformUI.getWorkbench();
//				WorkbenchWindow workbenchWindow = 
//				    (WorkbenchWindow)workbench.
//				    getActiveWorkbenchWindow();
//				IActionBars bars = 
//				    workbenchWindow.getActionBars();
//				IStatusLineManager 
//				    lineManager = bars.getStatusLineManager();
//				IProgressMonitor monitor = 
//				    lineManager.getProgressMonitor();
//			ILaunchConfigurationType compasstype = manager.getLaunchConfigurationType("com.chrhsmt.eclipse.plugin.compass.CompassLaunchConfigurationType");
//			ILaunch launch = compasstype.newInstance(null, Activator.PLUGIN_ID).launch(ILaunchManager.RUN_MODE, monitor);
//			manager.addLaunch(launch);
//		} catch (CoreException e2) {
//			e2.printStackTrace();
//		}
//		
//		WorkbenchJob job = new WorkbenchJob("compass-watch") {
//			@Override
//			public IStatus runInUIThread(IProgressMonitor monitor) {
//				
//				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
//				ILaunchConfigurationType type = manager.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE);
//				String location = "/Users/chr/.rvm/gems/ruby-1.9.3-p392/bin/compass";
//				try {
//					ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, Activator.PLUGIN_ID);
//					workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION, location);
//					workingCopy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "--help");
//					workingCopy.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, new HashMap<>());
//					workingCopy.setAttribute(IExternalToolConstants.ATTR_LAUNCH_IN_BACKGROUND, true);
//					monitor.beginTask("compass", 100);
//					ILaunch launch = workingCopy.launch(ILaunchManager.RUN_MODE, new SubProgressMonitor(monitor, 20), false, false);
//					manager.addLaunch(launch);
//					monitor.worked(10);
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					return Status.OK_STATUS;
//				} catch (CoreException e) {
//					e.printStackTrace();
//					return e.getStatus();
//				}
//				
//			}
//		};
//		job.schedule();

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