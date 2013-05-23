package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.util.internal.ExecutorUtil;

import java.net.Proxy;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 12:00 AM To change this template
 * use File | Settings | File Templates.
 */
public class OioProxyClientSocketChannelFactory implements ClientSocketChannelFactory
{
	private Proxy proxy_ = null;

	private final Executor workerExecutor_;
	final OioProxyClientSocketPipelineSink sink;

	public OioProxyClientSocketChannelFactory(Executor workerExecutor, Proxy proxy)
	{
		if (workerExecutor == null)
		{
			throw new NullPointerException("workerExecutor");
		}
		if (proxy == null)
		{
			throw new NullPointerException("proxy");
		}
		this.workerExecutor_ = workerExecutor;
		proxy_ = proxy;

		sink = new OioProxyClientSocketPipelineSink(workerExecutor);
	}

	@Override
	public SocketChannel newChannel(ChannelPipeline pipeline)
	{
		return new OioProxyClientSocketChannel(this, pipeline, sink, proxy_);
	}

	@Override
	public void releaseExternalResources()
	{
		ExecutorUtil.terminate(workerExecutor_);
	}
}
