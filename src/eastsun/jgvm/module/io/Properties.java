package eastsun.jgvm.module.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 *
 * @author Eastsun
 * @version 2008-4-19
 */
public class Properties {

    public static final String BLACK_COLOR = "BLACK_COLOR";
    public static final String WHITE_COLOR = "WHITE_COLOR";
    public static final String BACKGROUND = "BACKGROUND";
    public static final String CIRC_ANGLE = "CIRC_ANGLE";
    public static final String SCREEN_RATE = "SCREEN_RATE";
    public static final String GVM_ROOT = "GVM_ROOT";
    public static final String KEY_ENTER = "KEY_ENTER";
    public static final String KEY_ESC = "KEY_ESC";
    public static final String KEY_UP = "KEY_UP";
    public static final String KEY_DOWN = "KEY_DOWN";
    public static final String KEY_LEFT = "KEY_LEFT";
    public static final String KEY_RIGHT = "KEY_RIGHT";
    public static final String NUMBER_KEY_SUPPORTED = "NUMBER_KEY_SUPPORTED";
    public static final String QUICK_EXIT = "QUICK_EXIT";
    public static final String KEY_NUMBER0 = "KEY_NUMBER0";
    public static final String KEY_NUMBER1 = "KEY_NUMBER1";
    public static final String KEY_NUMBER2 = "KEY_NUMBER2";
    public static final String KEY_NUMBER3 = "KEY_NUMBER3";
    public static final String KEY_NUMBER4 = "KEY_NUMBER4";
    public static final String KEY_NUMBER5 = "KEY_NUMBER5";
    public static final String KEY_NUMBER6 = "KEY_NUMBER6";
    public static final String KEY_NUMBER7 = "KEY_NUMBER7";
    public static final String KEY_NUMBER8 = "KEY_NUMBER8";
    public static final String KEY_NUMBER9 = "KEY_NUMBER9";
    private Hashtable ht;

    public Properties(InputStream in) throws IOException {
        ht = parseStream(in);
    }

    /**
     * 得到指定属性的值
     * @param name 属性名
     * @return  对应的值;如果该属性不存在,返回null
     */
    public String getProperty(String name) {
        return (String) ht.get(name);
    }

    private static Hashtable parseStream(InputStream in) throws IOException {
        Hashtable ht = new Hashtable();
        byte[] buffer = new byte[128];
        String name, property;
        int b, index;
        while (true) {
            b = skipCommentAndSpace(in);
            if (b == -1) {
                break;
            }
            index = 0;
            do {
                buffer[index++] = (byte) b;
                b = in.read();
            } while (b != -1 && b != '=');
            name = new String(buffer, 0, index).trim();
            b = skipCommentAndSpace(in);
            index = 0;
            do {
                buffer[index++] = (byte) b;
                b = in.read();
            } while (b != -1 && b != 0x0a);
            property = new String(buffer, 0, index).trim();
            ht.put(name, property);
        //System.out.println(name + " = " + property);
        }
        in.close();
        return ht;
    }

    private static int skipCommentAndSpace(InputStream in) throws IOException {
        int b = 0;
        while (true) {
            do {
                b = in.read();
            } while (isSpace(b));
            if (b == -1) {
                return -1;
            }
            else if (b == '#') {
                do {
                    b = in.read();
                } while (b != -1 && b != 0x0a);
                if (b == -1) {
                    return -1;
                }
            }
            else {
                return b;
            }
        }
    }

    private static boolean isSpace(int c) {
        return (c == 0x09 || c == 0x0a || c == 0x0b || c == 0x0c || c == 0x0d || c == 0x20);
    }
}
