package eastsun.jgvm.module;

/**
 * 输入法接口<p>
 * 注意: 不同的JGVM实例应该使用不同的InputMethod实例
 * @author Eastsun
 * @version 2008-2-21
 */
public interface InputMethod {

    /**
     * 默认输入英文字母
     */
    public static final int ENGLISH_MODE = 0;
    /**
     * 默认输入为数字
     */
    public static final int NUMBER_MODE = 1;
    /**
     * 默认输入为汉字
     */
    public static final int GB2312_MODE = 2;
    /**
     * 保持之前的输入模式
     */
    public static final int DEFAULT_MODE = 3;

    /**
     * 通过接受键盘操作并适当反馈信息到屏幕而得到用户输入的信息<p>
     * 该方法使用屏幕底部12行的内容用于反馈信息
     * @param key 用于获得用户按键
     * @param screen 用于反馈操作过程中的信息
     * @return 一个gb2312编码的字符
     * @throws InterruptedException 期间线程被中断
     */
    public char getWord(KeyModel key, ScreenModel screen) throws InterruptedException;
    
    /**
     * 设置该输入法的默认输入模式
     * @param mode 输入模式
     * @return 之前使用的输入模式
     */
    public int setMode(int mode);
}
