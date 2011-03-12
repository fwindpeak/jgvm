package eastsun.jgvm.module;

/**
 * 提供GVM按键接口以及GVM用到的一些键值<p>
 * 注意,除了getRawKey方法外,其他方法中涉及到的键值是经过转换便于LAV程序使用的值,这个值与实际按键未必相同<p>
 * @author Eastsun
 */
public interface KeyModel {

    /**
     * 提供GVM必须的几个系统按键值,这些按键在GVM中用于文件列表以及输入法中使用<p>
     * 方向键,输入键,跳出键是必须;数字键是可选的
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
         * 是否支持'0'-'9'这10个数字按键,当且仅当系统支持数字键时支持完整的输入法
         * @return 是否支持
         */
        boolean hasNumberKey();

        /**
         * 得到'0'-'9'这10个数字键的键值
         * @param num 数字
         * @return 键值;如haveNumberKey返回false,该返回值未定义
         */
        int getNumberKey(int num);
    }

    /**
     * 释放按键key,即使该键正被按下
     * @param key
     */
    void releaseKey(char key);

    char checkKey(char key);

    /**
     * 阻塞直到有键按下
     * @return key
     * @throws InterruptedException 如果阻塞期间线程被中断
     * @see #getRawKey()
     */
    char getchar() throws InterruptedException;

    /**
     * 如果当前有键按下,则获得该键值并标记按键标志;否则返回0
     * @return key
     */
    char inkey();

    /**
     * 与getChar功能类似,但返回的是未经处理的原始key值<p>
     * @return rawKey
     * @throws InterruptedException 阻塞期间线程被中断
     */
    int getRawKey() throws InterruptedException;

    /**
     * 得到一些必须的系统按键值,这些值用于fileList与输入法之用
     */
    SysInfo getSysInfo();
}
