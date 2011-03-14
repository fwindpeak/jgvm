/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eastsun.jgvm.plaf.android;

import android.view.KeyEvent;
import eastsun.jgvm.module.KeyModel;
import eastsun.jgvm.module.io.DefaultKeyMap;
import eastsun.jgvm.module.io.DefaultKeyModel;

import static android.view.KeyEvent.*;


/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class KeyBoard {
	
    public KeyBoard() {
        keyModel = new DefaultKeyModel(new SysInfo());
        keyModel.setKeyMap(new DefaultKeyMap(rawKeyCodes, gvmKeyValues));
    }

    public KeyModel getKeyModel() {
        return keyModel;
    }
    
    private int indexOfKeyCode(int code) {
        int index = -1;
        for (int i = 0; i < rawKeyCodes.length; i++) {
            if (rawKeyCodes[i] == code) {
                index = i;
                break;
            }
        }
        return index;
    }
    
	public boolean doKeyDown(int keyCode, KeyEvent event) {
		if(indexOfKeyCode(keyCode) != -1) {
			keyModel.keyPreesed(keyCode);
			return true;
		} else {
			return false;
		}
	}

	public boolean doKeyUp(int keyCode, KeyEvent event) {
		if(indexOfKeyCode(keyCode) != -1) {
			keyModel.keyReleased(keyCode);
			return true;
		} else {
			return false;
		}
	}
	
    private static int[] rawKeyCodes = {        
    	KEYCODE_SHIFT_LEFT /*VK_F1*/, KEYCODE_ALT_LEFT /*VK_F2*/, KEYCODE_SEARCH /*VK_F3*/, KEYCODE_AT/*VK_F4*/,
    	
        KEYCODE_Q, KEYCODE_W, KEYCODE_E, KEYCODE_R, KEYCODE_T, KEYCODE_Y, KEYCODE_U, KEYCODE_I, KEYCODE_O, KEYCODE_P,
        KEYCODE_A, KEYCODE_S, KEYCODE_D, KEYCODE_F, KEYCODE_G, KEYCODE_H, KEYCODE_J, KEYCODE_K, KEYCODE_L, KEYCODE_ENTER,
        KEYCODE_Z, KEYCODE_X, KEYCODE_C, KEYCODE_V, KEYCODE_B, KEYCODE_N, KEYCODE_M, KEYCODE_VOLUME_UP /*VK_PAGE_UP*/, KEYCODE_COMMA/*KEYCODE_DPAD_UP*/ ,KEYCODE_VOLUME_DOWN /*KEYCODE_PAGE_DOWN*/,
        KEYCODE_EXPLORER /*VK_HELP*/, KEYCODE_TAB  /*VK_SHIFT*/,  KEYCODE_NUM /*VK_CAPS_LOCK*/, KEYCODE_DEL/*VK_ESCAPE*/, KEYCODE_0, KEYCODE_SYM/*VK_PERIOD*/, KEYCODE_SPACE,
        
        //KEYCODE_DPAD_LEFT, KEYCODE_DPAD_DOWN, KEYCODE_DPAD_RIGHT, 
        KEYCODE_PERIOD, KEYCODE_ALT_RIGHT, KEYCODE_SHIFT_RIGHT
    };
    
    private static char[] gvmKeyValues = {    	
        (char) 28, (char) 29, (char) 30, (char) 31,
        
        'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
        'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', (char) 13,
        'z', 'x', 'c', 'v', 'b', 'n', 'm', (char) 19, (char) 20,(char) 14,
        (char) 25, (char) 26, (char) 18, (char) 27, '0', '.', ' ',(char) 23, (char) 21, (char) 22
    };
    
    DefaultKeyModel keyModel;
}
