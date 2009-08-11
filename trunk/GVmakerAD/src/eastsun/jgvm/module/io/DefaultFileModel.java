package eastsun.jgvm.module.io;

import eastsun.jgvm.module.FileModel;
import eastsun.jgvm.module.ram.Getable;
import eastsun.jgvm.module.ram.Setable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * �ļ�����ģ�͵�Ĭ��ʵ��,��ʵ��ͨ��FileSystem�ӿڵõ������ļ��������������,�����ڴ���ģ���ļ��ĸ��ֲ���
 * @author Eastsun
 * @version 2008-2-22
 */
public class DefaultFileModel implements FileModel {

    public static final String READ_MODE = "r";
    public static final String READ_PLUS_MODE = "r+";
    public static final String READ_B_MODE = "rb";
    public static final String READ_B_PLUS_MODE = "rb+";
    public static final String WRITE_MODE = "w";
    public static final String WRITE_PLUS_MODE = "w+";
    public static final String WRITE_B_MODE = "wb";
    public static final String WRITE_B_PLUS_MODE = "wb+";
    public static final String APPEND_MODE = "a";
    public static final String APPEND_PLUS_MODE = "a+";
    public static final String APPEND_B_MODE = "ab";
    public static final String APPEND_B_PLUS_MODE = "ab+";
    private static final int SEEK_SET = 0;
    private static final int SEEK_CUR = 1;
    private static final int SEEK_END = 2;
    //ͬʱ�����ļ���������
    private final static int MAX_FILE_COUNT = 3;
    private FileSystem fileSys;
    private String workDir;
    private FileSystem.Info workDirInf;
    private boolean[] canRead;
    private boolean[] canWrite;
    private String[] fileNames;
    //�Ƿ����,Ҳ�����Ƿ����
    private boolean[] usable;
    private VirtualFile[] files;
    //��������String��byte����
    private byte[] strBuf;

    public DefaultFileModel(FileSystem fileSys) {
        this.fileSys = fileSys;
        workDir = "/";
        workDirInf = fileSys.getFileInf(workDir);
        canRead = new boolean[MAX_FILE_COUNT];
        canWrite = new boolean[MAX_FILE_COUNT];
        usable = new boolean[MAX_FILE_COUNT];
        files = new VirtualFile[MAX_FILE_COUNT];
        fileNames = new String[MAX_FILE_COUNT];

        for (int index = 0; index < MAX_FILE_COUNT; index++) {
            usable[index] = true;
            //��ʼ����:64K
            files[index] = new VirtualFile(0x10000);
        }
        strBuf = new byte[400];
    }

    public boolean changeDir(Getable source, int addr) {
        int pre = -2;
        int length = 0;
        byte b;
        while ((b = source.getByte(addr)) != 0) {
            if (b == '/') {
                if (pre != addr - 1) {
                    strBuf[length++] = b;
                }
                pre = addr;
                addr++;
            }
            else {
                strBuf[length++] = b;
                addr++;
            }
        }
        String newDir = null;
        try {
            newDir = new String(strBuf, 0, length, "gb2312");
        } catch (UnsupportedEncodingException uee) {
            newDir = new String(strBuf, 0, length);
        }
        if (newDir.equals("..")) {
            if (workDir.equals("/")) {
                return false;
            }
            int pos = workDir.lastIndexOf('/', workDir.length() - 2) + 1;
            workDir = workDir.substring(0, pos);
            workDirInf = fileSys.getFileInf(workDir);
            return true;
        }
        else {
            if (!newDir.startsWith("/")) {
                newDir = workDir + newDir;
            }
            if (!newDir.endsWith("/")) {
                newDir += "/";
            }
            if (workDir.equals(newDir)) {
                return true;
            }
            FileSystem.Info inf = fileSys.getFileInf(newDir);
            if (inf.isDirectory()) {
                workDir = newDir;
                workDirInf = inf;
                return true;
            }
            else {
                return false;
            }
        }
    }

