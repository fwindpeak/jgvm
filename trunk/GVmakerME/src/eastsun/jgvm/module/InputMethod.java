package eastsun.jgvm.module;

/**
 * ���뷨�ӿ�<p>
 * ע��: ��ͬ��JGVMʵ��Ӧ��ʹ�ò�ͬ��InputMethodʵ��
 * @author Eastsun
 * @version 2008-2-21
 */
public interface InputMethod {

    /**
     * Ĭ������Ӣ����ĸ
     */
    public static final int ENGLISH_MODE = 0;
    /**
     * Ĭ������Ϊ����
     */
    public static final int NUMBER_MODE = 1;
    /**
     * Ĭ������Ϊ����
     */
    public static final int GB2312_MODE = 2;
    /**
     * ����֮ǰ������ģʽ
     */
    public static final int DEFAULT_MODE = 3;

    /**
     * ͨ�����ܼ��̲������ʵ�������Ϣ����Ļ���õ��û��������Ϣ<p>
     * �÷���ʹ����Ļ�ײ�12�е��������ڷ�����Ϣ
     * @param key ���ڻ���û�����
     * @param screen ���ڷ������������е���Ϣ
     * @return һ��gb2312������ַ�
     * @throws InterruptedException �ڼ��̱߳��ж�
     */
    public char getWord(KeyModel key, ScreenModel screen) throws InterruptedException;
    
    /**
     * ���ø����뷨��Ĭ������ģʽ
     * @param mode ����ģʽ
     * @return ֮ǰʹ�õ�����ģʽ
     */
    public int setMode(int mode);
}
