package org.clamshellcli.wattsup;

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;
import org.clamshellcli.wattsup.CommandDescriptor.Arg;

public class ConfigurationCommand implements Command
{
    /**
     * 
     */
    private static final Descriptor COMMAND_DESCRIPTOR = new CommandDescriptor("config", "Define or read the parameter of the device.",
            "config [port:<Serial Port name>]", 
            new Arg("s", "Shows the parameters of the meter."), 
            new Arg("d", "Defines the parameters of the meter."));

    @Override
    public void plug(Context ctx)
    {
    }

    @Override
    public Object execute(Context ctx)
    {
        return null;
    }

    @Override
    public Descriptor getDescriptor()
    {
        return COMMAND_DESCRIPTOR;
    }
}
