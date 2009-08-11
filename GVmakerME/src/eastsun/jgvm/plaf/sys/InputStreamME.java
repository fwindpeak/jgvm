/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eastsun.jgvm.plaf.sys;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Administrator
 */
public class InputStreamME extends InputStream {

    private InputStream in;
    private FileConnection fc;

    public InputStreamME(String fileName) throws IOException {
        System.out.println("File open: "+fileName);
        fc = (FileConnection) Connector.open(fileName, Connector.READ);
        in = fc.openInputStream();
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
        fc.close();
    }

    public synchronized void mark(int pos) {
        in.mark(pos);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public int read() throws IOException {
        return in.read();
    }
}
