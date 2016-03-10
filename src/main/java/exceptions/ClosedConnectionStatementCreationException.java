package exceptions;

public class ClosedConnectionStatementCreationException extends Exception
{
	public ClosedConnectionStatementCreationException()
	{
		super("Failed to create statement, because connection is closed");
	}
}
