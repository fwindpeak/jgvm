/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eastsun.jgvm.plaf;

import javax.microedition.lcdui.Form;

/**
 *
 * @author Administrator
 */
public class SoftInf extends Form {

    private static SoftInf inf = new SoftInf();

    public static SoftInf getSoftInf() {
        return inf;
    }

    private SoftInf() {
        super("软件信息");
        append("软件名称: GVmakerME1.0 Beta\n");
        append("移 植 者: Eastsun\n");
        append("\n声  明:\n该软件为学习娱乐之用.本人只做了GVM的移植工作,GVM的版权以及运行在上面的GVmaker程序归原作者所有."+
                "有关GVmaker的具体信息以及相应软件可以去www.ggv.com.cn以及www.emsky.net");
    }
}
