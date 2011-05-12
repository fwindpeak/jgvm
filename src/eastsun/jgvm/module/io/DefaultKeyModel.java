package eastsun.jgvm.module.io;

import eastsun.jgvm.module.KeyModel;

/**
 * KeyModel的默认实现<p>
 * 该实现提供了两个回调方法:keyReleased与KeyPressed,当系统按键发生变化时应当确保调用这些方法
 * @author Eastsun
 * @version 2008-2-21
 */
public final class DefaultKeyModel implements KeyModel {

    private int[] keyValues;
    private boolean[] keyStatus;
    private boolean hasKey;
    private int keyCode;
    private KeyMap keyMap;
    private SysInfo keyInf;

    public DefaultKeyModel(SysInfo keyInf) {
        this.keyInf = keyInf;
        hasKey = false;
    }

    public void setKeyMap(KeyMap map) {
        this.keyMap = map;
        keyValues = keyMap.keyValues();
        keyStatus = new boolean[keyValues.length];
    }

    public synchronized void keyPreesed(int rawKeyCode) {
        hasKey = true;
        keyCode = rawKeyCode;
        for (int index = 0; index < keyValues.length; index++) {
            if (rawKeyCode == keyValues[index]) {
                keyStatus[index] = true;
                break;
            }
        }
        notify();
    }

    public synchronized void keyReleased(int rawKeyCode) {
        if (keyCode == rawKeyCode) {
            hasKey = false;
        }
        for (int index = 0; index < keyValues.length; index++) {
            if (rawKeyCode == keyValues[index]) {
                keyStatus[index] = false;
                break;
            }
        }
    }

    public synchronized void releaseKey(char key) {
        //System.out.printf("CheckKey :%2x%n", (int) key);
        if ((key & 0x80) != 0) {
            for (int index = 0; index < keyStatus.length; index++) {
                if (keyStatus[index]) {
                    hasKey = true;
                    keyCode = keyValues[index];
                    break;
                }
            }
        } else {
            for (int index = 0; index < keyStatus.length; index++) {
                if (keyStatus[index] && key == keyMap.translate(keyValues[index])) {
                    hasKey = true;
                    keyCode = keyValues[index];
                    break;
                }
            }
        }
    }

    public synchronized char checkKey(char key) {
        //System.out.printf("CheckKey :%2x%n",(int)key);
        if ((key & 0x80) != 0) {
            for (int index = 0; index < keyStatus.length; index++) {
                if (keyStatus[index]) {
                    return keyMap.translate(keyValues[index]);
                }
            }
            return 0;
        } else {
            for (int index = 0; index < keyStatus.length; index++) {
                if (keyStatus[index] && key == keyMap.translate(keyValues[index])) {
                    return key;
                }
            }
            return 0;
        }
    }

    public char getchar() throws InterruptedException {
        return keyMap.translate(getRawKey());
    }

    public synchronized char inkey() {
        //System.out.printf("inkey");
        if (hasKey) {
            hasKey = false;
            return keyMap.translate(keyCode);
        } else {
            return 0;
        }
    }

    public synchronized int getRawKey() throws InterruptedException {
        while (true) {
            if (hasKey) {
                hasKey = false;
                return keyCode;
            } else {
                wait();
            }
        }
    }

    public SysInfo getSysInfo() {
        return keyInf;
    }
}
