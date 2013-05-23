package com.github.jsocks.stubs.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 1:04 AM To change this template
 * use File | Settings | File Templates.
 */
public class KryoDecoder extends OneToOneDecoder
{
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}
}
