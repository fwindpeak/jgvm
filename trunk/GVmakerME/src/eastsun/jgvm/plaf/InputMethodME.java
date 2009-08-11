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
 * 手机上的输入法实现<p>
 * 该实现的目标是能支持E文字母(大小写),数字,符号,汉字的输入.
 * 该实现通过KeyModel.getSysInfo()得到所用按键,如果KeyModel.SysInfo.hasNumberKey()返回false,则该实现直接返回KeyModel.getchar()<p>
 * 按键说明:<p><hr><blockquote><pre>
 *             刚进入输入法时的状态下:
 *             如果第一次按键为输入,跳出,左,右这些键,直接返回这些键值;如果为向上键,则返回F2键的键值,如果为下键,则切换输入模式,否则:
 *             1.在数字输入模式下,按数字键直接返回输入的数字
 *             2.在字母输入模式下分两步:
 *                    a. 第一步: 如果按键是0,则直接返回半角空格;否则转入下一步
 *                    b. 若输入的是1,则显示半角字符备选,否则显示的是相应的大小写字母备选;
 *                       此时使用上/下键翻页,左右键移动光标,跳出返回上一步,数字键可以直接选中备选字符返回,若按输入键返回选中;
 *             3.在汉字输入模式下分两步:
 *                    a.第一步: 输入拼音,(如果第一次按键为0则直接返回全角空格,为进入全角符号选择)此过程中上下键切换备选拼音,左键退格,跳出键撤销所有输入,右键或输入键进入下一步
 *                    b.第二步:选择汉字,此过程中上下键翻页,左右键移动光标,输入键选中汉字,跳出键返回上一步
 * </pre></blockquote><hr><p>
 * @author Eastsun
 * @version 2008-3-18
 */
public class InputMethodME implements InputMethod {

