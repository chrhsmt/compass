package com.chrhsmt.eclipse.plugin.compass.console;

import java.io.IOException;

import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;

import com.chrhsmt.eclipse.plugin.compass.internal.PluginLogger;

/**
 * @author chr
 *
 */
@SuppressWarnings("restriction")
public class ConsoleLogger {

	private IOConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++) {
    	    if (existing[i] instanceof ProcessConsole) {
    		    return (ProcessConsole) existing[i];
	       }
    	   if (name.equals(existing[i].getName())) {
    		   return (MessageConsole) existing[i];
	       }
        }
        //no console found, so create a new one
        MessageConsole myConsole = new MessageConsole(name, null);
        conMan.addConsoles(new IConsole[]{myConsole});

        return myConsole;
    }

    public void out(String consoleName, String message) {
    	IOConsole myConsole = findConsole(consoleName);
	    IOConsoleOutputStream out = myConsole.newOutputStream();
	    out.setFontStyle(SWT.BOLD);
	    out.setColor(new Color(Display.getCurrent(), new RGB(255,102,51)));
	    try {
		    out.write(message);
		} catch (IOException e) {
			PluginLogger.log(e.getMessage());
		}
    }

    public static void output(String consoleName, String message) {
    	ConsoleLogger log = new ConsoleLogger();
	    log.out(consoleName, message);
    }
}
