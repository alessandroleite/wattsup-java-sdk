package wattsup.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public final class Main
{
    /**
     * Constructor.
     */
    private Main()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param args
     *            The console arguments.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException
    {
        OutputStream out = System.out;

        final String exportFilePath = System.getProperty("export.file.path");
        final String host = System.getProperty("wattsup.server.host", "localhost");
        final int port = Integer.valueOf(System.getProperty("wattsup.server.port", "9090"));

        if (exportFilePath != null && !exportFilePath.isEmpty())
        {
            out = new FileOutputStream(new File(exportFilePath));
        }

        try (Socket socket = new Socket(host, port))
        {
            int i;
            while ((i = socket.getInputStream().read()) != -1)
            {
                out.write(i);
            }
        }
        out.close();
    }
}
