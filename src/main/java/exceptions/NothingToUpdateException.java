package exceptions;

public class NothingToUpdateException extends Exception
{
	public NothingToUpdateException()
	{
		super("No such client exist - nothing to udate");
	}
}