    public boolean makeDir(Getable source, int addr) {
        String dir = getFileName(source, addr);
        boolean result = fileSys.makeDir(dir);
        if (result && isParent(workDir, dir)) {
            workDirInf = fileSys.getFileInf(workDir);
        }
        return result;
    }

    /**
     * �õ���ǰĿ¼�µ��ļ�����
     * @return �ļ��и���
     */
    public int getFileNum() {
        return workDirInf.getFileNum();
    }

    /**
     * �õ���ǰĿ¼�µ�start����ʼ��num���ļ���,���浽names��
     * @param names ���ڱ����ļ�����String����
     * @param start ��ʼ�ļ���
     * @param num   ����
     * @return      ʵ�ʵõ��ĸ���,�����,����-1
     */
    public int listFiles(String[] names, int start, int num) {
        return workDirInf.listFiles(names, start, num);
    }

    public int fopen(Getable source, int fileName, int openMode) {
        int num = -1;
        //ָʾ�ļ�ָ��λ��,true��ͷ,falseΪ��β
        boolean pointer = true;
        //�Ƿ����ԭ���ļ�
        boolean clear = false;
        for (int index = 0; index < MAX_FILE_COUNT; index++) {
            if (usable[index]) {
                num = index;
                break;
            }
        }
        if (num == -1) {
            return 0;
        }
        String name = getFileName(source, fileName);
        String mode = getString(source, openMode);
        FileSystem.Info inf = fileSys.getFileInf(name);
        //System.out.println("fopen: " + name + "," + mode + "," + inf);
        if (READ_MODE.equals(mode) || READ_B_MODE.equals(mode)) {
            if (!(inf.isFile() && inf.canRead())) {
                return 0;
            }
            canRead[num] = true;
            canWrite[num] = false;
        }
        else if (READ_PLUS_MODE.equals(mode) || READ_B_PLUS_MODE.equals(mode)) {
            if (!(inf.isFile() && inf.canRead() && inf.canWrite())) {
                return 0;
            }
            canRead[num] = true;
            canWrite[num] = true;
        }
        else if (WRITE_MODE.equals(mode) || WRITE_B_MODE.equals(mode)) {
            if (inf.isFile() && !inf.canWrite()) {
                return 0;
            }
            clear = true;
            canRead[num] = false;
            canWrite[num] = true;
        }
        else if (WRITE_PLUS_MODE.equals(mode) || WRITE_B_PLUS_MODE.equals(mode)) {
            if (inf.isFile() && !inf.canWrite()) {
                return 0;
            }
            clear = true;
            canRead[num] = true;
            canWrite[num] = true;
        }
        else if (APPEND_MODE.equals(mode) || APPEND_B_MODE.equals(mode)) {
            if (!(inf.isFile() && inf.canWrite())) {
                return 0;
            }
            canRead[num] = false;
            canWrite[num] = true;
            pointer = false;
        }
        else if (APPEND_PLUS_MODE.equals(mode) || APPEND_B_PLUS_MODE.equals(mode)) {
            if (!(inf.isFile() && inf.canRead() && inf.canWrite())) {
                return 0;
            }
            canRead[num] = true;
            canWrite[num] = true;
            pointer = false;
        }
        else {
            return 0;
        }
        VirtualFile file = files[num];
        if (clear) {
            file.refresh();
        }
        else {
            int length = 0;
            try {
                InputStream in = fileSys.getInputStream(name);
                file.readFromStream(in);
                length = file.limit();
                in.close();
            } catch (Exception ex) {
                return 0;
            }
            file.position(pointer ? 0 : length);
        }
        fileNames[num] = name;
        usable[num] = false;
        return num | 0x80;
    }

