package eastsun.jgvm.module.ram;

import eastsun.jgvm.module.ScreenModel;

/**
 * �����ڴ�ģ��<p>
 * �����ڴ�ֱ�ӻ�����ScreenModel�����,������Ҳ��ScreenModel���Ӧ.
 * @author Eastsun
 * @see ScreenModel
 */
public interface RelativeRam extends Ram {

    /**
     * �õ���Ram��Ӧ��ScreenModel
     * @return screen
     */
    ScreenModel getScreenModel();
}
