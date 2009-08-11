package eastsun.jgvm.plaf;

import eastsun.jgvm.module.io.*;
import eastsun.jgvm.module.InputMethod;
import eastsun.jgvm.module.KeyModel;
import eastsun.jgvm.module.Renderable;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.io.SpellInfo.SpellNode;
import eastsun.jgvm.module.ram.Accessable;
import eastsun.jgvm.module.ram.Getable;
import eastsun.jgvm.module.ram.Setable;
import java.util.Enumeration;

/**
 * �ֻ��ϵ����뷨ʵ��<p>
 * ��ʵ�ֵ�Ŀ������֧��E����ĸ(��Сд),����,����,���ֵ�����.
 * ��ʵ��ͨ��KeyModel.getSysInfo()�õ����ð���,���KeyModel.SysInfo.hasNumberKey()����false,���ʵ��ֱ�ӷ���KeyModel.getchar()<p>
 * ����˵��:<p><hr><blockquote><pre>
 *             �ս������뷨ʱ��״̬��:
 *             �����һ�ΰ���Ϊ����,����,��,����Щ��,ֱ�ӷ�����Щ��ֵ;���Ϊ���ϼ�,�򷵻�F2���ļ�ֵ,���Ϊ�¼�,���л�����ģʽ,����:
 *             1.����������ģʽ��,�����ּ�ֱ�ӷ������������
 *             2.����ĸ����ģʽ�·�����:
 *                    a. ��һ��: ���������0,��ֱ�ӷ��ذ�ǿո�;����ת����һ��
 *                    b. ���������1,����ʾ����ַ���ѡ,������ʾ������Ӧ�Ĵ�Сд��ĸ��ѡ;
 *                       ��ʱʹ����/�¼���ҳ,���Ҽ��ƶ����,����������һ��,���ּ�����ֱ��ѡ�б�ѡ�ַ�����,�������������ѡ��;
 *             3.�ں�������ģʽ�·�����:
 *                    a.��һ��: ����ƴ��,(�����һ�ΰ���Ϊ0��ֱ�ӷ���ȫ�ǿո�,Ϊ����ȫ�Ƿ���ѡ��)�˹��������¼��л���ѡƴ��,����˸�,������������������,�Ҽ��������������һ��
 *                    b.�ڶ���:ѡ����,�˹��������¼���ҳ,���Ҽ��ƶ����,�����ѡ�к���,������������һ��
 * </pre></blockquote><hr><p>
 * @author Eastsun
 * @version 2008-3-18
 */
public class InputMethodME implements InputMethod {

    private int width,  height;
    //��ʾ�������
    private int maxCol;
    private int mode;
    private Accessable graph;
    private ScreenModel screen;
    private Renderable render;
    private KeyModel key;
    private KeyModel.SysInfo info;
    //������ʾ����ʾ����
    private Accessable content;
    private SpellNode[] nodes = new SpellNode[SpellInfo.count() / 2];

    public InputMethodME() {
        mode = GB2312_MODE;
        height = 12;
    }

    public int setMode(int mode) {
        int oldValue = this.mode;
        switch (mode) {
            case ENGLISH_MODE:
            case NUMBER_MODE:
            case GB2312_MODE:
                this.mode = mode;
                break;
        }
        return oldValue;
    }

    public char getWord(KeyModel km, ScreenModel sm) throws InterruptedException {
        info = km.getSysInfo();
        if (!info.hasNumberKey()) {
            return km.getchar();
        }
        //���ڱ�����Ļ����height��״̬
        if (screen != sm || sm.getWidth() != width) {
            screen = sm;
            key = km;
            render = sm.getRender();
            width = sm.getWidth();
            graph = Util.asAccessable(new byte[(width + 7) / 8 * height]);
            maxCol = width / 6;
            content = Util.asAccessable(new byte[maxCol]);
        }
        //������Ļ״̬
        saveScreen(sm, height, graph);
        //��������ģʽ

        char word = 0;
        while (word == 0) {
            switch (mode) {
                case ENGLISH_MODE:
                    word = getEnglish();
                    break;
                case NUMBER_MODE:
                    word = getNumber();
                    break;
                case GB2312_MODE:
                    word = getGB2312();
                    break;
            }
        }
        resumeScreen(sm, height, graph);
        return word;
    }

