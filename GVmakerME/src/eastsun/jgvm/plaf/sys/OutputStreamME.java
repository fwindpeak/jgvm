package eastsun.jgvm.plaf.sys;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Eastsun
 * @version 2008-2-28
 */
class OutputStreamME extends OutputStream {

    OutputStream out;
    FileConnection fc;

    public OutputStreamME(String fileName) throws IOException {
        fc = (FileConnection) Connector.open(fileName, Connector.READ_WRITE);
        if (fc.exists()) {
            fc.delete();
        }
        fc.create();
        out = fc.openOutputStream();
    }

    public void close() throws IOException {
        out.close();
        fc.close();
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    public void write(int b) throws IOException {
        out.write(b);
    }
}
