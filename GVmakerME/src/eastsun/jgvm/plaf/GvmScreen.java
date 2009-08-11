package eastsun.jgvm.plaf;

import eastsun.jgvm.module.FileModel;
import eastsun.jgvm.module.io.FileSystem;
import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.GvmConfig;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.io.DefaultFileModel;
import eastsun.jgvm.module.io.DefaultKeyModel;
import eastsun.jgvm.module.io.Properties;
import eastsun.jgvm.module.io.Util;
import eastsun.jgvm.module.event.Area;
import eastsun.jgvm.module.event.ScreenChangeListener;
import eastsun.jgvm.plaf.sys.FileSysME;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Eastsun
 * @version 2008-2-19
 */
public class GvmScreen extends Canvas implements ScreenChangeListener {

    private GVmakerME mid;
    private JGVM gvm;
    private DefaultKeyModel keyModel;
    private int[] buffer;
    private int sx,  sy,  width,  height;
    private boolean first = true;
    private boolean supportedQuickExit;
    private int quickExit;
    private int background;
    private int circ,  rate;

    public GvmScreen(GVmakerME mIDlet) throws IOException {
        this.mid = mIDlet;
        setFullScreenMode(true);

        circ = GVmakerME.parseIntValue(Properties.CIRC_ANGLE) / 90;
        rate = GVmakerME.parseIntValue(Properties.SCREEN_RATE);

        if (circ == 0 || circ == 2) {
            width = 160 * rate;
            height = 80 * rate;
        }
        else {
            width = 80 * rate;
            height = 160 * rate;
        }
        buffer = new int[width * height];
        sx = (getWidth() - width) / 2;
        sy = (getHeight() - height) / 2;

        keyModel = new DefaultKeyModel(Util.loadFromProperties(GVmakerME.SYSTEM_PROS));
        FileSystem fileSys = new FileSysME(GVmakerME.GVM_ROOT);
        String sqe = GVmakerME.SYSTEM_PROS.getProperty(Properties.QUICK_EXIT);
        if (sqe != null) {
            if (sqe.startsWith("'")) {
                quickExit = sqe.charAt(1);
            }
            else {
                sqe = sqe.toLowerCase();
                quickExit = sqe.startsWith("0x") ? Integer.parseInt(sqe.substring(2), 16) : Integer.parseInt(sqe);
            }
            supportedQuickExit = true;
        }
        else {
            supportedQuickExit = false;
        }
        background = GVmakerME.parseIntValue(Properties.BACKGROUND);

        GvmConfig cfg = new GvmConfig();
        FileModel fileModel = new DefaultFileModel(fileSys);
        gvm = JGVM.newGVM(cfg, fileModel, keyModel);
        gvm.setInputMethod(new InputMethodME());
        gvm.addScreenChangeListener(this);
    }

    public JGVM getGVM() {
        return gvm;
    }

    public DefaultKeyModel getKeyModel() {
        return keyModel;
    }

    /**
     * 告知该Canvas需要绘制背景
     */
    public void setFirst() {
        first = true;
    }

    protected void paint(Graphics g) {
        if (first) {
            g.setColor(background);
            g.fillRect(0, 0, getWidth(), getHeight());
            first = false;
        }

        g.drawRGB(buffer, 0, width, sx, sy, width, height, false);
    }

    public void screenChanged(ScreenModel screen, Area area) {
        screen.getRGB(buffer, area, rate, circ);
        switch (circ) {
            case 0:
                repaint(sx + area.getX() * rate, sy + area.getY() * rate, area.getWidth() * rate, area.getHeight() * rate);
                break;
            case 1:
                repaint(sx + area.getY() * rate, sy + height - (area.getX() + area.getWidth()) * rate, area.getHeight() * rate, area.getWidth() * rate);
                break;
            case 3:
                repaint(sx + width - (area.getY() + area.getHeight()) * rate, sy + area.getX() * rate, area.getHeight() * rate, area.getWidth() * rate);
                break;
        }

    }

    protected void keyPressed(int code) {
        if (supportedQuickExit && code == quickExit) {
            mid.exitApp();
        }
        keyModel.keyPreesed(code);
    }

    protected void keyReleased(int code) {
        keyModel.keyReleased(code);
    }
}
