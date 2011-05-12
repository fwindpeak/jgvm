package eastsun.jgvm.module;

import eastsun.jgvm.module.ram.Getable;
import eastsun.jgvm.module.ram.Setable;

/**
 * ��ͼ�ӿ�,ͨ���ýӿ�����Ļ�򻺳�������ͼ��<p>
 * ע��:����refresh�����ἤ��fireScreenChanged������,���������������Զ�����fireScreenChanged
 * @author Eastsun
 * @version 1.0 2008/1/21
 */
public interface Renderable {

    /**
     * ʹ�ô�����ģʽ,��drawChar����Ч
     */
    public static final int TEXT_BIG_TYPE = 0x80;
    /**
     * ֱ������Ļ��ͼ
     */
    public static final int RENDER_GRAPH_TYPE = 0x40;
    /**
     * ��Լ���ͼ��,�Ƿ����
     */
    public static final int RENDER_FILL_TYPE = 0x10;
    /**
     * ͼ��ȡ��
     */
    public static final int RENDER_XNOT_TYPE = 0x08;
    /**
     * ͸��copy,0Ϊ͸������<p>
     * ע��,0~6����
     */
    public static final int DRAW_TCOPY_TYPE = 6;
    /**
     * xor
     */
    public static final int DRAW_XOR_TYPE = 5;
    /**
     * and
     */
    public static final int DRAW_AND_TYPE = 4;
    /**
     * or
     */
    public static final int DRAW_OR_TYPE = 3;
    /**
     * not
     */
    public static final int DRAW_NOT_TYPE = 2;
    /**
     * copy,����ͼ��ʱ��˼Ϊcopy,���Ƽ���ͼ��ʱ����ǰ��ɫ��ͼ
     */
    public static final int DRAW_COPY_TYPE = 1;
    /**
     * clear,�Ի��Ƽ���ͼ����Ч,ʹ�ñ���ɫ��ͼ
     */
    public static final int DRAW_CLEAR_TYPE = 0;

    /**
     * ���û�������,���ܰ������/�����,����/��ɫ,etc.
     * @param m ��Ҫ���õ�����
     */
    public void setDrawMode(int m);

    /**
     * ����һ����0��β���ַ���
     */
    public void drawString(int x, int y, Getable source, int addr);

    /**
     * ���ƴӵ�ַaddr��ʼ��length���ַ� 
     */
    public void drawString(int x, int y, Getable source, int addr, int length);

    /**
     * ���ƾ���
     */
    public void drawRect(int x0, int y0, int x1, int y1);

    /**
     * ������Բ
     */
    public void drawOval(int x, int y, int a, int b);

    /**
     * ����ĻΪ����(x,y)������
     * type=0:2ɫģʽ�»��׵㣬16ɫ��256ɫģʽ���ñ���ɫ����
     *      1:2ɫģʽ�»��ڵ㣬16ɫ��256ɫģʽ����ǰ��ɫ����
     *      2:�����ɫȡ��
     * ���겻����Ļ�ڲ����׳��쳣
     * @param x ������
     * @param y ������
     */
    public void drawPoint(int x, int y);

    /**
     * �õ���Ļ��(x,y)����ĵ�״̬,�÷���������graphMode��ֵ.
     * @return  2ɫģʽ������ǰ׵㷵���㣬�ڵ㷵�ط���ֵ
     */
    public int getPoint(int x, int y);

    /**
     * ����ֱ��
     */
    public void drawLine(int x0, int y0, int x1, int y1);

    /**
     * ����ͼ��,ͼ������������Բ�����Ļ��Χ��
     */
    public void drawRegion(int x, int y, int width, int height, Getable src, int addr);

    /**
     * �õ���Ļ�򻺳�����ͼ������,����x,width�ĵ���λ
     * @throws IndexOutOfBoundsException ���ͼ�񳬳���Ļ��Χ
     * @return length ͼ�����ݳ���
     */
    public int getRegion(int x, int y, int width, int height, Setable dst, int addr);

    /**
     * ��Ч
     */
    public void xdraw(int mode);

    /**
     * �����Ļ������
     */
    public void clearBuffer();

    /**
     * ˢ����Ļ����������Ļ<p>
     * ע��,�÷������Զ�����ScreenModel��fireScreenChanged����
     * @see ScreenModel#fireScreenChanged()
     */
    public void refresh();
}
