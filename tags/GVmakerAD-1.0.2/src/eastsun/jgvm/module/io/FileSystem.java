package eastsun.jgvm.module.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件系统接口,通过该接口将GVM中的文件操作映射到系统中的文件操作<p>
 * 注意:方法中涉及的文件名都是GVM中用到的文件名,不一定与底层实际文件一一对应.其具体解释由实现者提供<p>
 * @author Eastsun
 * @version 2008-2-23
 */
public interface FileSystem {

    /**
     * 一个描述文件信息的接口
     * @author Eastsun
     * @version 2008-2-25
     */
    public interface Info {

        /**
         * 是否为一个文件
         */
        public boolean isFile();

        /**
         * 是否为一个文件夹
         * @return 当仅当存在且为文件夹时返回true
         */
        public boolean isDirectory();

        /**
         * 该文件夹或文件是否可读
         * @return 当且仅当存在且可读时返回true
         */
        public boolean canRead();

        /**
         * 该文件夹或文件是否可写
         * @return 当仅当存在且可写时返回true
         */
        public boolean canWrite();

        /**
         * 得到文件夹下文件个数
         * @return 为文件夹时返回其目录下文件个数(含子目录);否则返回-1
         */
        public int getFileNum();

        /**
         * 得到目录下第start个开始的num个文件名,保存到names中
         * @param names 用于保存文件名的String数组
         * @param start 开始文件号
         * @param num   个数
         * @return      实际得到的个数,如出错,返回-1
         */
        public int listFiles(String[] names, int start, int num);
    }

    /**
     * 得到该文件的InputStream,以读取其内容
     * @return in 当文件存在且canRead返回true时返回指向该文件的InputStream
     * @throws java.io.IOException 文件不存在或不可读或发生IO错误
     */
    public InputStream getInputStream(String fileName) throws IOException;

    /**
     * 得到该文件的OutputStream以向其写入内容<p>
     * 当文件不存在时会创建一个新的文件
     * @return out  返回指向该文件的OutputStream
     * @throws java.io.IOException 若文件不可写或发生IO错误
     */
    public OutputStream getOutputStream(String fileName) throws IOException;

    /**
     * 删除文件
     * @param fileName 文件名
     * @return true,如果删除成功
     */
    public boolean deleteFile(String fileName);

    /**
     * 建立文件夹
     * @param dirName 文件夹名
     * @return true,如果创建成功
     */
    public boolean makeDir(String dirName);

    /**
     * 得到指定文件/文件夹的相关信息<p>
     * 注意:这些信息只代表获得该信息时文件的情况,并不随着环境的变化而变化
     * @param fileName 文件名
     * @return 其相关信息
     */
    public Info getFileInf(String fileName);
}
