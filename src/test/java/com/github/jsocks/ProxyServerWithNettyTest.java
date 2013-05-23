package com.github.jsocks;

import com.github.jsocks.socks.ProxyServer;
import com.github.jsocks.socks.server.UserPasswordAuthenticator;
import com.github.jsocks.socks.server.UserValidation;
import com.github.jsocks.stubs.netty.HttpTestClient;
import com.github.jsocks.stubs.netty.HttpTestServer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.junit.Test;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Client/Server Netty test with ProxyServer.
 * 
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-22 Time: 11:13 PM To change this template
 * use File | Settings | File Templates.
 */
public class ProxyServerWithNettyTest
{
	Logger log_ = Logger.getLogger(ProxyServerWithNettyTest.class.getName());

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
	public void testNettyProxyInit()
	{
		log_.info("Starting server...");
		count_.set(0);
		new HttpTestServer(49200, serverHandler_).run();

		log_.info("Starting the ProxyServer.");

		UserValidationMock us = new UserValidationMock("user", "password");
		UserPasswordAuthenticator auth = new UserPasswordAuthenticator(us);
		ProxyServer server = new ProxyServer(auth);

		ProxyServer.setLog(System.out);
		server.start(1080);

		log_.info("Connecting client...");
		new HttpTestClient(49200, 1080, new SimpleChannelUpstreamHandler()).run();

		assertTrue(count_.get() > 0);
	}

}