    /**
     * �÷������ܵķ���ֵ:E����ĸasicc��,���Ƽ�,0(�û��л�������ģʽ)
     */
    private char getEnglish() throws InterruptedException {
        clearContent();
        char word = 0;
        int num = -1, choiceIndex = 0, pageIndex = 0, offset = 4, pageSize = maxCol - offset;
        int yStart = screen.getHeight() - height;
        //'Ӣ'
        content.setByte(0, (byte) 0xd3);
        content.setByte(1, (byte) 0xa2);
        for (;;) {
            //����֮ǰ��״̬
            for (int index = offset; index < maxCol; index++) {
                content.setByte(index, (byte) 0);
            }
            if (num != -1) {
                int start = pageIndex * pageSize;
                for (int index = offset; index < maxCol && start < ENGLISH_ARRAY[num].length; index++) {
                    content.setByte(index, ENGLISH_ARRAY[num][start++]);
                }
            }
            render.setDrawMode(Renderable.RENDER_GRAPH_TYPE);
            render.drawString(0, yStart, content, 0, maxCol);
            render.setDrawMode(Renderable.RENDER_GRAPH_TYPE | Renderable.DRAW_NOT_TYPE | Renderable.RENDER_FILL_TYPE);
            render.drawRect(0, yStart, height - 1, screen.getHeight() - 1);
            if (num != -1) {
                render.drawRect((offset + choiceIndex) * 6, yStart, (offset + choiceIndex) * 6 + 5, screen.getHeight() - 1);
            }
            screen.fireScreenChanged();
            int keyValue = key.getRawKey();
            if (num == -1) {
                //����ǿ��Ƽ�
                if ((word = getCtrlKey(keyValue)) != 0) {
                    break;
                }
                //����Ǹı�����ģʽ����
                if (shiftMode(keyValue)) {
                    break;
                }
                for (int index = 0; index <= 9; index++) {
                    if (keyValue == info.getNumberKey(index)) {
                        num = index;
                        break;
                    }
                }
                if (num != -1) {
                    if (ENGLISH_ARRAY[num].length == 1) {
                        word = (char) (ENGLISH_ARRAY[num][0] & 0xff);
                        break;
                    }
                    pageIndex = choiceIndex = 0;
                }
            }
            else {
                if (keyValue == info.getEsc()) {
                    num = -1;
                    continue;
                }
                if (keyValue == info.getEnter()) {
                    word = (char) (ENGLISH_ARRAY[num][pageIndex * pageSize + choiceIndex] & 0xff);
                    break;
                }
                for (int index = 0; pageIndex * pageSize + index < ENGLISH_ARRAY[num].length && index <= 9; index++) {
                    if (keyValue == info.getNumberKey(index)) {
                        return (char) (ENGLISH_ARRAY[num][pageIndex * pageSize + index] & 0xff);
                    }
                }
                if (keyValue == info.getUp()) {
                    if (pageIndex > 0) {
                        pageIndex--;
                    }
                    else {
                        pageIndex = (ENGLISH_ARRAY[num].length - 1) / pageSize;
                    }
                    if (choiceIndex + pageIndex * pageSize >= ENGLISH_ARRAY[num].length) {
                        choiceIndex = ENGLISH_ARRAY[num].length - pageIndex * pageSize - 1;
                    }
                }
                else if (keyValue == info.getDown()) {
                    if ((pageIndex + 1) * pageSize < ENGLISH_ARRAY[num].length) {
                        pageIndex++;
                    }
                    else {
                        pageIndex = 0;
                    }
                    if (choiceIndex + pageIndex * pageSize >= ENGLISH_ARRAY[num].length) {
                        choiceIndex = ENGLISH_ARRAY[num].length - pageIndex * pageSize - 1;
                    }
                }
                else if (keyValue == info.getLeft()) {
                    if (choiceIndex > 0) {
                        choiceIndex--;
                    }
                    else if (pageIndex > 0) {
                        pageIndex--;
                        choiceIndex = pageSize - 1;
                    }
                    else {
                        pageIndex = (ENGLISH_ARRAY[num].length - 1) / pageSize;
                        choiceIndex = ENGLISH_ARRAY[num].length - pageIndex * pageSize - 1;
                    }
                }
                else if (keyValue == info.getRight()) {
                    if (choiceIndex + 1 < pageSize && pageIndex * pageSize + choiceIndex + 1 < ENGLISH_ARRAY[num].length) {
                        choiceIndex++;
                    }
                    else if (pageIndex * pageSize + choiceIndex + 1 >= ENGLISH_ARRAY[num].length) {
                        pageIndex = choiceIndex = 0;
                    }
                    else {
                        pageIndex++;
                        choiceIndex = 0;
                    }
                }
            }
        }
        return word;
    }

