/**
 *     WattsUp-J is a Java application to interact with the Watts up? power meter.
 *     Copyright (C) 2013  Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *     Contributors:
 *         Alessandro Ferreira Leite - the initial implementation.
 */
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
