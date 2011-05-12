/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eastsun.jgvm.module.ram;

/**
 *
 * @author Eastsun
 * @version 2008/2/19
 */
public interface Getable {

    /**
     * 读取地址addr处的数据
     * @param addr 地址
     * @return data
     * @throws java.lang.IndexOutOfBoundsException 访问越界
     */
    byte getByte(int addr) throws IndexOutOfBoundsException;
}
