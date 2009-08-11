package eastsun.jgvm.module.io;

/**
 * һ���õ�����ϵͳ������Ϣ�Ѿ��ṩϵͳ������GVM����ת�������Ľӿ�
 * @author Eastsun
 * @version 2008-2-21
 */
public interface KeyMap {

    /**
     * ��ʹ�õİ���,Ҳ����GVM��ֻ����Щ��������Ӧ
     * @return GVM��ʹ�õ���ϵͳ����ֵ,�ⲿ���������鲻��Ӱ�쵽���ڲ�״̬
     */
    int[] keyValues();

    /**
     * ��ϵͳԭʼ��ֵӳ�䵽GVMʹ�õļ�ֵ
     * @param rawKeyCode ԭʼ��ֵ
     * @return keyCode GVM��ʹ�õļ�ֵ
     */
    char translate(int rawKeyCode);
}
