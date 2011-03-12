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
public interface Setable {

    void setByte(int addr, byte b) throws IndexOutOfBoundsException;
}