    private int width,  height;
    //提示最大列数
    private int maxCol;
    private int mode;
    private Accessable graph;
    private ScreenModel screen;
    private Renderable render;
    private KeyModel key;
    private KeyModel.SysInfo info;
    //绘制显示的提示内容
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
        //用于保存屏幕地下height行状态
        if (screen != sm || sm.getWidth() != width) {
            screen = sm;
            key = km;
            render = sm.getRender();
            width = sm.getWidth();
            graph = Util.asAccessable(new byte[(width + 7) / 8 * height]);
            maxCol = width / 6;
            content = Util.asAccessable(new byte[maxCol]);
        }
        //保护屏幕状态
        saveScreen(sm, height, graph);
        //更改输入模式

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
     * 该方法可能的返回值:E文字母asicc码,控制键,0(用户切换了输入模式)
     */
    private char getEnglish() throws InterruptedException {
        clearContent();
        char word = 0;
        int num = -1, choiceIndex = 0, pageIndex = 0, offset = 4, pageSize = maxCol - offset;
        int yStart = screen.getHeight() - height;
        //'英'
        content.setByte(0, (byte) 0xd3);
        content.setByte(1, (byte) 0xa2);
        for (;;) {
            //清理之前的状态
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
                //如果是控制键
                if ((word = getCtrlKey(keyValue)) != 0) {
                    break;
                }
                //如果是改变输入模式按键
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
     *该方法可能的返回值:GB2312编码的汉字,控制键,0(用户切换了输入模式)
     */
    private char getGB2312() throws InterruptedException {
        clearContent();
        char word = 0;
        int yStart = screen.getHeight() - height;
        int offset = 2 + 8 + width % 2, nodeCount = 0, nodeIndex = -1;
        int pageSize = (maxCol - offset) / 2, pageIndex = 0, choiceIndex = 0;
        int number = 0, process = 0;
        boolean choicing = false;
        //'拼'
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
                //第一次按键
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
                    //如果是控制键
                    break;
                }
                if (shiftMode(keyValue)) {
                    //如果是改变输入模式按键
                    break;
                }
                if (n != -1) {
                    //前进一步
                    process = 1;
                    number = n;
                }
                else {
                    //未定义的按键
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
                    //转到上一步
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
                    //跳回上一步
                    process = 0;
                }
                else if (keyValue == info.getEnter() || keyValue == info.getRight()) {
                    //转入下一步
                    pageIndex = 0;
                    choiceIndex = 0;
                    choicing = true;
                    continue;
                }
                else if (keyValue == info.getLeft()) {
                    //退回一格
                    process--;
                    number &= ~(0x0f << (process * 4));
                }
                else if (keyValue == info.getUp()) {
                    //修改nodeIndex
                    nodeIndex = nodeIndex > 0 ? nodeIndex - 1 : nodeCount - 1;
                    continue;
                }
                else if (keyValue == info.getDown()) {
                    nodeIndex = nodeIndex + 1 < nodeCount ? nodeIndex + 1 : 0;
                    continue;
                }
                else {
                    //未定义的按键
                    continue;
                }
            }

            //下面集中处理number
            if (process == 0) {
                nodeIndex = -1;
                continue;
            }
            int count = getSpellNodeByNumber(nodes, number);
            if (count <= 0) {
                //如果没有,说明这次输入无效,退格,并保持上次node值不变
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
     * 获取一全角符号
     */
    private char getGBSign() {
        return 0;
    }

    /**
     *该方法可能的返回值:数字asicc码,,0(用户切换了输入模式),以及控制键
     */
    private char getNumber() throws InterruptedException {
        clearContent();
        char word = 0;
        int yStart = screen.getHeight() - height;
        //'数'
        content.setByte(0, (byte) 0xca);
        content.setByte(1, (byte) 0xfd);
        render.setDrawMode(Renderable.RENDER_GRAPH_TYPE);
        render.drawString(0, yStart, content, 0, maxCol);
        render.setDrawMode(Renderable.RENDER_GRAPH_TYPE | Renderable.DRAW_NOT_TYPE | Renderable.RENDER_FILL_TYPE);
        render.drawRect(0, yStart, height - 1, screen.getHeight() - 1);
        screen.fireScreenChanged();

        for (;;) {
            int keyValue = key.getRawKey();
            //如果是控制键
            if ((word = getCtrlKey(keyValue)) != 0) {
                break;
            }
            //如果是改变输入模式按键
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
     *  是否为控制键,如果是返回相应的键值,否则返回0
     */
    private char getCtrlKey(int keyCode) {
        if (keyCode == info.getEnter()) {
            return 0x0d;     //输入键

        }
        else if (keyCode == info.getEsc()) {
            return 0x1b;      //跳出键

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
     * 如果keyCode是向下键,则改变输入模式,并返回true
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
     * 从src获取数据恢复屏幕状态
     */
    private static void resumeScreen(ScreenModel screen, int height, Getable src) {
        screen.getRender().setDrawMode(Renderable.RENDER_GRAPH_TYPE);
        screen.getRender().drawRegion(0, screen.getHeight() - height, screen.getWidth(), height, src, 0);
        screen.fireScreenChanged();
    }

    /**
     *  保存屏幕底下height行状态到dst
     */
    private static void saveScreen(ScreenModel screen, int height, Setable dst) {
        screen.getRender().setDrawMode(Renderable.RENDER_GRAPH_TYPE);
        //这里使用screen.getWidth()+7是因为getRegion方法忽略width的第三位
        screen.getRender().getRegion(0, screen.getHeight() - height, screen.getWidth() + 7, height, dst, 0);
    }

    /**
     * 通过数字得到SpellNode
     * @param nodes 用于保存SpellNode的数组,应该足够长
     * @param num   数字
     * @return 符合条件的SpellNode个数,如果没有符合条件的则返回一个不大于0的数
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
     * 得到拼音字符串spell在手机键盘上对应的数字
     * @param spell 拼音
     * @return 数字
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
        //按spell的长度排序,目的是为了让最符合条件的先显示..
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
