package eastsun.jgvm.module;

import eastsun.jgvm.module.ram.Getable;
import eastsun.jgvm.module.ram.Setable;

/**
 * �ļ�ϵͳ,ʵ��GVM�еĸ����ļ���������<p>
 * @author Eastsun
 */
public interface FileModel {

    /**
     * �ļ�������󳤶�(ָת��Ϊgb2312�����ĳ���)
     */
    public static final int FILE_NAME_LENGTH = 18;

    /**
     * �ı䵱ǰ����Ŀ¼
     */
    public boolean changeDir(Getable source, int addr);

    /**
     * �����ļ���
     */
    public boolean makeDir(Getable source, int addr);

    /**
     * �õ���ǰĿ¼�µ��ļ�����
     * @return �ļ��и���
     */
    public int getFileNum();

    /**
     * �õ���ǰĿ¼�µ�start����ʼ��num���ļ���,���浽names��
     * @param names ���ڱ����ļ�����String����
     * @param start ��ʼ�ļ���
     * @param num   ����
     * @return      ʵ�ʵõ��ĸ���,�����,����-1
     */
    public int listFiles(String[] names, int start, int num);

    /**
     * ���ļ�
     * @param source �������ݵ�Դ
     * @param fileName �ļ�����ʼ��ַ
     * @param openMode ��ģʽ��ʼ��ַ
     * @return �ļ���,��8λ��Ч
     */
    public int fopen(Getable source, int fileName, int openMode);

    /**
     * �ر��ļ�
     * @param fp ��Ҫ�رյ��ļ���
     */
    public void fclose(int fp);

    /**
     * ��ָ���ļ���ȡһ��byte
     * @param fp �ļ���
     * @return ��ȡ���ַ�,�Ͱ�λ��Ч;��ʧ�ܷ���-1
     */
    public int getc(int fp);

    /**
     * д��һ���ַ���ָ���ļ�
     * @param c Ҫд����ַ�,�Ͱ�λ��Ч
     * @param fp �ļ���
     * @return д����ַ�,��ʧ�ܷ���-1
     */
    public int putc(int c, int fp);

    /**
     * ��ȡһ������
     * @param addr ������dest�б���Ŀ�ʼ��ַ
     * @param dest �������ݵ�Setable
     * @param size ��Ҫ��ȡ���ݵĳ���
     * @param fp   �ļ���
     * @return     ��ȡ���ݵĳ���,�緢��IO��������ļ���β����0
     */
    public int fread(Setable dest, int addr, int size, int fp);

    /**
     * д��һ������
     * @param source ��Ҫд����������ڵ�Getable
     * @param addr   ������source�еĿ�ʼ��ַ
     * @param size   д�����ݵĳ���
     * @param fp     �ļ���
     * @return       д�����ݵĳ���,�緢��IO����������ļ���β����0
     */
    public int fwrite(Getable source, int addr, int size, int fp);

    /**
     * ɾ���ļ�
     */
    public boolean deleteFile(Getable source, int addr);

    /**
     * �ļ�ָ�붨λ
     * @param fp     �ļ���
     * @param offset ������
     * @param base   ����
     * @return       ��λ����ļ�ָ��,��������-1
     */
    public int fseek(int fp, int offset, int base);

    /**
     * �õ��ļ�ָ��
     * @param fp �ļ���
     * @return   �ļ�ָ��
     */
    public int ftell(int fp);

    /**
     * ����ļ��Ƿ��ѽ���
     * @param fp �ļ���
     * @return   true,����ѽ���;����false
     */
    public boolean feof(int fp);

    /**
     * �ļ�ָ�븴λ
     * @param fp �ļ���
     */
    public void rewind(int fp);

    /**
     * �ر������ļ�,�ͷ�ռ�õ���Դ
     */
    public void dispose();
}
