package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.*;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.util.internal.DeadLockProofWorker;

import java.io.PushbackInputStream;
import java.net.SocketAddress;
import java.util.concurrent.Executor;

import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 12:02 AM To change this template
 * use File | Settings | File Templates.
 */
public class OioProxyClientSocketPipelineSink extends AbstractChannelSink
{
	private final Executor workerExecutor;

	OioProxyClientSocketPipelineSink(Executor workerExecutor)
	{
		this.workerExecutor = workerExecutor;
	}

	@Override
	public void eventSunk(ChannelPipeline pipeline, ChannelEvent e) throws Exception
	{
		OioProxyClientSocketChannel channel = (OioProxyClientSocketChannel) e.getChannel();
		ChannelFuture future = e.getFuture();
		if (e instanceof ChannelStateEvent)
		{
			ChannelStateEvent stateEvent = (ChannelStateEvent) e;
			ChannelState state = stateEvent.getState();
			Object value = stateEvent.getValue();
			switch (state)
			{
				case OPEN:
					if (Boolean.FALSE.equals(value))
					{
						OioWorker.close(channel, future);
					}
					break;
				case BOUND:
					if (value != null)
					{
						bind(channel, future, (SocketAddress) value);
					}
					else
					{
						OioWorker.close(channel, future);
					}
					break;
				case CONNECTED:
					if (value != null)
					{
						connect(channel, future, (SocketAddress) value);
					}
					else
					{
						OioWorker.close(channel, future);
					}
					break;
				case INTEREST_OPS:
					OioWorker.setInterestOps(channel, future, ((Integer) value).intValue());
					break;
			}
		}
		else if (e instanceof MessageEvent)
		{
			OioWorker.write(channel, future, ((MessageEvent) e).getMessage());
		}
	}

	private void bind(OioProxyClientSocketChannel channel, ChannelFuture future, SocketAddress localAddress)
	{
		try
		{
			channel.socket.bind(localAddress);
			future.setSuccess();
			fireChannelBound(channel, channel.getLocalAddress());
		}
		catch (Throwable t)
		{
			future.setFailure(t);
			fireExceptionCaught(channel, t);
		}
	}

	private void connect(OioProxyClientSocketChannel channel, ChannelFuture future, SocketAddress remoteAddress)
	{

		boolean bound = channel.isBound();
		boolean connected = false;
		boolean workerStarted = false;

		future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

		try
		{
			channel.socket.connect(remoteAddress, channel.getConfig().getConnectTimeoutMillis());
			connected = true;

			// Obtain I/O stream.
			channel.in_ = new PushbackInputStream(channel.socket.getInputStream(), 1);
			channel.out_ = channel.socket.getOutputStream();

			// Fire events.
			future.setSuccess();
			if (!bound)
			{
				fireChannelBound(channel, channel.getLocalAddress());
			}
			fireChannelConnected(channel, channel.getRemoteAddress());

			// Start the business.
			DeadLockProofWorker.start(workerExecutor, new ThreadRenamingRunnable(new OioWorker(channel),
				"Old I/O client worker (" + channel + ')'));
			workerStarted = true;
		}
		catch (Throwable t)
		{
			future.setFailure(t);
			fireExceptionCaught(channel, t);
		}
		finally
		{
			if (connected && !workerStarted)
			{
				OioWorker.close(channel, future);
			}
		}
	}
}
