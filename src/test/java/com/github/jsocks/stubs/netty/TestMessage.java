package com.github.jsocks.stubs.netty;

/**
 * Created with IntelliJ IDEA. User: denlion Date: 13-05-23 Time: 1:02 AM To change this template
 * use File | Settings | File Templates.
 */
public class TestMessage
{

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	private String type;

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	private String message;

	public TestMessage()
	{
	}

	public TestMessage(String type, String message)
	{
		this.type = type;
		this.message = message;
	}

}
