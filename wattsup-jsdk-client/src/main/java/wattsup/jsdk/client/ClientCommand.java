/**
 *     Copyright (C) 2013 Contributors
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
 */
package wattsup.jsdk.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import wattsup.jsdk.client.jcommander.converter.CommandTypeConverter;
import wattsup.jsdk.client.jcommander.converter.IDConverter;
import wattsup.jsdk.client.jcommander.validator.RemoteCommandNameValidator;
import wattsup.jsdk.core.data.ID;
import wattsup.jsdk.core.serialize.java.ObjectSerializer;
import wattsup.jsdk.remote.data.CommandType;
import wattsup.jsdk.remote.data.Request;
import wattsup.jsdk.remote.data.Response;

import com.beust.jcommander.Parameter;

public class ClientCommand
{
    /**
     * 
     */
    @Parameter(names = { "-host", "-server" }, description = "The server name to connect to.", required = true)
    private String host_;

    /**
     * 
     */
    @Parameter(names = { "-p", "-port" }, description = "The server port.", required = true)
    private int port_;

    @Parameter(names = { "-id", "-req-id" }, converter = IDConverter.class)
    private ID id_;

    /**
     * 
     */
    @Parameter(names = { "-method", "-name", "-region" }, required = true)
    private String name_;

    /**
     * 
     */
    @Parameter(names = { "-c", "-cmd", "-command" }, required = true, converter = CommandTypeConverter.class, validateValueWith = RemoteCommandNameValidator.class, description = "The command to execute. The valid values are:[start, dump, end].")
    private CommandType command_;

    /**
     * 
     */
    @Parameter(names = { "-of", "-output-format" }, description = "The output format. The valid values are:[CSV, JSON, PLAIN].")
    private OutputFormat outputFormat_ = OutputFormat.CSV;

    /**
     * 
     */
    @Parameter(names = { "-timeout" }, description = "Timeout in milliseconds to wait for a response. Default is one minute.")
    private int timeout_ = 1 * 60 * 1000; // one minute

    public Response execute() throws UnknownHostException, IOException
    {
        ObjectSerializer serializer = new ObjectSerializer();

        Socket socket = null;
        try
        {
            socket = new Socket(host_, port_);
            socket.setSoTimeout(timeout_);

            Request request = Request.newRequest().withName(name_).withId(id_ == null ? ID.randomID() : id_).withCommand(command_);
            serializer.serialize(socket.getOutputStream(), request);

            return serializer.deserialize(socket.getInputStream(), socket.getInputStream().available());
        }
        finally
        {
            if (socket != null)
            {
                socket.close();
            }
        }
    }

    /**
     * Returns the {@link OutputFormat}.
     * 
     * @return the {@link OutputFormat}.
     */
    public OutputFormat getOutputFormat()
    {
        return outputFormat_;
    }

    /**
     * Returns the command to execute.
     * 
     * @return the command to execute.
     */
    public CommandType getCommand()
    {
        return command_;
    }
}
