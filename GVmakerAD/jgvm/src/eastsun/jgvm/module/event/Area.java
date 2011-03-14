package eastsun.jgvm.module.event;

/**
 * 用于描述一个矩形区域的类,该类为immutable<p>
 * 当getWidth()或getHeight()有一个不大于0时,表示一个空的区域
 * @author Eastsun
 * @version 2008-2-24
 */
public final class Area {

    /**
     * 一个Area常量，其x,y,width,height都为0
     */
    public static final Area EMPTY_AREA = new Area(0, 0, 0, 0);
    private int x,  y,  width,  height;
    private boolean empty;

    /**
     * 构造函数
     * @param x     区域起始x值
     * @param y     区域起始y值
     * @param width 区域的宽度
     * @param height 区域的高度
     */
    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.empty = (width <= 0 || height <= 0);
    }

    /**
     * 判断这个Area是否为空
     * @return empty
     */
    public boolean isEmpty() {
        return empty;
    }

    public String toString() {
        return "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
