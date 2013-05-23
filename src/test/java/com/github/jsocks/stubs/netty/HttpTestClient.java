package com.github.jsocks.stubs.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.oio.OioProxyClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-22 Time: 11:36 PM To change this template
 * use File | Settings | File Templates.
 */
public class HttpTestClient
{
	private final int port;
	private final int proxyPort;

	private final SimpleChannelUpstreamHandler handler_;

	public HttpTestClient(int port, int proxyPort, SimpleChannelUpstreamHandler handler)
	{
		this.port = port;
		this.proxyPort = proxyPort;
		this.handler_ = handler;
	}

	public void run()
	{
		// Configure the client.
//		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
//			Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", proxyPort));
		ClientBootstrap bootstrap = new ClientBootstrap(new OioProxyClientSocketChannelFactory(
			Executors.newCachedThreadPool(), proxy));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory()
		{
			@Override
			public ChannelPipeline getPipeline() throws Exception
			{
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("codec", new HttpClientCodec());
				pipeline.addLast("handler", handler_);

				return pipeline;
			}
		});

		SocketAddress address = new InetSocketAddress("localhost", port);

		bootstrap.setOption("remoteAddress", address);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("configureBlocking", false);
		bootstrap.setOption("connectTimeoutMillis", 5000);

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(address);

		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess())
		{
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}

		// Prepare the HTTP request.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://test/");
		request.setHeader(HttpHeaders.Names.HOST, "localhost");
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

		// Set some example cookies.
		CookieEncoder httpCookieEncoder = new CookieEncoder(false);
		httpCookieEncoder.addCookie("my-cookie", "foo");
		httpCookieEncoder.addCookie("another-cookie", "bar");
		request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

		// Send the HTTP request.
		channel.write(request);

		// Wait for the server to close the connection.
		channel.getCloseFuture().awaitUninterruptibly();

		// Shut down executor threads to exit.
		bootstrap.releaseExternalResources();
	}
}
