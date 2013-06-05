wattsup
=======

What is it ?
------------

A Java application to communicate with the [Watts up?](https://www.wattsupmeters.com/secure/products.php?pn=0&wai=0&more=1) power meter to read the data. The communication is based in the protocol described in [https://www.wattsupmeters.com/secure/downloads/CommunicationsProtocol090824.pdf](https://www.wattsupmeters.com/secure/downloads/CommunicationsProtocol090824.pdf)

Usage
------

Here is a class that connect to the power meter during three minutes and print the measures in console. 

	public final class WattsUpTest
	{	    
	    /**
	     * Creates an {@link WattsUp} for monitoring during one minute.
	     * 
	     * @param args
	     *            The reference to the arguments.
	     * @throws IOException
	     *             If the power meter is not connected.
	     */
	    public static void main(String[] args) throws IOException
	    {
		final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		final WattsUp meter = new WattsUp(new WattsUpConfig().withPort(args[0]).scheduleDuration(3 * 60));

		meter.registerListener(new WattsUpDataAvailableListener()
		{
		    @Override
		    public void processDataAvailable(final WattsUpDataAvailableEvent event)
		    {
		        WattsUpPacket[] values = event.getValue();
		        System.out.printf("[%s] %s\n", format.format(new Date()), Arrays.toString(values));
		    }
		});
		meter.connect();
	    }
	}

### Run:

	java -cp lib/nrjavaserial-3.8.8.jar wattsup.meter.test.WattsUpTest /dev/ttyUSB0
   
Dependencies
--------------

This project uses the [nrjavaserial](https://code.google.com/p/nrjavaserial/) and [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