    /**
     *�÷������ܵķ���ֵ:GB2312����ĺ���,���Ƽ�,0(�û��л�������ģʽ)
     */
    private char getGB2312() throws InterruptedException {
        clearContent();
        char word = 0;
        int yStart = screen.getHeight() - height;
        int offset = 2 + 8 + width % 2, nodeCount = 0, nodeIndex = -1;
        int pageSize = (maxCol - offset) / 2, pageIndex = 0, choiceIndex = 0;
        int number = 0, process = 0;
        boolean choicing = false;
        //'ƴ'
        content.setByte(0, (byte) 0xc6);
        content.setByte(1, (byte) 0xb4);
        for (;;) {
            for (int index = 2; index < maxCol; index++) {
                content.setByte(index, (byte) 0);
            }
            if (nodeIndex != -1) {
                SpellNode node = nodes[nodeIndex];
                String spell = node.spell();
                for (int index = 0; index < spell.length(); index++) {
                    content.setByte(index + 2, (byte) spell.charAt(index));
                }
                node.getGB(content, offset, pageSize * pageIndex, pageSize);
            }
            render.setDrawMode(Renderable.RENDER_GRAPH_TYPE);
            render.drawString(0, yStart, content, 0, maxCol);
            render.setDrawMode(Renderable.RENDER_GRAPH_TYPE | Renderable.DRAW_NOT_TYPE | Renderable.RENDER_FILL_TYPE);
            render.drawRect(0, yStart, height - 1, screen.getHeight() - 1);
            if (nodeIndex != -1) {
                int length = nodes[nodeIndex].spell().length();
                for (int index = process; index < length; index++) {
                    render.drawRect((2 + index) * 6, yStart, (3 + index) * 6 - 1, screen.getHeight() - 1);
                }
            }
            if (choicing) {
                render.drawRect((offset + choiceIndex * 2) * 6, yStart, (offset + choiceIndex * 2 + 2) * 6 - 1, screen.getHeight() - 1);
            }
            screen.fireScreenChanged();
            int keyValue = key.getRawKey();
            int n = -1;
            for (int index = 0; index <= 9; index++) {
                if (keyValue == info.getNumberKey(index)) {
                    n = index;
                    break;
                }
            }
            if (nodeIndex == -1) {
                //��һ�ΰ���
                if (n == 0) {
                    //blank
                    word = 0xa1a1;
                    break;
                }
                if (n == 1) {
                    word = getGBSign();
                    if (word != 0) {
                        break;
                    }
                    continue;
                }
                if ((word = getCtrlKey(keyValue)) != 0) {
                    //����ǿ��Ƽ�
                    break;
                }
                if (shiftMode(keyValue)) {
                    //����Ǹı�����ģʽ����
                    break;
                }
                if (n != -1) {
                    //ǰ��һ��
                    process = 1;
                    number = n;
                }
                else {
                    //δ����İ���
                    continue;
                }
            }
            else if (choicing) {
                if (n != -1) {
                    if (n + pageIndex * pageSize < nodes[nodeIndex].size()) {
                        int pos = offset + 2 * n;
                        word |= (content.getByte(pos) & 0xff) | ((content.getByte(pos + 1) & 0xff) << 8);
                        break;
                    }
                }
                else if (keyValue == info.getEnter()) {
                    int pos = offset + 2 * choiceIndex;
                    word |= (content.getByte(pos) & 0xff) | ((content.getByte(pos + 1) & 0xff) << 8);
                    break;
                }
                else if (keyValue == info.getEsc()) {
                    //ת����һ��
                    choicing = false;
                    pageIndex = 0;
                }
                else if (keyValue == info.getLeft()) {
                    if (choiceIndex > 0) {
                        choiceIndex--;
                    }
                    else if (pageIndex > 0) {
                        pageIndex--;
                        choiceIndex = pageSize - 1;
                    }
                    else {
                        pageIndex = (nodes[nodeIndex].size() - 1) / pageSize;
                        choiceIndex = nodes[nodeIndex].size() - 1 - pageSize * pageIndex;
                    }
                }
                else if (keyValue == info.getRight()) {
                    if (choiceIndex + 1 < pageSize && pageIndex * pageSize + choiceIndex + 1 < nodes[nodeIndex].size()) {
                        choiceIndex++;
                    }
                    else if (pageSize * pageIndex + choiceIndex + 1 >= nodes[nodeIndex].size()) {
                        pageIndex = choiceIndex = 0;
                    }
                    else {
                        pageIndex++;
                        choiceIndex = 0;
                    }
                }
                else if (keyValue == info.getUp()) {
                    if (pageIndex > 0) {
                        pageIndex--;
                    }
                    else {
                        pageIndex = (nodes[nodeIndex].size() - 1) / pageSize;
                        if (choiceIndex + pageIndex * pageSize >= nodes[nodeIndex].size()) {
                            choiceIndex = nodes[nodeIndex].size() - pageIndex * pageSize - 1;
                        }
                    }
                }
                else if (keyValue == info.getDown()) {
                    if (pageIndex < (nodes[nodeIndex].size() - 1) / pageSize) {
                        pageIndex++;
                        if (choiceIndex + pageIndex * pageSize >= nodes[nodeIndex].size()) {
                            choiceIndex = nodes[nodeIndex].size() - pageIndex * pageSize - 1;
                        }
                    }
                    else {
                        pageIndex = 0;
                    }
                }
                continue;
            }
            else {
                if (n != -1) {
                    number |= n << (process * 4);
                    process++;
                }
                else if (keyValue == info.getEsc()) {
                    //������һ��
                    process = 0;
                }
                else if (keyValue == info.getEnter() || keyValue == info.getRight()) {
                    //ת����һ��
                    pageIndex = 0;
                    choiceIndex = 0;
                    choicing = true;
                    continue;
                }
                else if (keyValue == info.getLeft()) {
                    //�˻�һ��
                    process--;
                    number &= ~(0x0f << (process * 4));
                }
                else if (keyValue == info.getUp()) {
                    //�޸�nodeIndex
                    nodeIndex = nodeIndex > 0 ? nodeIndex - 1 : nodeCount - 1;
                    continue;
                }
                else if (keyValue == info.getDown()) {
                    nodeIndex = nodeIndex + 1 < nodeCount ? nodeIndex + 1 : 0;
                    continue;
                }
                else {
                    //δ����İ���
                    continue;
                }
            }

            //���漯�д���number
            if (process == 0) {
                nodeIndex = -1;
                continue;
            }
            int count = getSpellNodeByNumber(nodes, number);
            if (count <= 0) {
                //���û��,˵�����������Ч,�˸�,�������ϴ�nodeֵ����
                process--;
                number &= ~(0x0f << (process * 4));
                if (process == 0) {
                    nodeIndex = -1;
                }
            }
            else {
                nodeCount = count;
                nodeIndex = 0;
                pageIndex = 0;
            }


        }
        return word;
    }

