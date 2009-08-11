package eastsun.jgvm.plaf.sys;

import eastsun.jgvm.module.io.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Eastsun
 * @version 2008-2-26
 */
public class FileSysME implements FileSystem {

    private String root;

    public FileSysME(String dir) {
        if (dir.endsWith("/")) {
            root = dir.substring(0, dir.length() - 1);
        }
        else {
            root = dir;
            dir = dir + "/";
        }
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(dir, Connector.READ_WRITE);
            if (!fc.isDirectory()) {
                fc.mkdir();
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("ÎÄ¼þ¼Ð: " + dir + "·ÃÎÊÊ§°Ü!");
        } finally {
            try {
                fc.close();
            } catch (Exception ex) {
            //do nothing
            }
        }
    }

    public InputStream getInputStream(String fileName) throws IOException {
        return new InputStreamME(root + fileName);
    }

    public OutputStream getOutputStream(String fileName) throws IOException {
        return new OutputStreamME(root + fileName);
    }

    public boolean deleteFile(String fileName) {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(root + fileName, Connector.READ_WRITE);
            fc.delete();
        } catch (IOException ex) {
            return false;
        } finally {
            try {
                fc.close();
            } catch (IOException ex) {
            //do nothing
            }
        }
        return true;
    }

    public boolean makeDir(String dirName) {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(root + dirName, Connector.READ_WRITE);
            fc.mkdir();
        } catch (IOException ex) {
            return false;
        } finally {
            try {
                fc.close();
            } catch (IOException ex) {
            //do nothing
            }
        }
        return true;
    }

    public Info getFileInf(String fileName) {
        return new FileInfME(root + fileName);
    }
}
