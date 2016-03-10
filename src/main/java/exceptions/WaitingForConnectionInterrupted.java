package exceptions;

public class WaitingForConnectionInterrupted extends Exception
{
	public WaitingForConnectionInterrupted()
	{
		super("Failed to connect to DB");
	}
}
