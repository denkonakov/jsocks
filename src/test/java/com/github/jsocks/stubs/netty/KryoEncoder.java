package com.github.jsocks.stubs.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 1:05 AM To change this template
 * use File | Settings | File Templates.
 */
public class KryoEncoder extends OneToOneEncoder
{
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}
}
