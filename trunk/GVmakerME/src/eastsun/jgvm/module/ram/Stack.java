package eastsun.jgvm.module.ram;

/**
 * GVM��ջģ��
 * @author Eastsun
 * @version 1.0 2008/1/21
 */
public final class Stack {

    private int[] buffer;
    private int pointer;
    private int last;

    public Stack(int size) {
        buffer = new int[size];
        pointer = 0;
    }

    /**
     * ��ջѹ������,ջָ���һ
     * @param data ����
     */
    public void push(int data) {
        last = data;
        buffer[pointer++] = data;
    }

    /**
     * ����һ������,ָ���һ
     * @return data
     */
    public int pop() {
        last = buffer[--pointer];
        return last;
    }

    /**
     * ��ȡ��ջ��offset������,�����ı�ջָ��.
     * peek(-1) ==pop(),��peek(-2)��ֵ��pop()���pop()��ͬ,������.
     * @param offset ��������λ�����ջ����ƫ����
     * @return data
     */
    public int peek(int offset) {
        return buffer[pointer + offset];
    }

    /**
     * ���һ�ε���|ѹ���ֵ
     * @return lastValue
     */
    public int lastValue() {
        return last;
    }

    /**
     * �õ�ջָ��,��ֵ�뵱ǰջ�����ݸ�����ͬ
     * @return pointer
     */
    public int getPointer() {
        return pointer;
    }

    /**
     * �õ���stack�Ĵ�С(��λint)
     * @return size
     */
    public int size() {
        return buffer.length;
    }

    /**
     * �޸�ջָ��,�޸ĺ��ָ��ֵ����getPointer()+offset
     * @param offset ƫ��ֵ
     * @return �޸ĺ��pointerֵ
     */
    public int movePointer(int offset) {
        pointer += offset;
        return pointer;
    }

    /**
     * ջָ�����
     */
    public void clear() {
        pointer = 0;
    }
}