    /**
     * ��ȡһȫ�Ƿ���
     */
    private char getGBSign() {
        return 0;
    }

    /**
     *�÷������ܵķ���ֵ:����asicc��,,0(�û��л�������ģʽ),�Լ����Ƽ�
     */
    private char getNumber() throws InterruptedException {
        clearContent();
        char word = 0;
        int yStart = screen.getHeight() - height;
        //'��'
        content.setByte(0, (byte) 0xca);
        content.setByte(1, (byte) 0xfd);
        render.setDrawMode(Renderable.RENDER_GRAPH_TYPE);
        render.drawString(0, yStart, content, 0, maxCol);
        render.setDrawMode(Renderable.RENDER_GRAPH_TYPE | Renderable.DRAW_NOT_TYPE | Renderable.RENDER_FILL_TYPE);
        render.drawRect(0, yStart, height - 1, screen.getHeight() - 1);
        screen.fireScreenChanged();

        for (;;) {
            int keyValue = key.getRawKey();
            //����ǿ��Ƽ�
            if ((word = getCtrlKey(keyValue)) != 0) {
                break;
            }
            //����Ǹı�����ģʽ����
            if (shiftMode(keyValue)) {
                break;
            }
            for (int index = 0; index <= 9; index++) {
                if (keyValue == info.getNumberKey(index)) {
                    word = (char) ('0' + index);
                    break;
                }
            }
            if (word != 0) {
                break;
            }
        }
        return word;
    }

