package eastsun.jgvm.module;

/**
 * �ṩGVM�����ӿ��Լ�GVM�õ���һЩ��ֵ<p>
 * ע��,����getRawKey������,�����������漰���ļ�ֵ�Ǿ���ת������LAV����ʹ�õ�ֵ,���ֵ��ʵ�ʰ���δ����ͬ<p>
 * @author Eastsun
 */
public interface KeyModel {

    /**
     * �ṩGVM����ļ���ϵͳ����ֵ,��Щ������GVM�������ļ��б��Լ����뷨��ʹ��<p>
     * �����,�����,�������Ǳ���;���ּ��ǿ�ѡ��
     * @author Eastsun
     * @version 2008-2-27
     */
    public interface SysInfo {

        int getLeft();

        int getRight();

        int getUp();

        int getDown();

        int getEnter();

        int getEsc();

        /**
         * �Ƿ�֧��'0'-'9'��10�����ְ���,���ҽ���ϵͳ֧�����ּ�ʱ֧�����������뷨
         * @return �Ƿ�֧��
         */
        boolean hasNumberKey();

        /**
         * �õ�'0'-'9'��10�����ּ��ļ�ֵ
         * @param num ����
         * @return ��ֵ;��haveNumberKey����false,�÷���ֵδ����
         */
        int getNumberKey(int num);
    }

    /**
     * �ͷŰ���key,��ʹ�ü���������
     * @param key
     */
    void releaseKey(char key);

    char checkKey(char key);

    /**
     * ����ֱ���м�����
     * @return key
     * @throws InterruptedException ��������ڼ��̱߳��ж�
     * @see #getRawKey()
     */
    char getchar() throws InterruptedException;

    /**
     * �����ǰ�м�����,���øü�ֵ����ǰ�����־;���򷵻�0
     * @return key
     */
    char inkey();

    /**
     * ��getChar��������,�����ص���δ�������ԭʼkeyֵ<p>
     * @return rawKey
     * @throws InterruptedException �����ڼ��̱߳��ж�
     */
    int getRawKey() throws InterruptedException;

    /**
     * �õ�һЩ�����ϵͳ����ֵ,��Щֵ����fileList�����뷨֮��
     */
    SysInfo getSysInfo();
}
