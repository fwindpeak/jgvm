package eastsun.jgvm.module;

import eastsun.jgvm.module.event.Area;
import eastsun.jgvm.module.event.ScreenChangeListener;
import eastsun.jgvm.module.ram.RelativeRam;

/**
 * ��Ļģ��,��ģ�鱣�����Դ漰���������ʵĿ�ѡʵ��
 * @version 0.7 2007/1/21  �޸����ⲿ�õ���Ļ���ݵĽӿ�<p>
 *               2008/2/24  �ٴ��޸Ļ����Ļ���ݵĽӿ�,��ҪĿ�����Ż�ˢ���ٶ�
 * @author Eastsun
 */
public abstract class ScreenModel {

    /**
     * ��Ļ���
     */
    public static final int WIDTH = 160;
    /**
     * ��Ļ�߶�
     */
    public static final int HEIGHT = 80;

    /**
     * ����һ��ScreenModelʵ��
     */
    public static ScreenModel newScreenModel() {
        return new ScreenModelImp();
    }
    private ScreenChangeListener[] lis;

    protected ScreenModel() {
        lis = new ScreenChangeListener[0];
    }

    /**
     * ΪScreen��Ӽ�����,��Screen��Graph״̬�����仯ʱ�����仯ʱ�������¼�
     * @param listener �¼�������
     */
    public final void addScreenChangeListener(ScreenChangeListener listener) {
        ScreenChangeListener[] oldValue = lis;
        lis = new ScreenChangeListener[oldValue.length + 1];
        int index = 0;
        for (; index < oldValue.length; index++) {
            lis[index] = oldValue[index];
        }
        lis[index] = listener;
    }

    /**
     * ֪ͨ��Ļ������
     */
    public final void fireScreenChanged() {
        for (int index = 0; index < lis.length; index++) {
            lis[index].screenChanged(this, getChangedArea());
        }
        refreshArea();
    }

    /**
     * �õ���Ļ�Ŀ��
     * @return width
     */
    public final int getWidth() {
        return WIDTH;
    }

    /**
     * �õ���Ļ�ĸ߶�
     * @return height
     */
    public final int getHeight() {
        return HEIGHT;
    }

    /**
     * �������ڱ�ʾ�ڰ׵���ɫ��RGBֵ
     * @param black ��
     * @param white ��
     */
    public abstract void setColor(int black, int white);

    /**
     * �õ���Ļrgb����<p>
     * Լ��:<p>
     *     1. buffer�ĳ���Ӧ��С��getWidth()*getHeight()*rate*rate<p>
     *     2. ��buffer������ݵ�ʱ��Ὣbuffer����һ��(getWidth()*rate)*(getHeight()*rate)(��ֻû����ת�����,��ת���������)<p>
     *        Ȼ�󽫷����仯�������������䵽buffer����Ӧλ��,�����Ǵ����鿪ʼ���,Ҳ��һ�����������<p>
     *  2008.2.24�޸�
     * @param buffer ���ڱ�����Ļrgb���ݵ�int����,�䳤��Ӧ��С�� offset+rate*getWidth()*getHeight()
     * @param area   ��Ҫ�ĵ�ͼ����������Ļ�ķ�Χ
     * @param rate   ͼ��ķŴ����
     * @param circ   ����Ϊ0,1,2,3.�ֱ��ʾ����ת,��ʱ����ת90��,180��,270��
     * @return buffer����
     */
    public abstract int[] getRGB(int[] buffer, Area area, int rate, int circ);

    /**
     * �Ƿ�����������Դ��Լ��Դ滺����Ram
     * @return ���ҽ�����Ļ��СΪ160*80ʱ����true
     */
    public abstract boolean hasRelativeRam();

    /**
     * �õ������Ļ�Դ��������Ram,���Խ��䰲װ��RamManager��,��ʹ��LAVA�����ܹ�ֱ�ӷ����Դ�
     * @return ram �õ�������Ram,��Ram��������Screen���ݱ���ͬ���仯
     * @throws IllegalStateException ���hasRelativeRam()����false
     * @see #hasRelativeRam()
     * @see RamManager#install(Ram)
     */
    public abstract RelativeRam getGraphRam();

    /**
     * �õ�����Ļ�������������Ram,���Խ��䰲װ��RamManager��,��ʹ��LAVA�����ܹ�ֱ�ӷ�����Ļ������
     * @return ram �õ�������Ram,��Ram��������Screen���������ݱ���ͬ���仯
     * @throws IllegalStateException ���hasRelativeRam()����false
     * @see #hasRelativeRam()
     * @see RamManager#install(Ram)
     */
    public abstract RelativeRam getBufferRam();

    /**
     * �����Ļ��ͼ�ӿ�
     * @return render
     */
    public abstract Renderable getRender();

    /**
     * ���������仯��Χ��area,ʹ����¼�¼�仯��Χ<p>
     * �⼸������changedArea�ķ���ֻӦ����JGVM�ڲ������ʹ��
     * @see #getChangedArea()
     * @see #getRGB(int[],Area,int,int)
     */
    abstract void refreshArea();

    /**
     * �õ������ı����Ļ��Χ<p>
     * 2008.2.24���
     * @return area
     */
    abstract Area getChangedArea();

    /**
     * ���ı��������add
     * @param add �µĸĶ�������
     */
    abstract void addToChangedArea(Area add);
}
