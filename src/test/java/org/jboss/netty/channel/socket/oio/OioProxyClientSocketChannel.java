package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channels;

import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Proxy;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 12:09 AM To change this template
 * use File | Settings | File Templates.
 */
public class OioProxyClientSocketChannel extends OioSocketChannel
{
	volatile PushbackInputStream in_;
	volatile OutputStream out_;

	OioProxyClientSocketChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, Proxy proxy)
	{
		super(null, factory, pipeline, sink, new Socket(proxy));
		Channels.fireChannelOpen(this);
	}

	@Override
	PushbackInputStream getInputStream()
	{
		return in_;
	}

	@Override
	OutputStream getOutputStream()
	{
		return out_;
	}
}
