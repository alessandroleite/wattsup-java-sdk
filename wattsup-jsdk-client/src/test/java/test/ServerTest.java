package test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket server = new ServerSocket(9090);

        while (true)
        {
            Socket client = server.accept();

            BufferedInputStream in = new BufferedInputStream(client.getInputStream());
//            OutputStream output = client.getOutputStream();

            StringBuilder msg = new StringBuilder();
            int i;
            while ((i = in.read()) != -1)
            {
                msg.append((char) i);
            }
            System.err.println(msg.toString());
            msg.setLength(0);
        }
    }
}
