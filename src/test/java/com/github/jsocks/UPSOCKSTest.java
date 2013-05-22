package com.github.jsocks;

import com.github.jsocks.socks.*;
import com.github.jsocks.socks.server.*;
import org.junit.Test;

import java.net.Socket;
import java.util.logging.Logger;

/**
 * Test for the UserPassword Auth and for ProxyServer
 */
public class UPSOCKSTest
{
    Logger log_ = Logger.getLogger(UPSOCKSTest.class.getName());

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
			return (user.equals(this.user) && password.equals(this.password));
		}
	}

	@Test
	public void testUPAuth()
	{
        log_.info("Starting the ProxyServer.");

		UserValidationMock us = new UserValidationMock("user", "password");
		UserPasswordAuthenticator auth = new UserPasswordAuthenticator(us);
		ProxyServer server = new ProxyServer(auth);

		server.setLog(System.out);
		server.start(1080);

        log_.info("Stopping the ProxyServer.");

        server.stop();
	}
}
