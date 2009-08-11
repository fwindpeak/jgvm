package eastsun.jgvm.plaf;

import eastsun.jgvm.module.GvmConfig;
import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.LavApp;
import eastsun.jgvm.module.io.DefaultFileModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

/**
 * @version Aug 13, 2008
 * @author Eastsun
 */
public class MainFrame extends JFrame {

    JGVM gvm;
    ScreenPane screenPane;
    KeyBoard keyBoard;
    Worker worker;
    JFileChooser fileChooser;
    JLabel msgLabel;

    public MainFrame() {
        super("GVmakerSE");
        keyBoard = new KeyBoard();
        gvm = JGVM.newGVM(new GvmConfig(), new DefaultFileModel(new FileSysSE("GVM_ROOT")), keyBoard.getKeyModel());
        screenPane = new ScreenPane(gvm);
        fileChooser = new JFileChooser("GVM_ROOT");
        fileChooser.addChoosableFileFilter(new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".lav");
                }
            }

            public String getDescription() {
                return "GVmaker Application";
            }
        });

        msgLabel = new JLabel(" 结束");
        add(screenPane, BorderLayout.NORTH);
        add(msgLabel, BorderLayout.CENTER);
        add(keyBoard, BorderLayout.SOUTH);

        setJMenuBar(createMenuBar());
        pack();
        setResizable(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println(System.getProperty("java.class.path"));
        JFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setMsg(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                msgLabel.setText(" " + msg);
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("文件");
        file.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e) {
                if (worker != null && worker.isAlive()) {
                    worker.setPaused(true);
                    setMsg("暂停");
                }
            }

            public void menuDeselected(MenuEvent e) {
                if (worker != null && worker.isAlive()) {
                    worker.setPaused(false);
                    setMsg("运行");
                }
            }

            public void menuCanceled(MenuEvent e) {
                System.out.println("canzzzz");
            }
        });
        menuBar.add(file);
        JMenuItem open = new JMenuItem(new AbstractAction("打开") {

            public void actionPerformed(ActionEvent e) {
                if (worker != null && worker.isAlive()) {
                    worker.setPaused(true);
                    setMsg("暂停");
                }
                openLavFile();
            }
        });
        file.add(open);
        file.add(new AbstractAction("退出") {

            public void actionPerformed(ActionEvent e) {
                if (worker != null && worker.isAlive()) {
                    worker.interrupt();
                    try {
                        worker.join();
                    } catch (InterruptedException ex) {
                        System.err.println(ex);
                    }
                }
                System.exit(0);
            }
        });
        return menuBar;
    }

    private void openLavFile() {
        int res = fileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            InputStream in = null;
            try {
                in = new FileInputStream(fileChooser.getSelectedFile());
            } catch (FileNotFoundException ex) {
                System.err.println(ex);
            }
            LavApp lavApp = LavApp.createLavApp(in);
            if (worker != null && worker.isAlive()) {
                worker.interrupt();
                try {
                    worker.join();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            gvm.loadApp(lavApp);
            worker = new Worker();
            worker.start();
            setMsg("运行");
        } else {
            if (worker != null && worker.isAlive()) {
                worker.setPaused(false);
                setMsg("运行");
            } else {
                setMsg("结束");
            }
        }
    }

    class Worker extends Thread {

        private boolean isPaused;

        public Worker() {
            setDaemon(true);
            setPaused(false);
        }

        public void run() {
            try {
                int step = 0;
                while (!(gvm.isEnd() || isInterrupted())) {
                    while (isPaused()) {
                        synchronized (this) {
                            wait();
                        }
                    }
                    gvm.nextStep();
                    step++;
                    if (step == 100) {
                        step = 0;
                        Thread.sleep(0, 100);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                gvm.dispose();
                setMsg("结束");
            }
        }

        public synchronized boolean isPaused() {
            return isPaused;
        }

        public synchronized void setPaused(boolean p) {
            if (p != isPaused) {
                isPaused = p;
                notifyAll();
            }
        }
    }
}