    /**
     *  �Ƿ�Ϊ���Ƽ�,����Ƿ�����Ӧ�ļ�ֵ,���򷵻�0
     */
    private char getCtrlKey(int keyCode) {
        if (keyCode == info.getEnter()) {
            return 0x0d;     //�����

        }
        else if (keyCode == info.getEsc()) {
            return 0x1b;      //������

        }
        else if (keyCode == info.getLeft()) {
            return 0x17;      //left

        }
        else if (keyCode == info.getRight()) {
            return 0x16;     //right

        }
        else if (keyCode == info.getUp()) {
            return 0x1d;       //F2

        }
        return 0;
    }

    /**
     * ���keyCode�����¼�,��ı�����ģʽ,������true
     */
    private boolean shiftMode(int keyCode) {
        if (keyCode != info.getDown()) {
            return false;
        }
        switch (mode) {
            case ENGLISH_MODE:
                mode = NUMBER_MODE;
                break;
            case NUMBER_MODE:
                mode = GB2312_MODE;
                break;
            case GB2312_MODE:
                mode = ENGLISH_MODE;
                break;
        }
        return true;
    }

    private void clearContent() {
        for (int index = 0; index < maxCol; index++) {
            content.setByte(index, (byte) 0);
        }
    }

    /**
     * ��src��ȡ���ݻָ���Ļ״̬
     */
    private static void resumeScreen(ScreenModel screen, int height, Getable src) {
        screen.getRender().setDrawMode(Renderable.RENDER_GRAPH_TYPE);
        screen.getRender().drawRegion(0, screen.getHeight() - height, screen.getWidth(), height, src, 0);
        screen.fireScreenChanged();
    }

