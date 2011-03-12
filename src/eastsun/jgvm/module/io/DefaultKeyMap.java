package eastsun.jgvm.module.io;

/**
 * KeyMap的默认实现
 * @author Eastsun
 * @version 2008-2-21
 */
public class DefaultKeyMap implements KeyMap {

    int[] keyValues;
    char[] keyCodes;

    public DefaultKeyMap(int[] keyValues, char[] keyCodes) {
        if (keyValues.length != keyCodes.length) {
            throw new IllegalArgumentException("keyValues and keyCodes must have the same length!");
        }
        this.keyCodes = new char[keyCodes.length];
        System.arraycopy(keyCodes, 0, this.keyCodes, 0, keyCodes.length);
        this.keyValues = new int[keyValues.length];
        System.arraycopy(keyValues, 0, this.keyValues, 0, keyValues.length);
    }

    public int[] keyValues() {
        int[] newValues = new int[keyValues.length];
        System.arraycopy(keyValues, 0, newValues, 0, keyValues.length);
        return newValues;
    }

    public char translate(int rawKeyCode) {
        for (int index = 0; index < keyValues.length; index++) {
            if (keyValues[index] == rawKeyCode) {
                return keyCodes[index];
            }
        }
        return 0;
    }
}
