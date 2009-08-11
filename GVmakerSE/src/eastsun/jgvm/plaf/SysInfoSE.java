package eastsun.jgvm.plaf;

import eastsun.jgvm.module.KeyModel;
import java.awt.event.KeyEvent;

/**
 * @version Aug 13, 2008
 * @author Eastsun
 */
public class SysInfoSE implements KeyModel.SysInfo {

    public int getLeft() {
        return KeyEvent.VK_LEFT;
    }

    public int getRight() {
        return KeyEvent.VK_RIGHT;
    }

    public int getUp() {
        return KeyEvent.VK_UP;
    }

    public int getDown() {
        return KeyEvent.VK_DOWN;
    }

    public int getEnter() {
        return KeyEvent.VK_ENTER;
    }

    public int getEsc() {
        return KeyEvent.VK_ESCAPE;
    }

    public boolean hasNumberKey() {
        return true;
    }

    public int getNumberKey(int num) {
        int key = 0;
        switch (num) {
            case 0:
                key = KeyEvent.VK_0;
                break;
            case 1:
                key = KeyEvent.VK_1;
                break;
            case 2:
                key = KeyEvent.VK_2;
                break;
            case 3:
                key = KeyEvent.VK_3;
                break;
            case 4:
                key = KeyEvent.VK_4;
                break;
            case 5:
                key = KeyEvent.VK_5;
                break;
            case 6:
                key = KeyEvent.VK_6;
                break;
            case 7:
                key = KeyEvent.VK_7;
                break;
            case 8:
                key = KeyEvent.VK_8;
                break;
            case 9:
                key = KeyEvent.VK_9;
                break;
        }
        return key;
    }
}
