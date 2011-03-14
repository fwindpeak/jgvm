package eastsun.jgvm.module.ram;

/**
 * ����ʱ�ڴ�,�ṩ����ʱ�����Ϣ
 * @author Eastsun
 */
public final class RuntimeRam implements Ram {

    private int startAddr,  regionStartAddr,  regionEndAddr;
    private byte[] buffer;

    public RuntimeRam(int size) {
        buffer = new byte[size];
    }

    /**
     * �õ���ǰ���ں���ʹ���ڴ��������ʼ��ַ
     * @return startAddr
     */
    public int getRegionStartAddr() {
        return regionStartAddr;
    }

    /**
     * ���õ�ǰ����ʹ���ڴ����ʼ��ַ
     * @param addr ��ʼ��ַ
     */
    public void setRegionStartAddr(int addr) {
        regionStartAddr = addr;
    }

    /**
     * �õ���ǰ���ں���ʹ���ڴ�����Ľ�����ַ(������)
     * @return startAddr
     */
    public int getRegionEndAddr() {
        return regionEndAddr;
    }

    /**
     * ���õ�ǰ����ʹ���ڴ�Ľ�����ַ
     * @param addr ��ʼ��ַ
     */
    public void setRegionEndAddr(int addr) {
        regionEndAddr = addr;
    }

    public int size() {
        return buffer.length;
    }

    public int getRamType() {
        return Ram.RAM_RUNTIME_TYPE;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(int addr) {
        startAddr = addr;
    }

    public byte getByte(int addr) {
        return buffer[addr - startAddr];
    }

    public void setByte(int addr, byte data) {
        buffer[addr - startAddr] = data;
    }

    public void clear() {
        for (int index = buffer.length - 1; index >= 0; index--) {
            buffer[index] = 0;
        }
    }
}
