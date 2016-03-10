package exceptions;

public class GetConnectionWaitInteruptedException extends Exception
{
	public GetConnectionWaitInteruptedException()
	{
		super("Waiting for a connection was interupted");
	}
}
