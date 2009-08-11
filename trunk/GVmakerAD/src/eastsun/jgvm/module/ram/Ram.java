package eastsun.jgvm.module.ram;

/**
 * GVM�����ڴ�ģ��ĸ��ӿ�.
 * ע��,��ĳЩ�ӽӿڿ��ܻ�����setByte()��ʹ��
 * @author Eastsun
 */
public interface Ram extends Accessable {

    /**
     * ����ʱ�ڴ�����
     */
    public static final int RAM_RUNTIME_TYPE = 0x01;
    /**
     * �Դ��ڴ�����
     */
    public static final int RAM_GRAPH_TYPE = 0x02;
    /**
     * ��Ļ��������
     */
    public static final int RAM_BUFFER_TYPE = 0x04;
    /**
     * �ı�����������
     */
    public static final int RAM_TEXT_TYPE = 0x08;
    /**
     * �ַ����ڴ�����
     */
    public static final int RAM_STRING_TYPE = 0x10;

    /**
     * ����ڴ�Ĵ�С,���ֽ�������
     * @return size
     */
    int size();

    /**
     * �õ��ڴ������,ֻ����RAM_RUNTIME_TYPE,RAM_GRAPH_TYPE,RAM_BUFFER_TYPE,RAM_TEXT_TYPE,RAM_STRING_TYPE������֮һ
     * @return type
     */
    int getRamType();

    /**
     * ����ڴ�Ŀ�ʼ��ַ
     * @return addr
     */
    int getStartAddr();

    /**
     * �����ڴ�Ŀ�ʼ��ַ,��RamManager����
     * @param addr ��ʼ��ַ
     */
    void setStartAddr(int addr);

    /**
     * ��ȡָ����ַ��һ���ֽ�
     * @param addr ��ַ
     * @return data
     * @throws IndexOutOfBoundsException �ڴ��Խ��
     */
    byte getByte(int addr) throws IndexOutOfBoundsException;

    /**
     * ����ָ����ַ������
     * @param addr ��ַ
     * @param data ֵ
     * @throws IndexOutOfBoundsException �ڴ�дԽ��
     */
    void setByte(int addr, byte data) throws IndexOutOfBoundsException;

    /**
     * ȫ���ڴ�����
     */
    void clear();
}
