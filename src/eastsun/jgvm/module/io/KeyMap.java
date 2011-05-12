package eastsun.jgvm.module.io;

/**
 * 一个得到可用系统按键信息已经提供系统按键到GVM按键转换方法的接口
 * @author Eastsun
 * @version 2008-2-21
 */
public interface KeyMap {

    /**
     * 被使用的按键,也就是GVM中只对这些按键有响应
     * @return GVM中使用到的系统按键值,外部操作该数组不会影响到其内部状态
     */
    int[] keyValues();

    /**
     * 将系统原始键值映射到GVM使用的键值
     * @param rawKeyCode 原始键值
     * @return keyCode GVM中使用的键值
     */
    char translate(int rawKeyCode);
}
