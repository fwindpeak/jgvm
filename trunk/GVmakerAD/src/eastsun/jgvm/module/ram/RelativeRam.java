package eastsun.jgvm.module.ram;

import eastsun.jgvm.module.ScreenModel;

/**
 * 关联内存模块<p>
 * 这种内存直接或间接与ScreenModel相关联,其内容也与ScreenModel相对应.
 * @author Eastsun
 * @see ScreenModel
 */
public interface RelativeRam extends Ram {

    /**
     * 得到此Ram对应的ScreenModel
     * @return screen
     */
    ScreenModel getScreenModel();
}
