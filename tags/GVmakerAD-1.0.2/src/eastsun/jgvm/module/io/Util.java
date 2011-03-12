package eastsun.jgvm.module.io;

import eastsun.jgvm.module.KeyModel;
import eastsun.jgvm.module.ram.Accessable;
import eastsun.jgvm.module.ram.Getable;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.R;

/**
 * һ��������,��Ҫ�ṩ�ַ�������Լ�һЩʵ�÷���
 * @author Eastsun
 * @version 2008/1/22
 */
public final class Util {

    private Util() {
        //
    }

    /**
     * �õ�src��addr��ʼ��length���ֽڵ�crc16��
     */
    public static char getCrc16Value(Getable src, int addr, int length) {
        char crc = 0, tmp;
        while (--length >= 0) {
            tmp = (char) ((crc >> 8) & 0xff);
            crc <<= 4;
            crc ^= CRC16_TAB[(tmp >> 4) ^ ((src.getByte(addr) & 0xff) >> 4)];
            tmp = (char) ((crc >> 8) & 0xff);
            crc <<= 4;
            crc ^= CRC16_TAB[(tmp >> 4) ^ (src.getByte(addr) & 0x0f)];
            addr++;
        }
        return crc;
    }
    private static final char[] CRC16_TAB = {
        0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
        0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef
    };

    /**
     * ��Properties�еõ�GVMϵͳ������Ϣ
     */
    public static KeyModel.SysInfo loadFromProperties(final Properties pp) {
        return new KeyModel.SysInfo() {

            int left, right, down, up, enter, esc;
            boolean hasNumberKey;
            int[] numberKey = new int[10];
            

            {
                left = getKeyValue(Properties.KEY_LEFT);
                right = getKeyValue(Properties.KEY_RIGHT);
                up = getKeyValue(Properties.KEY_UP);
                down = getKeyValue(Properties.KEY_DOWN);
                enter = getKeyValue(Properties.KEY_ENTER);
                esc = getKeyValue(Properties.KEY_ESC);
                hasNumberKey = pp.getProperty(Properties.NUMBER_KEY_SUPPORTED).equals("true");
                if (hasNumberKey) {
                    for (int index = 0; index <= 9; index++) {
                        numberKey[index] = getKeyValue("KEY_NUMBER" + index);
                    }
                }
            }

            int getKeyValue(String str) {
                str = pp.getProperty(str);
                if (str.startsWith("'")) {
                    return str.charAt(1);
                }
                else {
                    str = str.toLowerCase();
                    return str.startsWith("0x") ? Integer.parseInt(str.substring(2), 16) : Integer.parseInt(str);
                }
            }

            public int getLeft() {
                return left;
            }

            public int getRight() {
                return right;
            }

            public int getUp() {
                return up;
            }

            public int getDown() {
                return down;
            }

            public int getEnter() {
                return enter;
            }

            public int getEsc() {
                return esc;
            }

            public boolean hasNumberKey() {
                return hasNumberKey;
            }

            public int getNumberKey(int num) {
                return numberKey[num];
            }
        };
    }

    /**
     * �Ӱ��������ļ��н�����KeyMap<p>
     * �����ļ���ʽ:<p>
     *     1.������ʽ: [ע��] ϵͳKeyֵ=GVM��ӦKeyֵ [ע��]\n
     *       ��ʾ��ϵͳ�ϵİ���ֵΪ{ϵͳKeyֵ}�Ķ�Ӧ��GVM�ϰ���ֵ{GVM��ӦKeyֵ}
     *     2.����ע��: �����ļ���������ע��,����ʱ������ע�Ͳ���,ע�͸�ʽ��C++�е�ע�͸�ʽ��ͬ
     *     3.ע������: ÿ�����ֻ����һ��������,(��{ϵͳKeyֵ=GVM��ӦKeyֵ}),������ǰ����Դ���ע��,��ע�����м䲻�ܻ���ע��
     * @param in ���ڽ�����������
     * @return ��������KeyMap
     * @throws java.io.IOException ����IO����
     */
    public static KeyMap parseKeyMap(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] tmpBuffer = new byte[512];
        int length;
        while ((length = in.read(tmpBuffer)) != -1) {
            bos.write(tmpBuffer, 0, length);
        }
        in.close();
        byte[] buffer = bos.toByteArray();
        int count = 0;
        for (int index = 0; index < buffer.length;) {
            if (buffer[index] == '=') {
                count++;
            }
            int comment = skipComment(buffer, index);
            if (comment > 0) {
                index += comment;
            }
            else {
                index++;
            }
        }
        int[] keyValues = new int[count];
        char[] keyCodes = new char[count];
        int start = 0, end = 0;
        while (--count >= 0) {
            while (buffer[end] != '=') {
                int comment = skipComment(buffer, end);
                if (comment == 0) {
                    end++;
                }
                else {
                    end += comment;
                    start = end;
                }
            }
            keyValues[count] = parseInt(buffer, start, end);
            start = end;
            while (end < buffer.length && buffer[end] != 0x0a) {
                int command = skipComment(buffer, end);
                if (command == 0) {
                    end++;
                }
                else {
                    break;
                }
            }
            keyCodes[count] = (char) parseInt(buffer, start, end);
            start = end;
        }

