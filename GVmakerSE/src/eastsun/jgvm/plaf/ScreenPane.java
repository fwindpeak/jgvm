package eastsun.jgvm.plaf;

import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.event.Area;
import eastsun.jgvm.module.event.ScreenChangeListener;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @version Aug 13, 2008
 * @author Eastsun
 */
public class ScreenPane extends JPanel {

    BufferedImage image;
    JGVM gvm;
    int[] buffer = new int[160 * 80];

    public ScreenPane(JGVM gvm) {
        this.gvm = gvm;
        image = new BufferedImage(160, 80, BufferedImage.TYPE_3BYTE_BGR);
        gvm.addScreenChangeListener(new ScreenChangeListener() {

            public void screenChanged(ScreenModel screenModel, Area area) {
                screenModel.getRGB(buffer, area, 1, 0);
                image.setRGB(0, 0, 160, 80, buffer, 0, 160);
                repaint();
            }
        });
        setPreferredSize(new Dimension(320, 160));
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

}
