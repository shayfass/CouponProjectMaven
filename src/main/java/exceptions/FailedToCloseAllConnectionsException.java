package exceptions;

public class FailedToCloseAllConnectionsException extends Exception
{
	public FailedToCloseAllConnectionsException()
	{
		super("Failed to close all connections");
	}
}
