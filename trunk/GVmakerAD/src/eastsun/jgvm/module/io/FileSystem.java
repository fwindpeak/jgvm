package eastsun.jgvm.module.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * �ļ�ϵͳ�ӿ�,ͨ���ýӿڽ�GVM�е��ļ�����ӳ�䵽ϵͳ�е��ļ�����<p>
 * ע��:�������漰���ļ�������GVM���õ����ļ���,��һ����ײ�ʵ���ļ�һһ��Ӧ.����������ʵ�����ṩ<p>
 * @author Eastsun
 * @version 2008-2-23
 */
public interface FileSystem {

    /**
     * һ�������ļ���Ϣ�Ľӿ�
     * @author Eastsun
     * @version 2008-2-25
     */
    public interface Info {

        /**
         * �Ƿ�Ϊһ���ļ�
         */
        public boolean isFile();

        /**
         * �Ƿ�Ϊһ���ļ���
         * @return ������������Ϊ�ļ���ʱ����true
         */
        public boolean isDirectory();

        /**
         * ���ļ��л��ļ��Ƿ�ɶ�
         * @return ���ҽ��������ҿɶ�ʱ����true
         */
        public boolean canRead();

        /**
         * ���ļ��л��ļ��Ƿ��д
         * @return �����������ҿ�дʱ����true
         */
        public boolean canWrite();

        /**
         * �õ��ļ������ļ�����
         * @return Ϊ�ļ���ʱ������Ŀ¼���ļ�����(����Ŀ¼);���򷵻�-1
         */
        public int getFileNum();

        /**
         * �õ�Ŀ¼�µ�start����ʼ��num���ļ���,���浽names��
         * @param names ���ڱ����ļ�����String����
         * @param start ��ʼ�ļ���
         * @param num   ����
         * @return      ʵ�ʵõ��ĸ���,�����,����-1
         */
        public int listFiles(String[] names, int start, int num);
    }

    /**
     * �õ����ļ���InputStream,�Զ�ȡ������
     * @return in ���ļ�������canRead����trueʱ����ָ����ļ���InputStream
     * @throws java.io.IOException �ļ������ڻ򲻿ɶ�����IO����
     */
    public InputStream getInputStream(String fileName) throws IOException;

    /**
     * �õ����ļ���OutputStream������д������<p>
     * ���ļ�������ʱ�ᴴ��һ���µ��ļ�
     * @return out  ����ָ����ļ���OutputStream
     * @throws java.io.IOException ���ļ�����д����IO����
     */
    public OutputStream getOutputStream(String fileName) throws IOException;

    /**
     * ɾ���ļ�
     * @param fileName �ļ���
     * @return true,���ɾ���ɹ�
     */
    public boolean deleteFile(String fileName);

    /**
     * �����ļ���
     * @param dirName �ļ�����
     * @return true,��������ɹ�
     */
    public boolean makeDir(String dirName);

    /**
     * �õ�ָ���ļ�/�ļ��е������Ϣ<p>
     * ע��:��Щ��Ϣֻ�����ø���Ϣʱ�ļ������,�������Ż����ı仯���仯
     * @param fileName �ļ���
     * @return �������Ϣ
     */
    public Info getFileInf(String fileName);
}