    /**
     *  ������Ļ����height��״̬��dst
     */
    private static void saveScreen(ScreenModel screen, int height, Setable dst) {
        screen.getRender().setDrawMode(Renderable.RENDER_GRAPH_TYPE);
        //����ʹ��screen.getWidth()+7����ΪgetRegion��������width�ĵ���λ
        screen.getRender().getRegion(0, screen.getHeight() - height, screen.getWidth() + 7, height, dst, 0);
    }

    /**
     * ͨ�����ֵõ�SpellNode
     * @param nodes ���ڱ���SpellNode������,Ӧ���㹻��
     * @param num   ����
     * @return ����������SpellNode����,���û�з����������򷵻�һ��������0����
     */
    private static int getSpellNodeByNumber(SpellNode[] nodes, int num) {
        int mask = -1;
        for (;;) {
            int tmp = mask >>> 4;
            if ((num & ~tmp) != 0) {
                break;
            }
            mask = tmp;
        }
        int count = 0;
        for (int index = 0; index < SPELL_NODES.length; index++) {
            if ((SPELL_NUMBERS[index] & mask) == num) {
                nodes[count++] = SPELL_NODES[index];
            }
        }
        return count;
    }

    /**
     * �õ�ƴ���ַ���spell���ֻ������϶�Ӧ������
     * @param spell ƴ��
     * @return ����
     */
    private static int getNumber(String spell) {
        int num = 0;
        for (int index = 0; index < spell.length(); index++) {
            num |= NUMBERS[spell.charAt(index) - 'a'] << (index * 4);
        }
        return num;
    }
    private static final int[] NUMBERS = {2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9};
    private static final SpellInfo.SpellNode[] SPELL_NODES;
    private static final int[] SPELL_NUMBERS;
    

    static {
        SPELL_NODES = new SpellNode[SpellInfo.count()];
        SPELL_NUMBERS = new int[SpellInfo.count()];
        Enumeration e = SpellInfo.list();
        int index = 0;
        while (e.hasMoreElements()) {
            SPELL_NODES[index++] = (SpellNode) e.nextElement();
        }
        //��spell�ĳ�������,Ŀ����Ϊ�������������������ʾ..
        for (int i = 0; i < SPELL_NODES.length; i++) {
            for (int j = i + 1; j < SPELL_NODES.length; j++) {
                if (SPELL_NODES[j].spell().length() < SPELL_NODES[i].spell().length()) {
                    SpellNode node = SPELL_NODES[i];
                    SPELL_NODES[i] = SPELL_NODES[j];
                    SPELL_NODES[j] = node;
                }
            }
        }
        index = 0;
        while (index < SPELL_NODES.length) {
            SPELL_NUMBERS[index] = getNumber(SPELL_NODES[index].spell());
            index++;
        }
    }
    private static final byte[][] ENGLISH_ARRAY = {
        {
            (byte) ' '
        },
        {(byte) '.', (byte) ',', (byte) ';', (byte) '-', (byte) '/', (byte) '?', (byte) '!', (byte) '$', (byte) '%', (byte) '(', (byte) ')',
            (byte) ':', (byte) '`', (byte) '\'', (byte) '"', (byte) '<', (byte) '>', (byte) '{', (byte) '}', (byte) '[', (byte) ']', (byte) '#',
            (byte) '~', (byte) '@', (byte) '^', (byte) '&', (byte) '*', (byte) '+', (byte) '=', (byte) '_', (byte) '|', (byte) '\\'
        },
        {
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'A', (byte) 'B', (byte) 'C'
        },
        {
            (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'D', (byte) 'E', (byte) 'F'
        },
        {
            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'G', (byte) 'H', (byte) 'I'
        },
        {
            (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'J', (byte) 'K', (byte) 'L'
        },
        {
            (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'M', (byte) 'N', (byte) 'O'
        },
        {
            (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S'
        },
        {
            (byte) 't', (byte) 'u', (byte) 'v', (byte) 'T', (byte) 'U', (byte) 'V'
        },
        {
            (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z'
        }
    };
}
