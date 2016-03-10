package exceptions;

public class UpdateDidNotExecuteException extends Exception
{
	public UpdateDidNotExecuteException()
	{
		super("Update did not execute");
	}
}
