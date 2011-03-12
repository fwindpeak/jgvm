package eastsun.jgvm.module.event;

import eastsun.jgvm.module.ScreenModel;
/**
 * GVM屏幕状态监听器
 * @author Eastsun
 */
public interface ScreenChangeListener {

    /**
     * 屏幕状态发生了变化时调用此方法
     * @param screenModel 发生变化的ScreenModel
     * @param area 发生变化的区域,该区域确保在屏幕范围内
     */
    void screenChanged(ScreenModel screenModel, Area area);
}
