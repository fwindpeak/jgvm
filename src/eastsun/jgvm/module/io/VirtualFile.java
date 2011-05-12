package eastsun.jgvm.module.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 虚拟文件,使用内存模拟文件操作<p>
 * 每个虚拟文件除了读写数据外,还有三个属性:position,limit,capacity<p>
 *     其capacity描述了这个虚拟文件当前能容纳的最大数据量,这个值不能从外部修改,但在向其写入数据时根据需要内部会适当扩充其capacity<p>
 *     limit描述的是虚拟文件当前存储的数据总量,外部可以读取或修改或增加数据到虚拟文件.这个值在调用readFromStream时自动初始化,并且内部自动维护<p>
 *     position表示下一个读/写数据的地址,相当于普通文件操作中的文件指针.其初始值应该由调用者在调用readFromStream方法后正确设置<p>
 * 对于一个含有数据,并正确初始化的VirtualFile,应有以下关系成立:<p>
 *     0<=position<=limit<=capacity
 * @author Eastsun
 * @version 2008-2-25
 */
final class VirtualFile {

    //每次增量的最小值:128K
    private static final int MIN_ADD_CAPS = 0x20000;
    private static final int MAX_COUNT = 30;

    //内存块,最多支持10块,也就是1280K,对GVmaker来说足够了
    private byte[][] bufs = new byte[MAX_COUNT][];
    //caps[k]表示第0,..k-1块内存的总容量
    private int[] caps = new int[MAX_COUNT + 1];
    //内存块数量
    private int count;
    //当前所在内存块下标
    private int index;
    //文件长度
    private int limit;
    //the index of the next element to be read or written
    private int position;

    /**
     * 使用一个初始容量构造VirtualFile
     * @param size
     */
    public VirtualFile(int size) {
        bufs[0] = new byte[size];
        for (int n = 1; n <= MAX_COUNT; n++) {
            caps[n] = size;
        }
        count = 1;
    }

    /**
     * 得到该VirtualFile总容量
     * @return capacity
     */
    public int capacity() {
        return caps[count];
    }

    /**
     * 得到VirtualFile中实际存储数据的长度,也就是文件的长度
     * @return length of file
     */
    public int limit() {
        return limit;
    }

    /**
     * 得到VirtualFile中的读写指针位置,也就是文件指针
     * @return position
     */
    public int position() {
        return position;
    }

    /**
     * 设置文件指针
     * @param newPos 新的指针位置
     * @return newPos 设置后的指针,若出错返回-1
     */
    public int position(int newPos) {
        if (newPos < 0 || newPos > limit) {
            return -1;
        }
        position = newPos;
        //修改index,使其满足caps[index]<=position<caps[index+1]
        while (index < MAX_COUNT && caps[index] < caps[index + 1] && caps[index + 1] <= position) {
            index++;
        }
        while (caps[index] > position) {
            index--;
        }
        return position;
    }

    /**
     * 读取文件数据,并且position加1
     * @return 当前position出的文件内容;若已到文件位(>=limit()),返回-1
     */
    public int getc() {
        if (position >= limit) {
            return -1;
        }
        int c = bufs[index][position - caps[index]] & 0xff;
        position++;
        if (position >= caps[index + 1]) {
            index++;
        }
        return c;
    }

    public int putc(int ch) {
        if (position > limit) {
            return -1;
        }
        ensureCapacity(position + 1);
        bufs[index][position - caps[index]] = (byte) ch;
        position++;
        if (position > limit) {
            limit = position;
        }
        if (position >= caps[index + 1]) {
            index++;
        }
        return ch;
    }

    /**
     * 将position,limit清零
     */
    public void refresh() {
        index = position = limit = 0;
    }

    /**
     * 从in读取数据到VirtualFile,初始limit的值为从in读取的数量长度<p>
     * 操作完成后关闭in
     * @param in 数据来源
     * @throws java.io.IOException 发生IO错误
     */
    public void readFromStream(InputStream in) throws IOException {
        limit = 0;
        int length;
        int n = 0;
        for (;;) {
            //需要增加容量
            if (n == count) {
                bufs[n] = new byte[MIN_ADD_CAPS];
                for (int k = n + 1; k <= MAX_COUNT; k++) {
                    caps[k] = caps[n] + MIN_ADD_CAPS;
                }
                count++;
            }
            length = in.read(bufs[n]);
            if (length == -1) {
                break;
            }
            else {
                limit += length;
                if (length < bufs[n].length) {
                    break;
                }
            }
            n++;
        }
    }

    /**
     * 将VirtualFile中的内容写入到out<p>
     * 操作完成后关闭out
     * @param out 写入目标
     * @throws java.io.IOException 发生IO错误
     */
    public void writeToStream(OutputStream out) throws IOException {
        int n = 0;
        while (limit > caps[n + 1]) {
            out.write(bufs[n++]);
        }
        if (limit > caps[n]) {
            out.write(bufs[n], 0, limit - caps[n]);
        }
        out.close();
    }
    //确保至少有minCap大小的内存可用

    private void ensureCapacity(int minCap) {
        if (caps[count] >= minCap) {
            return;
        }
        //每次至少增加128K
        int addCap = Math.max(MIN_ADD_CAPS, minCap - caps[count]);
        bufs[count] = new byte[addCap];
        for (int n = count + 1; n <= MAX_COUNT; n++) {
            caps[n] = caps[count] + addCap;
        }
        count++;
    }
}
