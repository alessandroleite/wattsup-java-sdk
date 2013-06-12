package org.clamshellcli.wattsup;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;
import org.clamshellcli.core.AnInputController;
import org.clamshellcli.core.ShellException;


public class WattsUpController extends AnInputController
{
    /**
     * The namespace of Watts Up? commands.
     */
    private static final String WATTSUP_NAMESPACE = "wattsup";

    /**
     * The commands founds by this controller.
     */
    private Map<String, Command> commands_;

    /**
     * The library to work with JSON format.
     */
    private Gson gson_;

    @Override
    public boolean handle(Context ctx)
    {
        boolean handled = false;
        String cmdLine = (String) ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        if (cmdLine != null && !cmdLine.trim().isEmpty())
        {
            String[] tokens = cmdLine.split("\\s+");
            String cmdName = tokens[0];
            Map<String, Object> argsMap = null;

            // if there are arguments
            if (tokens.length > 1)
            {
                String argsString = convertToStringSeparedByComma(Arrays.copyOfRange(tokens, 1, tokens.length));
                String argsJson = "{" + argsString + "}";
                try
                {
                    Type mapType = new TypeToken<Map<String, Object>>() { } .getType();
                    argsMap = gson_.fromJson(argsJson, mapType);
                }
                catch (JsonSyntaxException ex)
                {
                    ctx.getIoConsole().writeOutput(
                            String.format("%nUnable to parse command parameters [%s]: " + " %s.%n%n", argsJson, ex.getMessage()));
                    handled = true;
                }
            }

            Command cmd = commands_ != null ? commands_.get(cmdName) : null;
            
            if (cmd != null)
            {
                ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
                try
                {
                    cmd.execute(ctx);
                }
                catch (ShellException se)
                {
                    ctx.getIoConsole().writeOutput(String.format("%n%s%n%n", se.getMessage()));
                }
                handled = true;
            }
            else
            {
                handled = false;
            }

        }
        return handled;
    }

    @Override
    public void plug(Context plug)
    {
        super.plug(plug);
        gson_ = new Gson();

        List<Command> wattsUpsCommands = plug.getCommandsByNamespace(WATTSUP_NAMESPACE);

        if (wattsUpsCommands.isEmpty())
        {
            commands_ = plug.mapCommands(wattsUpsCommands);

            Set<String> cmdHints = new TreeSet<String>();

            // plug each Command instance and collect input hints
            for (Command cmd : wattsUpsCommands)
            {
                cmd.plug(plug);
                cmdHints.addAll(collectInputHints(cmd));
            }

            // save expected command input hints
            setExpectedInputs(cmdHints.toArray(new String[0]));
        }
        else
        {
            plug.getIoConsole().writeOutput(String.format("%nNo commands were found for input controller" + " [%s].%n%n", this.getClass().getName()));
        }
    }

    /**
     * Returns a {@link String} with the values of {@code params} separated by comma(,).
     * 
     * @param params The array to be converted to n {@link String}. Might not be <code>null</code>.
     * @return A {@link String} with the values of {@code params} separated by comma(,).
     * @throws NullPointerException If {@code params} is <code>null</code>.
     */
    private String convertToStringSeparedByComma(final String[] params)
    {
        StringBuffer buff = new StringBuffer();

        for (int i = 0; i < Objects.requireNonNull(params.length); i++)
        {
            buff.append(params[i].trim());
            if (i < (params.length - 1))
            {
                buff.append(",");
            }
        }
        return buff.toString();
    }
}
