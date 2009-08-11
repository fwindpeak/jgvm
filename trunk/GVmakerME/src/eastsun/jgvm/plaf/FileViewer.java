/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eastsun.jgvm.plaf;

import eastsun.jgvm.module.io.KeyMap;
import eastsun.jgvm.module.LavApp;
import eastsun.jgvm.module.io.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 *
 * @author Administrator
 */
public class FileViewer extends List implements CommandListener {

    private GVmakerME demo;
    private String gvmRoot;
    private Command open;
    private Command exit;

    public FileViewer(GVmakerME demo, String gvmRoot) {
        super("选择文件", List.IMPLICIT);
        this.demo = demo;
        this.gvmRoot = gvmRoot;
        open = new Command("打开", Command.OK, 1);
        exit = new Command("退出", Command.EXIT, 1);
        addCommand(open);
        addCommand(exit);
        setCommandListener(this);
    }

    void refresh() {
        this.deleteAll();
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(gvmRoot + "LAVA/", Connector.READ_WRITE);
            if (!fc.isDirectory()) {
                return;
            }
            Enumeration e = fc.list();
            while (e.hasMoreElements()) {
                this.append(e.nextElement().toString(), null);
            }
        } catch (IOException ie) {
            throw new IllegalArgumentException(ie.getMessage());
        } finally {
            try {
                fc.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exit) {
            demo.exit();
        }
        if (c == List.SELECT_COMMAND || c == open) {
            String name = getString(getSelectedIndex());
            FileConnection appFC = null;
            FileConnection keyFC = null;
            LavApp app = null;
            KeyMap map = null;
            try {
                appFC = (FileConnection) Connector.open(gvmRoot + "LAVA/" + name, Connector.READ_WRITE);
                InputStream in = appFC.openInputStream();
                app = LavApp.createLavApp(in);
                keyFC = (FileConnection) Connector.open(gvmRoot + "KEY/" + name + ".ini", Connector.READ_WRITE);
                in = keyFC.openInputStream();
                map = Util.parseKeyMap(in);
            } catch (Exception ex) {
                map = GVmakerME.DEFAULT_KEYMAP;
            } finally {
                try {
                    appFC.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    keyFC.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            demo.runApp(app, map);

        }
    }
}
