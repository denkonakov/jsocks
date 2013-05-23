package com.github.jsocks;

import com.github.jsocks.socks.*;
import com.github.jsocks.socks.server.*;
import com.github.jsocks.stubs.TestClient;
import com.github.jsocks.stubs.TestServer;
import com.github.jsocks.stubs.TestService;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

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
        log_.info("Starting the Test Services.");

        TestServer.startServerWithAllServices();

        log_.info("Starting the ProxyServer.");

		UserValidationMock us = new UserValidationMock("user", "password");
		UserPasswordAuthenticator auth = new UserPasswordAuthenticator(us);
		ProxyServer server = new ProxyServer(auth);

		server.setLog(System.out);
		server.start(1080);

        log_.info("Creating the Test Client.");

        try {
            Socket directSock = new Socket("localhost", TestService.servicePorts[TestService.CONNECT]);

            InputStream in = directSock.getInputStream();
            OutputStream out = directSock.getOutputStream();

            assertNotNull(in);
            assertNotNull(out);
        } catch (IOException e) {
            assertTrue(false);
        }

        log_.info("Stopping the ProxyServer.");

        server.stop();
	}
}
