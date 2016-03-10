package exceptions;

public class FailedToJoinThreadInterruptedException extends Exception
{
	public FailedToJoinThreadInterruptedException()
	{
		super("Thread waiting to join was interrupted");
	}
}
