package eastsun.jgvm.plaf.sys;

import eastsun.jgvm.module.FileModel;
import eastsun.jgvm.module.io.FileSystem;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 * J2ME上的FileInf实现
 * @author Eastsun
 * @version 2008-2-26
 */
public class FileInfME implements FileSystem.Info {

    private boolean isFile,  isDir,  canRead,  canWrite;
    private int fileNum;
    private String[] files;

    public FileInfME(String fileName) {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(fileName, Connector.READ_WRITE);
            isDir = fc.isDirectory();
            isFile = (fc.exists() && !fc.isDirectory());
            canRead = fc.canRead();
            canWrite = fc.canWrite();
            if (fc.isDirectory()) {
                Enumeration e = fc.list();
                fileNum = 0;
                //只计算文件名长度不超过FILE_NAME_LENGTH的
                while (e.hasMoreElements()) {
                    String name = e.nextElement().toString();
                    int length = name.getBytes().length;
                    if (name.endsWith("/") && length <= FileModel.FILE_NAME_LENGTH + 1) {
                        fileNum++;
                    }
                    else if (length <= FileModel.FILE_NAME_LENGTH) {
                        fileNum++;
                    }
                }
                files = new String[fileNum];
                e = fc.list();
                int index = 0;
                while (e.hasMoreElements() && index < fileNum) {
                    String name = e.nextElement().toString();
                    int length = name.getBytes().length;
                    if (name.endsWith("/") && length <= FileModel.FILE_NAME_LENGTH + 1) {
                        files[index++] = name.substring(0, name.length() - 1);
                    }
                    else if (length <= FileModel.FILE_NAME_LENGTH) {
                        files[index++] = name;
                    }
                }
            }
            else {
                fileNum = -1;
            }
        } catch (Exception ex) {
            isFile = false;
            isDir = false;
            canRead = false;
            canWrite = false;
            fileNum = -1;
        } finally {
            try {
                fc.close();
            } catch (Exception ex) {
            //do nothing
            }
        }
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean isDirectory() {
        return isDir;
    }

    public boolean canRead() {
        return canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public int getFileNum() {
        return fileNum;
    }

    public int listFiles(String[] names, int start, int num) {
        if (fileNum == -1) {
            return -1;
        }
        int index = 0;
        while (index < num && index + start < fileNum) {
            names[index] = files[index + start];
            index++;
        }
        return index;
    }

    public String toString() {
        return "fileName: [isFile:" + isFile() + ",isDir:" + isDir + "canRead: " + canRead() + ",canWrite:" + canWrite() + ",fileNum:" + getFileNum() + "]";
    }
}
