package eastsun.jgvm.module.io;

import eastsun.jgvm.module.ram.Setable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

/**
 * ��װƴ�����뷨�������Ϣ
 * @author Eastsun
 * @version 2008-3-17
 */
public final class SpellInfo {

    private SpellInfo() {
    }

    /**
     * �õ�����SpellNode��ö����,���а�����ƴ���ֵ�˳�����е�count��SpellNode
     */
    public static Enumeration list() {
        return new SpellNodeEnum();
    }

    /**
     * �õ���ͬƴ������Ŀ
     * @return count
     */
    public static int count() {
        return SPELL_NODES.length;
    }
    private static final String SPELL_DATA = "/eastsun/jgvm/module/io/res/spell.dat";
    private static final SpellNode[] SPELL_NODES;
    

    static {
        //װ��ƴ������
        //���ݸ�ʽ:
        //��һ���ֽ�0xAB��ʾ�汾��ΪA.B
        //�ڶ�,�����ֽڱ�ʾ�ܹ��ж��ٸ�ƴ��
        //������Ϊƴ������,ÿ��ƴ�����ݵĸ�ʽΪ"��Ӧ������(һ�ֽ�) ƴ���ַ��� |"
        //�����Ǻ�������,��ƴ�����ֵĴ���˳������
    	
    	android.content.Context con = eastsun.jgvm.plaf.android.MainView.getCurrentView().getContext();
    	InputStream in = con.getResources().openRawResource(eastsun.jgvm.plaf.android.R.raw.spell);
        //InputStream in = SpellInfo.class.getResourceAsStream(SPELL_DATA);
        SpellNode[] tmp = null;
        try {
            int version = in.read();
            if (version != 0x10) {
                throw new IllegalStateException("���ʺϵ����ݰ汾:" + Integer.toHexString(version));
            }
            int count = in.read();
            count = count + in.read() * 256;
            tmp = new SpellNode[count];
            byte[] buffer = new byte[20];
            for (int index = 0; index < count; index++) {
                int size = in.read();
                int n = 0, b;
                while ((b = in.read()) != '|') {
                    buffer[n++] = (byte) b;
                }
                String spell = new String(buffer, 0, n);
                tmp[index] = new SpellNode(spell, size);
            }
            for (int index = 0; index < count; index++) {
                SpellNode node = tmp[index];
                byte[] data = new byte[2 * node.size];
                in.read(data);
                node.setData(data);
            }
        } catch (IOException ex) {
            System.exit(-1);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                //do nothing
            }
        }
        SPELL_NODES = tmp;
    }

    private static class SpellNodeEnum implements Enumeration {

        private int index = 0;

        public boolean hasMoreElements() {
            return index < SPELL_NODES.length;
        }

        public Object nextElement() {
            return SPELL_NODES[index++];
        }
    }

    public static class SpellNode {

        private String spell;
        private byte[] data;
        private int size;

        /**
         * ��װ����ƴ���������Ϣ
         * @param spell ƴ��
         * @param data spell��Ӧ�ĺ��ֵ�gb2312������ɵ�����,�ڲ���ֱ��ʹ���������
         */
        SpellNode(String spell, int size) {
            this.spell = spell;
            this.size = size;
        }

        /**
         * �õ���node��ƴ���ַ���
         */
        public String spell() {
            return spell;
        }

        /**
         * ��ƴ����Ӧ���ָ���
         */
        public int size() {
            return size;
        }

        /**
         * ȡ�ø�ƴ����id��ʼ��len�����ֵ�gb2312��������
         * @param dst ���ڱ�������
         * @param id       ��ʼ�ĺ��ֱ��,��0��ʼ
         * @param len     ����ȡ�ĺ��ָ���
         * @return           ʵ�ʻ�ȡ�ĺ��ָ���
         */
        public int getGB(Setable dst, int offset, int id, int len) {
            id <<= 1;
            len <<= 1;
            int index = 0;
            while (index < len && id + index < data.length) {
                dst.setByte(offset + index, data[id + index]);
                index++;
                dst.setByte(offset + index, data[id + index]);
                index++;
            }
            return index >>> 1;
        }

        public String toString() {
            String gbStr = null;
            try {
                gbStr = new String(data, 0, data.length, "gb2312");
            } catch (UnsupportedEncodingException ex) {
                gbStr ="Don't Unsupport GB2312";
            }
            return spell + ": " + gbStr;
        }

        /**
         *  ���ø�node�ĺ�������,�ڲ�ֱ��ʹ�ø�byte����
         */
        void setData(byte[] data) {
            if (data.length != size * 2) {
                throw new IllegalStateException();
            }
            this.data = data;
        }
    }
}
