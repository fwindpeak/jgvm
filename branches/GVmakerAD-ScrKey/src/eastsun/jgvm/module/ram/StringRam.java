package eastsun.jgvm.module.ram;

import eastsun.jgvm.module.LavApp;

/**
 * �ַ������ڴ�ģ��
 * @author Eastsun
 * @version 1.0 2008/1/19
 */
public final class StringRam implements Ram {

    private byte[] buffer;
    private int offset,  startAddr;

    public StringRam(int size) {
        buffer = new byte[size];
        offset = 0;
    }

    /**
     * ��lav�ļ��ж�ȡһ����0��β���ַ�������
     * @param source ����Դ
     * @return addr ������ݱ�����StringRam�еĵ�ַ
     */
    public int addString(LavApp source) {
        int addr = offset + startAddr;
        byte b;
        do {
            b = (byte) source.getChar();
            buffer[offset++] = b;
        } while (b != 0);
        if (offset >= buffer.length * 3 / 4) {
            offset = 0;
        }
        return addr;
    }

    /**
     * ��Ram������ֱ��д�ڴ�,ֻ��ͨ��addString()����������д����
     * @throws IndexOutOfBoundsException ���ô˷��������׳����쳣
     * @see #addString(LavApp)
     */
    public void setByte(int addr, byte data) {
        throw new IndexOutOfBoundsException("���ַ��������޸�: " + addr);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return buffer.length;
    }

    /**
     * {@inheritDoc}
     */
    public int getRamType() {
        return Ram.RAM_STRING_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public int getStartAddr() {
        return startAddr;
    }

    /**
     * {@inheritDoc}
     */
    public void setStartAddr(int addr) {
        startAddr = addr;
    }

    /**
     * {@inheritDoc}
     */
    public byte getByte(int addr) {
        return buffer[addr - startAddr];
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        offset = 0;
    }
}
