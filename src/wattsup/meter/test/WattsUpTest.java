package wattsup.meter.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wattsup.data.WattsUpConfig;
import wattsup.data.WattsUpPacket;
import wattsup.event.WattsUpDataAvailableEvent;
import wattsup.listener.WattsUpDataAvailableListener;
import wattsup.meter.WattsUp;

public class WattsUpTest 
{
	
	
	public static void main(String[] args) throws IOException 
	{
		final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		final WattsUp meter = new WattsUp(new WattsUpConfig().withPort(args[0]));
		meter.registerListener(new WattsUpDataAvailableListener() 
		{
			@Override
			public void processDataAvailable(final WattsUpDataAvailableEvent event) 
			{
				WattsUpPacket[] values = event.getValue();
				System.out.printf("[%s] %s\n",  format.format(new Date()),  values.length);
			}
		});
		
		meter.connect();
		meter.reset();		
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run() 
			{
				meter.disconnect();
			}
		});
	}
}
