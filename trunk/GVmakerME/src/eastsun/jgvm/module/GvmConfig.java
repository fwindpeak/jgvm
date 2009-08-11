package eastsun.jgvm.module;

/**
 * GVM�����ò���,����ָ��GVMʵ���ĸ������,immutable
 * @author Eastsun
 * @version 1.0 2008/2/1
 */
public final class GvmConfig {

    private int runtimeRamSize,  stringRamSize,  stackSize,  version;

    /**
     * Ĭ�ϵ�JLVM����
     */
    public GvmConfig() {
        this(0x6000, 1024, 512, 0x10);
    }

    /**
     * ���캯��
     * @param runtimeRamSize �����ڴ��С
     * @param stringRamSize �ַ����ڴ��С
     * @param stackSize     ջ��С
     * @param version GVM�İ汾
     */
    public GvmConfig(int runtimeRamSize, int stringRamSize, int stackSize, int version) {
        this.runtimeRamSize = runtimeRamSize;
        this.stringRamSize = stringRamSize;
        this.stackSize = stackSize;
        this.version = version;
    }

    public int stringRamSize() {
        return stringRamSize;
    }

    public int stackSize() {
        return stackSize;
    }

    public int runtimeRamSize() {
        return runtimeRamSize;
    }

    /**
     * �õ�GVM�İ汾
     * @return version
     */
    public int version() {
        return version;
    }

    public String toString() {
        return "[Version: 0x" + Integer.toHexString(version) +
                ",runtimeRamSize: " + runtimeRamSize +
                ",stringRamSize: " + stringRamSize +
                ", stackSize: " + stackSize + "]";
    }
}
