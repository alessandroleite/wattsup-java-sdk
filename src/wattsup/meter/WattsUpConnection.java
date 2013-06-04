package wattsup.meter;
import gnu.io.NRSerialPort;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import wattsup.data.WattsUpConfig;
import wattsup.data.WattsUpConfig.Delimiter;
import wattsup.data.WattsUpPacket;
import wattsup.data.command.WattsUpCommand;

class WattsUpConnection implements Closeable
{
	
	/**
	 * The reference for this connection {@link InputStream}.
	 */
	private InputStream input_;
	
	/**
	 * The reference for this connection {@link OutputStream}.
	 */
	private OutputStream output_;
	
	/**
	 * The reference for the serial port implementation.
	 */
	private final NRSerialPort serial;

	/**
	 * The reference to the {@link WattsUpConfig} with the data related to the configuration of the meter, such as port, field and line delimiter. 
	 */
	private final WattsUpConfig configuration_;
	
	protected WattsUpConnection(WattsUpConfig configuration)
	{
		serial = new NRSerialPort(Objects.requireNonNull(configuration).getPort(), 115200);
		this.configuration_ = configuration;
	}
	
	/**
	 * Returns <code>true</code> if the connection with the meter was successfully established.
	 * @return <code>true</code> if the connection with the meter was successfully established.
	 */
	protected boolean connect()
	{
		boolean connected = serial.connect();
		input_ = new DataInputStream(serial.getInputStream());
		output_ = new DataOutputStream(serial.getOutputStream());
		
		return connected;
	}
	
	/**
	 * Returns <code>true</code> if the power meter is connected.
	 * 
	 * @return <code>true</code> if the power meter is connected.
	 */
	public boolean isConnected() 
	{
		return this.serial.isConnected();
	}
	
	/**
	 * Close the connection with the meter.
	 * @see #close()
	 */
	protected void disconnect()
	{
		try 
		{
			this.close();
		} catch (IOException ignore) 
		{
			if (this.isConnected())
			{
				this.serial.disconnect();
			}
		}
	}
	
	/**
	 * Executes a command and returns the reply of the meter.
	 * 
	 * @param command The command to be executed. Might not be <code>null</code>.
	 * @param arguments The arguments for the command.
	 * @return The response of the meter.
	 * @throws IOException If the meter is not available or the communication is not possible.
	 */
	protected WattsUpPacket[] execute(final WattsUpCommand command, final String ... arguments) throws IOException
	{
		validate(command, arguments);
		
		String instruction = getWattsUpInstructionFormat(command, arguments);
		
		output_.write(instruction.getBytes());
		
		//TODO create a type to encapsulate the meter's response.
		WattsUpPacket[] response = new WattsUpPacket[0];
		
		if (command.waitResponse())
		{
			String reply = read();
			response = WattsUpPacket.parser(reply, configuration_.getDelimiter());
		}
		
		return response;
	}

	/**
	 * Formats and returns the command in the format required by the power meter.
	 * 
	 * @param command The commands (command and sub-command) to be executed.
	 * @param arguments The arguments of the commands.
	 * @return The command in the format required by the power meter.
	 */
	protected String getWattsUpInstructionFormat(final WattsUpCommand command, final String... arguments) 
	{
		StringBuilder sb = new StringBuilder(command.getCommands()).append(command.getNumberOfArguments() == 0 ? ",0" : Delimiter.COMMA.getSymbol());
		
		if (arguments != null) 
		{
			for (int i = 0; i < arguments.length; i++) 
			{
				sb.append(arguments[i]);
				
				if (i < arguments.length - 1)
				{
					sb.append(Delimiter.COMMA.getSymbol());	
				}
			}
		}
		
		sb.append(Delimiter.SEMICOLON.getSymbol());
		return sb.toString();
	}

	/**
	 * Validate if all arguments for a command are valid.
	 * 
	 * @param command The command to be executed. Might not be <code>null</code>.
	 * @param arguments The arguments of the command. Might not be <code>null</code> if the command requires at least one argument.
	 * @throws NullPointerException If the command is <code>null</code> or if the command requires arguments and the given argument is <code>null</code>.
	 * @throws IllegalArgumentException If the number of arguments required by the command is different from the given.
	 */
	protected void validate(final WattsUpCommand command, final String... arguments) 
	{
		if (command == null)
		{
			throw new NullPointerException("The command might not be null!");
		}
		
		if (command.getNumberOfArguments() > 0 && arguments == null)
		{
			throw new NullPointerException(String.format("This command %s requires %s argument(s) but it was null!", command.name(), command.getNumberOfArguments()));
		}
		
		if (arguments != null && command.getNumberOfArguments() != arguments.length)
		{
			throw new IllegalArgumentException(String.format("This command %s requires %s argument(s) but was found %s!", command.name(), command.getNumberOfArguments(), arguments.length));
		}
	}

	/**
	 * Returns the content available in the input.
	 * 
	 * @return The content available in the input.
	 * @throws IOException If the {@link InputStream} is closed or in an invalid state.
	 */
	protected String read() throws IOException 
	{
		StringBuilder sb = new StringBuilder();
		
		int i;
		while ((i = input_.read()) != -1) 
		{
			sb.append((char) i);
		}

		return sb.toString();
	}
	

	@Override
	public void close() throws IOException 
	{
		if (this.isConnected())
		{
			this.serial.disconnect();
		}
		
		if (this.input_ != null) 
		{
			this.input_.close();
			this.input_ = null;
		}
		
		if (this.output_ != null) 
		{
			this.output_.close();
			this.output_ = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable 
	{
		close();
		super.finalize();
	}

}
