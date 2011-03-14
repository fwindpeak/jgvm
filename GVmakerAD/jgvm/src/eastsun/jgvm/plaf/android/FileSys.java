package eastsun.jgvm.plaf.android;

import eastsun.jgvm.module.FileModel;
import eastsun.jgvm.module.io.FileSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class FileSys implements FileSystem {

    String root;
    
    public FileSys(String root) {
        File froot = new File(root);
        if (!froot.isDirectory()) {
            froot.mkdirs();
        }
        this.root = root;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        return new FileInputStream(root + fileName);
    }

    public OutputStream getOutputStream(String fileName) throws IOException {
        return new FileOutputStream(root + fileName);
    }

    public boolean deleteFile(String fileName) {
        File file = new File(root + fileName);
        return file.delete();
    }

    public boolean makeDir(String dirName) {
        File dir = new File(root + dirName);
        return dir.mkdir();
    }

    public Info getFileInf(String fileName) {
        final File file = new File(root + fileName);
        final String[] files = file.list(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                try {
                    return name.getBytes("gb2312").length <= FileModel.FILE_NAME_LENGTH;
                } catch (UnsupportedEncodingException ex) {
                    return false;
                }
            }
        });

        return new Info() {

            public boolean isFile() {
                return file.isFile();
            }

            public boolean isDirectory() {
                return file.isDirectory();
            }

            public boolean canRead() {
                return file.canRead();
            }

            public boolean canWrite() {
                return file.canWrite();
            }

            public int getFileNum() {
                return files.length;
            }

            public int listFiles(String[] names, int start, int num) {
                int index = 0;
                while (index < num && index + start < files.length) {
                    names[index] = files[index + start];
                    index++;
                }
                return index;
            }
        };
    }
}
