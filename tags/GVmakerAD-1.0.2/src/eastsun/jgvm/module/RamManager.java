package eastsun.jgvm.module;

import eastsun.jgvm.module.event.Area;
import eastsun.jgvm.module.ram.Accessable;
import eastsun.jgvm.module.ram.Ram;
import eastsun.jgvm.module.ram.RelativeRam;
import eastsun.jgvm.module.ram.RuntimeRam;
import eastsun.jgvm.module.ram.Stack;
import eastsun.jgvm.module.ram.StringRam;

/**
 * �ڴ����ģ��,Ĭ��ά������ʱ�ڴ����ַ��Ѳ��ṩ��ջ��֧��<p>
 * ����ͨ��install����֧�ֶ��Դ����ı��������Ŀ�ѡ֧��<p>
 * GVM����ͨ��RamManager�ṩ���ڴ��д������������Ram,������Ҫ֪������ľ���ṹ<p>
 * @version 2.0 2008/3/19
 * @author Eastsun
 */
public final class RamManager implements Accessable {

    public static final int SIZE_OF_ADDR = 3;
    public static final int SIZE_OF_CHAR = 1;
    public static final int SIZE_OF_INT = 2;
    public static final int SIZE_OF_LONG = 4;
    /**
     * ����ʱ�ڴ�Ŀ�ʼ��ַ
     */
    public static final int START_ADDR = 0x2000;
    private RelativeRam textRam,  graphRam,  bufferRam;
    private ScreenModel screen;
    private RuntimeRam runRam;
    private StringRam strRam;
    private Stack stack;
    private Ram[] rams = new Ram[3];
    private int ramCount;

    public RamManager(RuntimeRam runRam, StringRam strRam, Stack stack) {
        if (runRam == null || strRam == null || stack == null) {
            throw new IllegalArgumentException("param can't be null");
        }
        this.stack = stack;
        install(runRam);
        install(strRam);
    }

    /**
     * ��ͬ��getByte(addr)
     * @param addr ��ַ
     * @return һ���޷���charֵ��ʾһ��byteֵ
     * @see #getByte(int)
     */
    public char getChar(int addr) {
        return (char) (getByte(addr) & 0xff);
    }

    /**
     * ��ָ����ַ��ȡ���ֽ����һ��lava�е�int����
     * @param addr ��ַ
     * @return int
     */
    public short getInt(int addr) {
        return (short) getBytes(addr, SIZE_OF_INT);
    }

    /**
     * ��ָ����ַ��ȡ���ֽ����һ��lava�е��ļ�ָ������
     * @param addr ��ַ
     * @return �ļ�ָ��
     */
    public int getAddr(int addr) {
        return getBytes(addr, SIZE_OF_ADDR);
    }

    /**
     * ��ָ����ַ��ȡ���ֽ����һ��lava�е�long����
     * @param addr ��ַ
     * @return long
     */
    public int getLong(int addr) {
        return getBytes(addr, SIZE_OF_LONG);
    }

    /**
     * ����һ��lava�е�char����,��ͬ��setByte((byte)c)
     * @param addr ��ַ
     * @param c charֵ,�ڰ�λ��Ч
     * @see #setByte(int,byte)
     */
    public void setChar(int addr, char c) {
        setByte(addr, (byte) c);
    }

    /**
     * ����һ��lava�е�int����
     */
    public void setInt(int addr, short i) {
        setBytes(addr, SIZE_OF_INT, i);
    }

    /**
     * ����һ��lava�е��ļ�ָ������
     */
    public void setAddr(int addr, int a) {
        setBytes(addr, SIZE_OF_ADDR, a);
    }

    /**
     * ����һ��lava�е�long����
     */
    public void setLong(int addr, int l) {
        setBytes(addr, SIZE_OF_LONG, l);
    }

    /**
     * ��ָ���ڴ��ȡ����count���ֽ�ƴ��һ������
     * @param addr ��ʼ��ַ
     * @param count �ֽ���
     * @return ��ֵ
     */
    public int getBytes(int addr, int count) {
        int data = 0;
        while (--count >= 0) {
            data <<= 8;
            data |= (getByte(addr + count) & 0xff);
        }
        return data;
    }

