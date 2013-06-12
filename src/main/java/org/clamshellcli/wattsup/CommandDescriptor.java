package org.clamshellcli.wattsup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.clamshellcli.api.Command;

public class CommandDescriptor implements Command.Descriptor
{
    /**
     * 
     */
    public static final String NAMESPACE = "watts";

    /**
     * The command arguments.
     */
    private final Map<String, Arg> arguments_ = new HashMap<>();

    /**
     * The command's description.
     */
    private String description_;

    /**
     * The command's name.
     */
    private String name_;

    /**
     * The command's usage.
     */
    private String usage_;

    /**
     * 
     * @param name
     *            The command name. Might not be <code>null</code> or empty.
     * @param description
     *            This command description. It's useful to user.
     * @param usage
     *            The command usage.
     */
    public CommandDescriptor(String name, String description, String usage)
    {
        this.description_ = description;
        this.name_ = name;
        this.usage_ = usage;
    }

    /**
     * 
     * @param name
     *            The command name. Might not be <code>null</code> or empty.
     * @param description
     *            This command description. It's useful to user.
     * @param usage
     *            The command usage.
     * @param args
     *            The command's arguments.
     */
    public CommandDescriptor(String name, String description, String usage, Arg... args)
    {
        this(name, description, usage);

        for (Arg arg : args)
        {
            this.arguments_.put(arg.getName(), arg);
        }
    }

    @Override
    public Map<String, String> getArguments()
    {
        return null;
    }

    @Override
    public String getDescription()
    {
        return this.description_;
    }

    @Override
    public String getName()
    {
        return this.name_;
    }

    @Override
    public String getNamespace()
    {
        return NAMESPACE;
    }

    @Override
    public String getUsage()
    {
        return this.usage_;
    }

    public static class Arg implements Serializable
    {
        /**
         * Serial code version <code>serialVersionUID</code> for serialization.
         */
        private static final long serialVersionUID = -7372809669449521274L;

        /**
         * The command parameter's name.
         */
        private final String name_;

        /**
         * The command parameter's description.
         */
        private final String description_;

        /**
         * 
         * @param name
         *            The argument name. Might not be <code>null</code>.
         * @param description
         *            The argument description.
         */
        public Arg(String name, String description)
        {
            this.name_ = name;
            this.description_ = description;
        }

        /**
         * @return the name_
         */
        public String getName()
        {
            return name_;
        }

        /**
         * @return the description_
         */
        public String getDescription()
        {
            return description_;
        }
    }
}
