package org.clamshellcli.wattsup;

import java.io.IOException;
import java.util.Map;

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;

import org.clamshellcli.wattsup.CommandDescriptor.Arg;

import wattsup.data.WattsUpConfig;
import wattsup.meter.WattsUp;

public class ConnectCommand implements Command
{
    /**
     * The {@link Descriptor} for this {@link Command}.
     */
    private static final CommandDescriptor DESCRIPTOR = new CommandDescriptor("connect", "Connects to the power meter device.",
            "connect [port:<Serial Port name>]", new Arg("port",
                    "The serial port name to connect to device. Example: /dev/ttyUSB0 or COM3."));

    @Override
    public void plug(Context ctx)
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Context ctx)
    {
        Map<String, Object> args = (Map<String, Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        Object port = args.get(DESCRIPTOR.getName());
        WattsUp wattsUp = new WattsUp(new WattsUpConfig().withPort(port.toString()));
        
        try
        {
            wattsUp.connect();
            ctx.putValue("wattsUp", wattsUp);
        }
        catch (IOException exception)
        {
            ctx.getIoConsole().writeOutput(exception.getMessage());
        }
        
        return null;
    }

    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }
}