    public void fclose(int fp) {
        if ((fp & 0x80) == 0) {
            return;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT) {
            return;
        }
        if (usable[fp]) {
            return;
        }
        if (canWrite[fp]) {
            try {
                OutputStream out = fileSys.getOutputStream(fileNames[fp]);
                files[fp].writeToStream(out);
                if (isParent(workDir, fileNames[fp])) {
                    workDirInf = fileSys.getFileInf(workDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Here:" + fileNames[fp] + "," + e.getMessage());
            //do nothing
            }
        }
        usable[fp] = true;
    }

    public int getc(int fp) {
        if ((fp & 0x80) == 0) {
            return -1;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT) {
            return -1;
        }
        if (usable[fp] || !canRead[fp]) {
            return -1;
        }
        return files[fp].getc();
    }

    public int putc(int c, int fp) {
        if ((fp & 0x80) == 0) {
            return -1;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT) {
            return -1;
        }
        if (usable[fp] || !canWrite[fp]) {
            return -1;
        }
        return files[fp].putc(c);
    }

    public int fread(Setable dest, int addr, int size, int fp) {
        if ((fp & 0x80) == 0) {
            return 0;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT) {
            return 0;
        }
        if (usable[fp] || !canRead[fp]) {
            return 0;
        }
        VirtualFile file = files[fp];
        int count = 0, b;
        while (count < size && (b = file.getc()) != -1) {
            dest.setByte(addr++, (byte) b);
            count++;
        }
        return count;
    }

    public int fwrite(Getable source, int addr, int size, int fp) {
        if ((fp & 0x80) == 0) {
            return 0;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT) {
            return 0;
        }
        if (usable[fp] || !canWrite[fp]) {
            return 0;
        }
        VirtualFile file = files[fp];
        int count = 0, b;
        while (count < size) {
            b = source.getByte(addr++);
            if (file.putc(b & 0xff) == -1) {
                break;
            }
            count++;
        }
        return count;
    }

    public boolean deleteFile(Getable source, int addr) {
        String file = getFileName(source, addr);
        boolean result = fileSys.deleteFile(file);
        //�����ǰĿ¼��Ϣ���޸�,����֮
        if (result && isParent(workDir, file)) {
            workDirInf = fileSys.getFileInf(workDir);
        }
        return result;
    }

    public int fseek(int fp, int offset, int base) {
        if ((fp & 0x80) == 0) {
            return -1;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT || usable[fp]) {
            return -1;
        }
        VirtualFile file = files[fp];
        int pos = 0;
        switch (base) {
            case SEEK_SET:
                pos = offset;
                break;
            case SEEK_CUR:
                pos = file.position() + offset;
                break;
            case SEEK_END:
                pos = file.limit() + offset;
                break;
            default:
                return -1;
        }
        return file.position(pos);
    }

    public int ftell(int fp) {
        if ((fp & 0x80) == 0) {
            return -1;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT || usable[fp]) {
            return -1;
        }
        return files[fp].position();
    }

    public boolean feof(int fp) {
        if ((fp & 0x80) == 0) {
            return true;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT || usable[fp]) {
            return true;
        }
        return files[fp].position() == files[fp].limit();
    }

    public void rewind(int fp) {
        if ((fp & 0x80) == 0) {
            return;
        }
        fp &= 0x7f;
        if (fp >= MAX_FILE_COUNT || usable[fp]) {
            return;
        }
        files[fp].position(0);
    }

    public void dispose() {
        for (int index = 0; index < MAX_FILE_COUNT; index++) {
            fclose(index | 0x80);
        }
    }

    /**
     * �ж�dir�Ƿ���file�ĸ�Ŀ¼
     */
    private boolean isParent(String dir, String file) {
        if (file.startsWith(dir)) {
            int pos = file.indexOf('/', dir.length());
            return pos == -1 || pos == file.length() - 1;
        }
        return false;
    }

    private String getFileName(Getable src, int addr) {
        String name = getString(src, addr);
        if (!name.startsWith("/")) {
            name = workDir + name;
        }
        return name;
    }

    private String getString(Getable src, int addr) {
        int length = 0;
        byte b;
        while ((b = src.getByte(addr++)) != 0) {
            strBuf[length++] = b;
        }
        try {
            return new String(strBuf, 0, length, "gb2312");
        } catch (UnsupportedEncodingException uee) {
            return new String(strBuf, 0, length);
        }
    }
}
