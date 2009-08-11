package eastsun.jgvm.plaf;

import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.io.KeyMap;
import eastsun.jgvm.module.LavApp;
import eastsun.jgvm.module.io.DefaultKeyModel;
import eastsun.jgvm.module.io.Properties;
import eastsun.jgvm.module.io.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * @author Eastsun
 */
public class GVmakerME extends MIDlet {

    private static final String KEY_MAP_FILE = "/eastsun/jgvm/plaf/sys/default.ini";
    private static final String SYS_PRO_FILE = "/eastsun/jgvm/plaf/sys/system.pro";
    public static final String GVM_ROOT;
    public static final KeyMap DEFAULT_KEYMAP;
    public static final Properties SYSTEM_PROS;
    

    static {
        InputStream in = GVmakerME.class.getResourceAsStream(KEY_MAP_FILE);
        KeyMap keyMap = null;
        try {
            keyMap = Util.parseKeyMap(in);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        Properties pro = null;
        try {
            pro = new Properties(GVmakerME.class.getResourceAsStream(SYS_PRO_FILE));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        SYSTEM_PROS = pro;
        DEFAULT_KEYMAP = keyMap;


        String root = SYSTEM_PROS.getProperty(Properties.GVM_ROOT);
        if (root == null) {
            Enumeration e = FileSystemRegistry.listRoots();
            while (e.hasMoreElements()) {
                root = "file://" + e.nextElement() + "GVM_ROOT/";
                FileConnection fc = null;
                try {
                    fc = (FileConnection) Connector.open(root, Connector.READ_WRITE);
                    if (fc.isDirectory()) {
                        break;
                    }
                    root = null;
                } catch (Exception ex) {
                    root = null;
                //do nothing
                } finally {
                    try {
                        fc.close();
                    } catch (Exception ex) {
                        //do nothing
                    }
                }
            }
            if (root == null) {
                root = "file://localhost/GVM_ROOT/";
            }
        }
        GVM_ROOT = root;
        //System.out.println("root " + root);
    }
    private static final int APP_RUNNING = 1;
    private static final int FILE_VIEW = 2;
    private static final int INSTALL = 3;
    private JGVM gvm;
    private Display display;
    private GvmScreen canvas;
    private FileViewer fileView;
    private DefaultKeyModel keyModel;
    private Thread thread;
    private volatile boolean stop;
    private int status;

    public GVmakerME() throws IOException {
        display = Display.getDisplay(this);
        status = INSTALL;
    }

    public void startApp() {
        if (INSTALL == status) {
            try {
                display.setCurrent(SoftInf.getSoftInf());
                canvas = new GvmScreen(this);
                gvm = canvas.getGVM();
                gvm.setColor(parseIntValue(Properties.BLACK_COLOR), parseIntValue(Properties.WHITE_COLOR));
                keyModel = canvas.getKeyModel();
                fileView = new FileViewer(this, GVM_ROOT);
                status = FILE_VIEW;
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                System.exit(-1);
            }
        }
        if (status == FILE_VIEW) {
            fileView.refresh();
            display.setCurrent(fileView);
        }
        else {
            canvas.setFirst();
            display.setCurrent(canvas);
        }
    }

    public static int parseIntValue(String name) {
        String value = SYSTEM_PROS.getProperty(name).toLowerCase();
        return value.startsWith("0x") ? Integer.parseInt(value.substring(2), 16) : Integer.parseInt(value);
    }

    public void runApp(LavApp app, KeyMap map) {
        status = APP_RUNNING;
        keyModel.setKeyMap(map);
        gvm.loadApp(app);
        stop = false;
        thread = new Thread(new Runnable() {

            public void run() {
                try {
                    while (!(stop || gvm.isEnd())) {
                        gvm.nextStep();
                    }
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (RuntimeException rex) {
                    rex.printStackTrace();
                } finally {
                    gvm.dispose();
                    status = FILE_VIEW;
                    fileView.refresh();
                    display.setCurrent(fileView);
                }

            }
        });
        canvas.setFirst();
        display.setCurrent(canvas);
        thread.start();
    }

    public void exit() {
        exitApp();
        destroyApp(true);
        notifyDestroyed();
    }

    public void exitApp() {
        if (thread != null) {
            thread.interrupt();
        }
        stop = true;
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
