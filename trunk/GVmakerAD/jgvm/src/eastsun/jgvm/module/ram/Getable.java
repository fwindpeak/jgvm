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
     * ��ȡ��ַaddr��������
     * @param addr ��ַ
     * @return data
     * @throws java.lang.IndexOutOfBoundsException ����Խ��
     */
    byte getByte(int addr) throws IndexOutOfBoundsException;
}
