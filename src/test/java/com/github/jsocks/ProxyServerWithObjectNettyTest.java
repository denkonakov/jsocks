package com.github.jsocks;

import com.github.jsocks.socks.server.UserValidation;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.junit.Test;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 12:56 AM To change this template
 * use File | Settings | File Templates.
 */
public class ProxyServerWithObjectNettyTest
{
	Logger log_ = Logger.getLogger(ProxyServerWithHttpNettyTest.class.getName());

	static class UserValidationMock implements UserValidation
	{
		String user;
		String password;

		UserValidationMock(String user, String password)
		{
			this.user = user;
			this.password = password;
		}

		public boolean isUserValid(String user, String password, Socket s)
		{
			System.err.println("User:" + user + "\tPassword:" + password);
			System.err.println("Socket:" + s);
//			return (user.equals(this.user) && password.equals(this.password));
			return true;
		}
	}

	private StubServerHandler serverHandler_ = new StubServerHandler();

	private static AtomicInteger count_ = new AtomicInteger(0);

	static class StubServerHandler extends SimpleChannelUpstreamHandler
	{
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
		{
			count_.getAndIncrement();
			ctx.getChannel().close();
		}
	}

	@Test
	public void testNettyProxySendRecieved()
	{

	}
}
