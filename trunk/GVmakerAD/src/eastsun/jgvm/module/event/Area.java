package eastsun.jgvm.module.event;

/**
 * ��������һ�������������,����Ϊimmutable<p>
 * ��getWidth()��getHeight()��һ��������0ʱ,��ʾһ���յ�����
 * @author Eastsun
 * @version 2008-2-24
 */
public final class Area {

    /**
     * һ��Area��������x,y,width,height��Ϊ0
     */
    public static final Area EMPTY_AREA = new Area(0, 0, 0, 0);
    private int x,  y,  width,  height;
    private boolean empty;

    /**
     * ���캯��
     * @param x     ������ʼxֵ
     * @param y     ������ʼyֵ
     * @param width ����Ŀ��
     * @param height ����ĸ߶�
     */
    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.empty = (width <= 0 || height <= 0);
    }

    /**
     * �ж����Area�Ƿ�Ϊ��
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
