package eastsun.jgvm.module.event;

import eastsun.jgvm.module.ScreenModel;
/**
 * GVM��Ļ״̬������
 * @author Eastsun
 */
public interface ScreenChangeListener {

    /**
     * ��Ļ״̬�����˱仯ʱ���ô˷���
     * @param screenModel �����仯��ScreenModel
     * @param area �����仯������,������ȷ������Ļ��Χ��
     */
    void screenChanged(ScreenModel screenModel, Area area);
}
