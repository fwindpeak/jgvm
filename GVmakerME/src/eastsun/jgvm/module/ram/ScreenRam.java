package eastsun.jgvm.module.ram;

import eastsun.jgvm.module.*;

/**
 * 用于显存与缓存的Ram
 * @author Eastsun
 * @version 2008/1/19
 */
public final class ScreenRam implements RelativeRam {

    private byte[] buffer;
    private int type;
    private int startAddr;
    private ScreenModel screen;

    public ScreenRam(ScreenModel screen, byte[] buffer, int type) {
        this.screen = screen;
        this.buffer = buffer;
        this.type = type;
    }

    public ScreenModel getScreenModel() {
        return screen;
    }

    public int size() {
        return buffer.length;
    }

    public int getRamType() {
        return type;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(int addr) {
        startAddr = addr;
    }

    public byte getByte(int addr) throws IndexOutOfBoundsException {
        return buffer[addr - startAddr];
    }

    public void setByte(int addr, byte data) throws IndexOutOfBoundsException {
        buffer[addr - startAddr] = data;
    }

    public void clear() {
        for (int index = buffer.length - 1; index >= 0; index--) {
            buffer[index] = 0;
        }
    }
}
