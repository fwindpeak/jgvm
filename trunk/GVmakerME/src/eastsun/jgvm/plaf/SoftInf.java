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
        super("�����Ϣ");
        append("�������: GVmakerME1.0 Beta\n");
        append("�� ֲ ��: Eastsun\n");
        append("\n��  ��:\n�����Ϊѧϰ����֮��.����ֻ����GVM����ֲ����,GVM�İ�Ȩ�Լ������������GVmaker�����ԭ��������."+
                "�й�GVmaker�ľ�����Ϣ�Լ���Ӧ�������ȥwww.ggv.com.cn�Լ�www.emsky.net");
    }
}
