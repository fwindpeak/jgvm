package eastsun.jgvm.module;

import eastsun.jgvm.module.ram.Getable;
import eastsun.jgvm.module.ram.Ram;
import eastsun.jgvm.module.ram.RelativeRam;

/**
 * �ı����ģʽ�Ĳ����ӿ� <p>
 * һ��textmodel����ά���ı��������������Լ��ı������������ָ��<p>
 * �����ݵĻ�����Ҫ������Ӧ��ScreenModel<p>
 * ���ڸ���Ĵ󲿷ַ�����Ӧ����ʹ��setScreenModel�������ú�ScreenModel�����<p>
 * �ýӿڱ����˶��ı�����������RAM�Ŀ�ѡʵ��
 * @author Eastsun
 * @version 1.0 2007/2/1
 */
public final class TextModel {

    private ScreenModel screen;
    private Renderable render;
    private int maxRow,  maxCol;
    private int curRow,  curCol;
    private boolean isBigMode;
    private byte[] buffer;
    private Getable getter;
    private RelativeRam ram;

    public TextModel() {
        //..
        super();
    }

    /**
     * ����������ʾ��ScreenModel,�����ʵ��ĳ�ʼ��
     * @param screen
     */
    public void setScreenModel(ScreenModel screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must't be null!");
        }
        if (this.screen != screen) {
            this.screen = screen;
            this.buffer = new byte[(screen.getWidth() / 6) * (screen.getHeight() / 13)];
            this.getter = new ByteArrayGetter(buffer);
            this.ram = new ByteArrayRam(buffer, screen);
            this.render = screen.getRender();
        }
        else {
            ram.clear();
        }
        this.curCol = this.curRow = 0;
        this.isBigMode = true;
        this.maxCol = screen.getWidth() / 8;
        this.maxRow = screen.getHeight() / 16;
    }

    /**
     * �õ�������ʾ��ScreenModel
     * @return screenModel
     */
    public ScreenModel getScreenModel() {
        return screen;
    }

    /**
     * �ı��������Ƿ��й�����Ram
     * @return ����true
     */
    public boolean hasRelativeRam() {
        return true;
    }

    /**
     * �õ�����ı��������������Ram,���Խ��䰲װ��RamManager��,��ʹ��LAVA�����ܹ�ֱ�ӷ��ʵ��ı�������
     * @return ram ������Ram,��Ram�������ı�����������ͬ���仯
     * @throws IllegalStateException ���hasRelativeRam()��������false
     * @see RamManager#install(Ram)
     */
    public RelativeRam getTextRam() {
        return ram;
    }

    /**
     * ���ı����������һ��gb2312������ַ�,��ˢ�µ���Ļ
     * @param c
     */
    public void addChar(char c) {
        if (curRow >= maxRow) {
            //����Ѿ�������Ļ,���ı���������������һ��
            textMoveUp();
        }
        if (c > 0xff) {
            //�����һ��gb2312�ַ�
            if (curCol + 1 >= maxCol) {
                //��λ����,ת��һ��
                buffer[maxCol * curRow + curCol] = (byte) 0x20;
                curCol = 0;
                curRow++;
                if (curRow >= maxRow) {
                    textMoveUp();
                }
            }
            buffer[maxCol * curRow + curCol] = (byte) c;
            curCol++;
            buffer[maxCol * curRow + curCol] = (byte) (c >>> 8);
            curCol++;
            if (curCol >= maxCol) {
                curCol = 0;
                curRow++;
            }
            return;
        }
        //��һ�����ֽ��ַ�
        switch (c) {
            case 0x0a:
                curCol = 0;
                curRow++;
                if (curRow >= maxRow) {
                    textMoveUp();
                }
                break;
            case 0x0d:
                break;
            default:
                buffer[maxCol * curRow + curCol] = (byte) c;
                curCol++;
                if (curCol >= maxCol) {
                    curCol = 0;
                    curRow++;
                }
                break;
        }

    }

    /**
     * ˢ����Ļ,ֻ���ǵͰ�λ<p>
     * �Ӹߵ��Ϳ�����Ļ��ÿһ�У�0��ʾ���и��£�1��ʾ���в�����<p>
     * ��mΪ0ʱˢ��ȫ���ı�����������Ļ<p>
     * @param m ˢ��ģʽ
     */
    public void updateLCD(int m) {
        m &= 0xff;
        //����Ҫˢ��
        if (m == 0xff) {
            return;
        }
        int ox = isBigMode ? 0 : 1;
        int oy = isBigMode ? 0 : 1;
        int dy = isBigMode ? 16 : 13;
        int drawMode = render.DRAW_COPY_TYPE | render.RENDER_GRAPH_TYPE;
        if (isBigMode) {
            drawMode |= render.TEXT_BIG_TYPE;
        }
        if (m == 0) {
            //ˢ������
            render.setDrawMode(render.DRAW_CLEAR_TYPE | render.RENDER_GRAPH_TYPE | render.RENDER_FILL_TYPE);
            render.drawRect(0, 0, screen.getWidth(), screen.getHeight());
            render.setDrawMode(drawMode);
            for (int row = 0; row < maxRow; row++) {
                render.drawString(ox, oy + row * dy, getter, row * maxCol, maxCol);
            }
        }
        else {
            render.setDrawMode(drawMode);
            for (int row = 0; row < maxRow; row++) {
                if ((m & 0x80) == 0) {
                    //ˢ�¸���
                    render.drawString(ox, oy + row * dy, getter, row * maxCol, maxCol);
                }
                m <<= 1;
            }
        }
        screen.fireScreenChanged();
    }

    public void setLocation(int row, int col) {
        if (row >= 0 && row < maxRow) {
            curRow = row;
        }
        if (col >= 0 && col < maxCol) {
            curCol = col;
        }
    }

    /**
     * ����ı�����������������,��SetScreen
     * @param mode mode==0Ϊ������,����ΪС����
     */
    public void setTextMode(int mode) {
        ram.clear();

        curRow = curCol = 0;
        isBigMode = (mode == 0);
        if (isBigMode) {
            maxCol = screen.getWidth() / 8;
            maxRow = screen.getHeight() / 16;
        }
        else {
            maxCol = screen.getWidth() / 6;
            maxRow = screen.getHeight() / 13;
        }
    }

    /**
     * ���ı�������������������һ��,curRow--
     */
    private void textMoveUp() {
        if (curRow <= 0) {
            return;
        }
        int index = 0;
        while (index < maxCol * maxRow - maxCol) {
            buffer[index] = buffer[index + maxCol];
            index++;
        }
        while (index < maxCol * maxRow) {
            buffer[index++] = (byte) 0;
        }
        curRow--;
    }

    private static final class ByteArrayGetter implements Getable {

        private byte[] buffer;

        public ByteArrayGetter(byte[] buffer) {
            this.buffer = buffer;
        }

        public byte getByte(int addr) {
            return buffer[addr];
        }
    }

    /**
     * ʹ��һ���ⲿbyte������Ϊ���ݴ洢��Ram,ע��:�ⲿ�޸Ĵ�byte�����Ӱ�쵽��Ram
     * @author Eastsun
     * @version 2008-1-3
     */
    private static final class ByteArrayRam implements RelativeRam {

        private byte[] buffer;
        private ScreenModel screen;
        private int startAddr;

        public ByteArrayRam(byte[] buffer, ScreenModel screen) {
            this.buffer = buffer;
            this.screen = screen;
        }

        public ScreenModel getScreenModel() {
            return screen;
        }

        public int size() {
            return buffer.length;
        }

        public int getRamType() {
            return Ram.RAM_TEXT_TYPE;
        }

        public int getStartAddr() {
            return startAddr;
        }

        public void setStartAddr(int addr) {
            startAddr = addr;
        }

        public byte getByte(int addr) {
            return buffer[addr - startAddr];
        }

        public void setByte(int addr, byte data) {
            buffer[addr - startAddr] = data;
        }

        public void clear() {
            for (int index = buffer.length - 1; index >= 0; index--) {
                buffer[index] = 0;
            }
        }
    }
}
