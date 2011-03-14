package eastsun.jgvm.module;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ��װһ����ִ�е�lav��������,���ڲ�ά��һ���ļ�ָ��
 * @author Eastsun
 */
public abstract class LavApp {

    private byte[] appData;
    private int offset;

    /**
     * ͨ��һ������������һ��LavApp����
     * @param in һ��������
     * @return һ��LavApp����
     * @throws java.lang.IllegalArgumentException ����IO��������ݸ�ʽ����ȷ
     */
    public static LavApp createLavApp(InputStream in) throws IllegalArgumentException {
        return new DefaultLavApp(in);
    }

    /**
     * ͨ��һ��lav��������������һ��LavApp<p>
     * ע��,LavApp�ڲ�ʹ�õľ��Ǹ�����,�ഴ�����ܴ��ⲿ�޸��������
     * @param data
     * @throws java.lang.IllegalArgumentException
     */
    protected LavApp(byte[] data) throws IllegalArgumentException {
        this.appData = data;
        verifyData();
    }

    /**
     * lav�������ݴ�С(�ֽ���)
     * @return size ���lav�������ݵ��ܴ�С,���ļ�ͷ
     */
    public final int size() {
        return appData.length;
    }

    /**
     * ��pointer����ȡһ�ֽ�����,��ʹpointer��һ<p>
     * ע��,���ﷵ��ֵ��char����,��Ӧlav��char����,��Ϊlav��char�������޷��ŵ�.
     */
    public final char getChar() {
        return (char) (appData[offset++] & 0xff);
    }

    /**
     * ��app�ж�ȡ���ֽ�����,��Ӧlav�е�int����
     * @return int
     */
    public final short getInt() {
        short s;
        s = (short) (appData[offset++] & 0xff);
        s |= (appData[offset++] & 0xff) << 8;
        return s;
    }

    /**
     * ��app�ж�ȡ���ֽ�����(�޷���),��Ӧlav���ļ�ָ������
     */
    public final int getAddr() {
        int addr;
        addr = appData[offset++] & 0xff;
        addr |= (appData[offset++] & 0xff) << 8;
        addr |= (appData[offset++] & 0xff) << 16;
        return addr;
    }

    /**
     * ��app�ж�ȡ���ֽ�����,��Ӧlav�е�long����
     */
    public final int getLong() {
        int i;
        i = appData[offset++] & 0xff;
        i |= (appData[offset++] & 0xff) << 8;
        i |= (appData[offset++] & 0xff) << 16;
        i |= (appData[offset++] & 0xff) << 24;
        return i;
    }

    /**
     * �õ���ǰ����ƫ����
     * @return pointer �´ζ�ȡʱ��λ��
     */
    public final int getOffset() {
        return offset;
    }

    /** ���ö�ȡƫ����
     * @param pos ƫ����,�´ζ�ȡ����ʱ��ʼλ��
     */
    public final void setOffset(int pos) {
        offset = pos;
    }

    private static class DefaultLavApp extends LavApp {

        public DefaultLavApp(InputStream in) {
            super(getDataByInputStream(in));
        }

        /**
         * 2008.3.5<p>
         * bug fixed<p>
         * J2ME��ĳЩInputStream��available�������Ƿ���0
         */
        private static byte[] getDataByInputStream(InputStream in) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] tmpBuffer = new byte[512];
            try {
                int length;
                while ((length = in.read(tmpBuffer)) != -1) {
                    bos.write(tmpBuffer, 0, length);
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                //do nothing
                }
            }
            return bos.toByteArray();
        }
    }

    /**
     * ������ݸ�ʽ��������Ӧ����
     * @param data һ��lavApp����
     * @throws java.lang.IllegalArgumentException ����ȷ��lava��ʽ
     */
    private void verifyData() throws IllegalArgumentException {
        if (appData.length <= 16) {
            throw new IllegalArgumentException("������Ч��LAV�ļ�!");
        }
        if (appData[0] != 0x4c || appData[1] != 0x41 || appData[2] != 0x56) {
            throw new IllegalArgumentException("������Ч��LAV�ļ�!");
        }
        offset = 16;
    }
}