    /**
     * ��һ����ֵ�������ڴ�������count���ֽ�ֵ<p>
     * ע��:��Щ�ڴ��ַ�п����漰���Դ����Ļ������,�÷���������֪ͨ��Ӧ���<p>
     *      Ӧ���Լ�����intersectWithGraph()�����жϲ�������Ӧ��Ӧ
     * @see #intersectWithGraph(int,int)
     */
    public void setBytes(int addr, int count, int data) {
        while (--count >= 0) {
            setByte(addr++, (byte) data);
            data >>>= 8;
        }
    }

    /**
     * �õ�stringRam
     */
    public StringRam getStringRam() {
        return strRam;
    }

    /**
     * �õ���JLVMʹ�õ�Stack
     * @return stack
     */
    public Stack getStack() {
        return stack;
    }

    /**
     * �õ���JLVMʹ�õ�RuntimeRam
     * @return runtimeRam
     */
    public RuntimeRam getRuntimeRam() {
        return runRam;
    }

    /**
     * �õ���start��ʼend�������ڴ��ַ����Ļ�ϵ���ʾ����<p>
     * @param start ��ʼ��ַ,����
     * @param end ������ַ,������
     * @return ���ڴ���������Ļ��ʾ�������С���Ǿ���;����������ִ����ڴ�鲻���Դ��ཻ,�򷵻�һ���յ�Area
     */
    public Area intersectWithGraph(int start, int end) {
        if (graphRam == null) {
            return Area.EMPTY_AREA;
        }
        if (start >= graphRam.getStartAddr() + graphRam.size() || end <= graphRam.getStartAddr()) {
            return Area.EMPTY_AREA;
        }
        if (start < graphRam.getStartAddr()) {
            start = graphRam.getStartAddr();
        }
        if (end > graphRam.getStartAddr() + graphRam.size()) {
            end = graphRam.getStartAddr() + graphRam.size();
        }
        start <<= 3;
        end = (end << 3) - 1;
        int y1 = start / screen.getWidth();
        int x1 = start % screen.getWidth();
        int y2 = end / screen.getWidth();
        int x2 = end % screen.getWidth();
        if (y1 == y2) {
            return new Area(x1, y1, x2 - x1 + 1, 1);
        }
        else {
            return new Area(0, y1, screen.getWidth(), y2 - y1 + 1);
        }
    }

    /**
     * ��ȡָ���ڴ��ַһ�ֽ�����,����byte����
     * @param addr ��ַ
     * @return byte����
     */
    public byte getByte(int addr) {
        //Notice: ��ʵ����resetRamAddress��ʵ�ַ�ʽ�й�
        if (addr >= runRam.getStartAddr()) {
            return runRam.getByte(addr);
        }
        if (addr >= strRam.getStartAddr()) {
            return strRam.getByte(addr);
        }
        for (int index = ramCount - 1; index >= 0; index--) {
            Ram ram = rams[index];
            if (addr >= ram.getStartAddr()) {
                return ram.getByte(addr);
            }
        }
        throw new IndexOutOfBoundsException("�ڴ��Խ��:" + addr);
    }

    /**
     * ����ַΪaddr��������Ϊb
     * ע��:��Щ�ڴ��ַ�п����漰���Դ����Ļ������,�÷���������֪ͨ��Ӧ���<p>
     *      Ӧ���Լ�����intersectWithGraph()�����жϲ�������Ӧ��Ӧ
     * @param addr ��ַ
     * @param b ����
     * @throws IndexOutOfBoundsException �ڴ�дԽ��
     */
    public void setByte(int addr, byte b) {
        //Notice: ��ʵ����resetRamAddress��ʵ�ַ�ʽ�й�
        if (addr >= runRam.getStartAddr()) {
            runRam.setByte(addr, b);
            return;
        }
        if (addr >= strRam.getStartAddr()) {
            strRam.setByte(addr, b);
            return;
        }
        for (int index = ramCount - 1; index >= 0; index--) {
            Ram ram = rams[index];
            if (addr >= ram.getStartAddr()) {
                ram.setByte(addr, b);
                return;
            }
        }
        throw new IndexOutOfBoundsException("�ڴ�дԽ��:" + addr);
    }

