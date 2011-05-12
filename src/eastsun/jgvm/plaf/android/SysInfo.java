package eastsun.jgvm.plaf.android;

import eastsun.jgvm.module.KeyModel;
import android.view.KeyEvent;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class SysInfo implements KeyModel.SysInfo {

    public int getLeft() {
        return KeyEvent.KEYCODE_DPAD_LEFT;
    }

    public int getRight() {
    	return KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    public int getUp() {
    	return KeyEvent.KEYCODE_DPAD_UP;
    }

    public int getDown() {
    	return KeyEvent.KEYCODE_DPAD_DOWN;
    }

    public int getEnter() {
    	return KeyEvent.KEYCODE_ENTER;
    }

    public int getEsc() {
        return KeyEvent.KEYCODE_BACK;
    }

    public boolean hasNumberKey() {
        return true;
    }

    public int getNumberKey(int num) {
        int key = 0;
        switch (num) {
            case 0:
                key = KeyEvent.KEYCODE_0;
                break;
            case 1:
            	key = KeyEvent.KEYCODE_1;
                break;
            case 2:
            	key = KeyEvent.KEYCODE_2;
                break;
            case 3:
            	key = KeyEvent.KEYCODE_3;
                break;
            case 4:
            	key = KeyEvent.KEYCODE_4;
                break;
            case 5:
            	key = KeyEvent.KEYCODE_5;
                break;
            case 6:
            	key = KeyEvent.KEYCODE_6;
                break;
            case 7:
            	key = KeyEvent.KEYCODE_7;
                break;
            case 8:
            	key = KeyEvent.KEYCODE_8;
                break;
            case 9:
            	key = KeyEvent.KEYCODE_9;
                break;
        }
        return key;
    }
}
