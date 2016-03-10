package exceptions;

public class ConnectionCloseException extends Exception
{
	public ConnectionCloseException()
	{
		super("Failed to close connection");
	}
}