    /**
     * �������ڴ�ģ������
     */
    public void clear() {
        runRam.clear();
        strRam.clear();
        stack.clear();
        for (int index = 0; index < ramCount; index++) {
            rams[index].clear();
        }

    }

    /**
     * ����������ڴ�ģ��,ÿ�����͵��ڴ����ֻ�ܰ�װһ��.<p>
     * ramΪnull�����׳��쳣,�Ҳ���ı�RamManager���κ�״̬
     * @param ram ��Ҫ��װ���ڴ�
     * @throws IllegalStateException �Ѿ���װ���������͵��ڴ�
     * @see #uninstall
     */
    public void install(Ram ram) {
        if (ram == null) {
            return;
        }
        switch (ram.getRamType()) {
            case Ram.RAM_RUNTIME_TYPE:
                if (runRam != null) {
                    throw new IllegalStateException("Runtime Ram was installed!");
                }
                runRam = (RuntimeRam) ram;
                break;

            case Ram.RAM_GRAPH_TYPE:
                if (graphRam != null) {
                    throw new IllegalStateException("Graph Ram was installed!");
                }
                graphRam = (RelativeRam) ram;
                screen = graphRam.getScreenModel();
                break;

            case Ram.RAM_BUFFER_TYPE:
                if (bufferRam != null) {
                    throw new IllegalStateException("Buffer Ram was installed!");
                }
                bufferRam = (RelativeRam) ram;
                break;

            case Ram.RAM_STRING_TYPE:
                if (strRam != null) {
                    throw new IllegalStateException("String Ram was installed!");
                }
                strRam = (StringRam) ram;
                break;

            case Ram.RAM_TEXT_TYPE:
                if (textRam != null) {
                    throw new IllegalStateException("Text Ram was installed!");
                }
                textRam = (RelativeRam) ram;
                break;

        }
        resetRamAddress();

    }

    /**
     * ж��ram,�����û�а�װ��ram,��ʲôҲ���ᷢ��
     * @param type ��Ҫж�ص��ڴ�����
     */
    public void uninstall(int type) {
        Ram ram = null;
        switch (type) {
            case Ram.RAM_RUNTIME_TYPE:
                ram = runRam;
                runRam = null;
                break;

            case Ram.RAM_GRAPH_TYPE:
                ram = graphRam;
                graphRam = null;
                break;

            case Ram.RAM_BUFFER_TYPE:
                ram = bufferRam;
                bufferRam = null;
                break;

            case Ram.RAM_STRING_TYPE:
                ram = strRam;
                strRam = null;
                break;

            case Ram.RAM_TEXT_TYPE:
                ram = textRam;
                textRam = null;
                break;

        }
        if (ram != null) {
            resetRamAddress();
        }

    }

    private void resetRamAddress() {

        // �ڴ�ģ�鲼��: 0x2000��ʼΪRuntimeRam
        //                      0x0000��ʼ����ΪgraphRam,bufferRam,textRam,stringRam
        // Ӧ�ð���ʹ�ø��ʴ�С��������,��rams�еĴ���Ӧ�ð���ʼ��ַ��С��������
        // Notice: ����޸�,����ͬʱ��Ҫ�޸�setByte��getByte����

        //��ֹ��Ч����
        for (int index = 0; index < rams.length; index++) {
            rams[index] = null;
        }

        ramCount = 0;
        if (runRam != null) {
            //����ӵ�rams��
            runRam.setStartAddr(START_ADDR);
        }

        int startAddr = 0;
        if (graphRam != null) {
            graphRam.setStartAddr(startAddr);
            startAddr += graphRam.size();
            rams[ramCount++] = graphRam;
        }

        if (bufferRam != null) {
            bufferRam.setStartAddr(startAddr);
            startAddr += bufferRam.size();
            rams[ramCount++] = bufferRam;
        }

        if (textRam != null) {
            textRam.setStartAddr(startAddr);
            startAddr += textRam.size();
            rams[ramCount++] = textRam;
        }

        if (strRam != null) {
            strRam.setStartAddr(startAddr);
            startAddr += strRam.size();
        //strRam��runtimeRam����rams֮��
        //rams[ramCount++] = strRam;
        }

        if (startAddr > START_ADDR) {
            throw new IllegalStateException("��,�ڴ�ģ����ô��!");
        }
    }
}