        return new DefaultKeyMap(keyValues, keyCodes);
    }

    /**
     * ����ע�Ͳ���
     * @param data
     * @param offset
     * @return �����ע��,����ע���ܳ���;���򷵻�0
     */
    private static int skipComment(byte[] data, int offset) {
        int start = offset;
        if (data[offset++] != '/') {
            return 0;
        }
        if (data[offset] != '/' && data[offset] != '*') {
            return 0;
        }
        if (data[offset++] == '/') {
            while (offset < data.length && data[offset] != 0x0a) {
                offset++;
            }
        }
        else {
            while (!(data[offset] == '*' && data[offset + 1] == '/')) {
                offset++;
            }
            offset += 2;
        }
        return offset - start;
    }

    /**
     * ����һ��byte��ʽ���ַ���Ϊint,���byte���鿪ʼ���β���ܴ��ڶ�����ַ�
     * @return �������
     */
    private static int parseInt(byte[] data, int start, int end) {
        int mark = -1, count = 0;
        for (int index = start; index < end; index++) {
            if (data[index] == '\'') {
                count++;
                if (count > 2 || (count == 2 && index != mark + 2)) {
                    throw new IllegalArgumentException("���������ļ�����!" + count + "," + index + "," + mark);
                }
                if (count == 1) {
                    mark = index;
                }
            }
        }
        if (count > 0) {
            return data[mark + 1];
        }
        while (!((data[start] >= '0' && data[start] <= '9') || data[start] == '-')) {
            start++;
        }
        int num = 0;
        if (data[start] == '0' && (data[start + 1] == 'x' || data[start + 1] == 'X')) {
            start += 2;
            while (start < end) {
                byte b = data[start];
                if (b >= '0' && b <= '9') {
                    num <<= 4;
                    num |= b - '0';
                }
                else if (b >= 'a' && b <= 'f') {
                    num <<= 4;
                    num |= b - 'a' + 10;
                }
                else if (b >= 'A' && b <= 'F') {
                    num <<= 4;
                    num |= b = 'A' + 10;
                }
                else {
                    break;
                }
                start++;
            }
        }
        else {
            boolean flags = (data[start] == '-');
            if (flags) {
                start++;
            }
            while (start < end) {
                byte b = data[start];
                if (b >= '0' && b <= '9') {
                    num = num * 10 + b - '0';
                }
                else {
                    break;
                }
                start++;
            }
            if (flags) {
                num = -num;
            }
        }
        return num;
    }

    /**
     * ��һ����������gb2312������ַ�������
     * @param i һ������ֵ
     * @return һ����ʾgb2312�����byte����
     */
    public static byte[] intToGB(int i) {
        int sign = i < 0 ? 1 : 0;
        if (i < 0) {
            i = -i;
        }
        int length = 1;
        int tmp = i;
        while ((tmp /= 10) > 0) {
            length++;
        }
        byte[] data = new byte[sign + length];
        while (--length >= 0) {
            data[length + sign] = (byte) (i % 10 + 0x30);
            i /= 10;
        }
        if (sign > 0) {
            data[0] = 0x2d;
        }
        return data;
    }

    /**
     * ����ͬGVmaker�е�cos����
     */
    public static int cos(int deg) {
        deg &= 0x7fff;
        deg = 90 - deg;
        deg = deg % 360 + 360;
        return sin(deg);
    }

    /**
     * ����ͬGVmaker�е�cos����
     */
    public static int sin(int deg) {
        deg &= 0x7fff;
        deg %= 360;
        switch (deg / 90) {
            case 0:
                return sinTab[deg];
            case 1:
                return sinTab[180 - deg];
            case 2:
                return -sinTab[deg - 180];
            default:
                return -sinTab[360 - deg];
        }
    }

    /**
     * ��һ��byte�����װΪһ��Accessable
     * @param array byte����
     * @return һ��Accessable
     */
    public static Accessable asAccessable(final byte[] array) {
        return new Accessable() {

            public byte getByte(
                    int addr) throws IndexOutOfBoundsException {
                return array[addr];
            }

            public void setByte(int addr, byte b) throws IndexOutOfBoundsException {
                array[addr] = b;
            }
        };
    }

    /**
     * �õ�gb2312�����ַ�c�ĵ�������<p>
     * ����ַ�����һ��������gb2312�ַ�,����0�������
     * @param c �ַ�
     * @param data ���������������,���СӦ��С��12��24
     * @return count �����ֽ���
     */
    public static int getGB12Data(char c, byte[] data) {
        int offset, count;
        byte[] buffer;
        if (c <= 0xff) {
            count = 12;
            offset = c * 12;
            buffer = ascii12Data;
        }
        else {
            count = 24;
            buffer = gb12Data;
            int high = (c & 0xff) - 0xa1;
            if (high > 8) {
                high -= 6;
            }
            offset = (high * 94 + (c >> 8) - 0xa1) * 24;
        }
        if (offset < 0 || offset + count > buffer.length) {
            fillZero(data);
        }
        else {
            for (int index = count - 1; index >= 0; index--) {
                data[index] = buffer[offset + index];
            }
        }
        return count;
    }

    private static void fillZero(byte[] array) {
        for (int index = 0; index < array.length; index++) {
            array[index] = 0;
        }
    }

    /**
     * �õ�gb2312�����ַ�c�ĵ�������
     * @param c �ַ�
     * @param data ���������������,���СӦ��С��16��32
     * @return count �����ֽ���
     */
    public static int getGB16Data(char c, byte[] data) {
        int offset, count;
        byte[] buffer;
        if (c <= 0xff) {
            count = 16;
            offset = c << 4;
            buffer = ascii16Data;
        }
        else {
            count = 32;
            buffer = gb16Data;
            int high = (c & 0xff) - 0xa1;
            if (high > 8) {
                high -= 6;
            }
            offset = (high * 94 + (c >> 8) - 0xa1) << 5;
        }
        if (offset < 0 || offset + count > buffer.length) {
            fillZero(data);
        }
        else {
            for (int index = count - 1; index >= 0; index--) {
                data[index] = buffer[offset + index];
            }
        }
        return count;
    }
    private static byte[] gb16Data;
    private static byte[] gb12Data;
    private static byte[] ascii16Data;
    private static byte[] ascii12Data;
    private static final int[] sinTab = {
        0, 18, 36, 54, 71, 89, 107, 125, 143, 160, 178, 195, 213, 230, 248, 265, 282,
        299, 316, 333, 350, 367, 384, 400, 416, 433, 449, 465, 481, 496, 512, 527,
        543, 558, 573, 587, 602, 616, 630, 644, 658, 672, 685, 698, 711, 724, 737,
        749, 761, 773, 784, 796, 807, 818, 828, 839, 849, 859, 868, 878, 887, 896,
        904, 912, 920, 928, 935, 943, 949, 956, 962, 968, 974, 979, 984, 989, 994,
        998, 1002, 1005, 1008, 1011, 1014, 1016, 1018, 1020, 1022, 1023, 1023, 1024, 1024
    };

    private static void readData(InputStream in, byte[] buffer) throws IOException {
        int offset = 0;
        int len = buffer.length;
        do {
            int size = in.read(buffer, offset, len - offset);
            if (size == -1) {
                break;
            }
            offset += size;
        } while (offset < len);
        in.close();
    }
    

    static {
        try {
            InputStream in = null;
            
            android.content.Context con = eastsun.jgvm.plaf.android.MainView.getCurrentView().getContext();
            
            in = con.getResources().openRawResource(eastsun.jgvm.plaf.android.R.raw.gbfont);
            //in = Util.class.getResourceAsStream("/eastsun/jgvm/module/io/res/gbfont.bin");
            gb12Data = new byte[in.available()];
            readData(in, gb12Data);

            in = con.getResources().openRawResource(eastsun.jgvm.plaf.android.R.raw.gbfont16);
            //in = Util.class.getResourceAsStream("/eastsun/jgvm/module/io/res/gbfont16.bin");
            gb16Data = new byte[in.available()];
            readData(in, gb16Data);

            in = con.getResources().openRawResource(eastsun.jgvm.plaf.android.R.raw.ascii);
            //in = Util.class.getResourceAsStream("/eastsun/jgvm/module/io/res/ascii.bin");
            ascii12Data = new byte[in.available()];
            readData(in, ascii12Data);

            in = con.getResources().openRawResource(eastsun.jgvm.plaf.android.R.raw.ascii8);
            //in = Util.class.getResourceAsStream("/eastsun/jgvm/module/io/res/ascii8.bin");
            ascii16Data = new byte[in.available()];
            readData(in, ascii16Data);
        } catch (IOException ex) {
            throw new IllegalStateException(ex.toString());
        }
    }
}
