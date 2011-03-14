package eastsun.jgvm.module.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * �����ļ�,ʹ���ڴ�ģ���ļ�����<p>
 * ÿ�������ļ����˶�д������,������������:position,limit,capacity<p>
 *     ��capacity��������������ļ���ǰ�����ɵ����������,���ֵ���ܴ��ⲿ�޸�,��������д������ʱ������Ҫ�ڲ����ʵ�������capacity<p>
 *     limit�������������ļ���ǰ�洢����������,�ⲿ���Զ�ȡ���޸Ļ��������ݵ������ļ�.���ֵ�ڵ���readFromStreamʱ�Զ���ʼ��,�����ڲ��Զ�ά��<p>
 *     position��ʾ��һ����/д���ݵĵ�ַ,�൱����ͨ�ļ������е��ļ�ָ��.���ʼֵӦ���ɵ������ڵ���readFromStream��������ȷ����<p>
 * ����һ����������,����ȷ��ʼ����VirtualFile,Ӧ�����¹�ϵ����:<p>
 *     0<=position<=limit<=capacity
 * @author Eastsun
 * @version 2008-2-25
 */
final class VirtualFile {

    //ÿ����������Сֵ:128K
    private static final int MIN_ADD_CAPS = 0x20000;
    private static final int MAX_COUNT = 30;

    //�ڴ��,���֧��10��,Ҳ����1280K,��GVmaker��˵�㹻��
    private byte[][] bufs = new byte[MAX_COUNT][];
    //caps[k]��ʾ��0,..k-1���ڴ��������
    private int[] caps = new int[MAX_COUNT + 1];
    //�ڴ������
    private int count;
    //��ǰ�����ڴ���±�
    private int index;
    //�ļ�����
    private int limit;
    //the index of the next element to be read or written
    private int position;

    /**
     * ʹ��һ����ʼ��������VirtualFile
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
     * �õ���VirtualFile������
     * @return capacity
     */
    public int capacity() {
        return caps[count];
    }

    /**
     * �õ�VirtualFile��ʵ�ʴ洢���ݵĳ���,Ҳ�����ļ��ĳ���
     * @return length of file
     */
    public int limit() {
        return limit;
    }

    /**
     * �õ�VirtualFile�еĶ�дָ��λ��,Ҳ�����ļ�ָ��
     * @return position
     */
    public int position() {
        return position;
    }

    /**
     * �����ļ�ָ��
     * @param newPos �µ�ָ��λ��
     * @return newPos ���ú��ָ��,��������-1
     */
    public int position(int newPos) {
        if (newPos < 0 || newPos > limit) {
            return -1;
        }
        position = newPos;
        //�޸�index,ʹ������caps[index]<=position<caps[index+1]
        while (index < MAX_COUNT && caps[index] < caps[index + 1] && caps[index + 1] <= position) {
            index++;
        }
        while (caps[index] > position) {
            index--;
        }
        return position;
    }

    /**
     * ��ȡ�ļ�����,����position��1
     * @return ��ǰposition�����ļ�����;���ѵ��ļ�λ(>=limit()),����-1
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
     * ��position,limit����
     */
    public void refresh() {
        index = position = limit = 0;
    }

    /**
     * ��in��ȡ���ݵ�VirtualFile,��ʼlimit��ֵΪ��in��ȡ����������<p>
     * ������ɺ�ر�in
     * @param in ������Դ
     * @throws java.io.IOException ����IO����
     */
    public void readFromStream(InputStream in) throws IOException {
        limit = 0;
        int length;
        int n = 0;
        for (;;) {
            //��Ҫ��������
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
     * ��VirtualFile�е�����д�뵽out<p>
     * ������ɺ�ر�out
     * @param out д��Ŀ��
     * @throws java.io.IOException ����IO����
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
    //ȷ��������minCap��С���ڴ����

    private void ensureCapacity(int minCap) {
        if (caps[count] >= minCap) {
            return;
        }
        //ÿ����������128K
        int addCap = Math.max(MIN_ADD_CAPS, minCap - caps[count]);
        bufs[count] = new byte[addCap];
        for (int n = count + 1; n <= MAX_COUNT; n++) {
            caps[n] = caps[count] + addCap;
        }
        count++;
    }
}
