package exceptions;

public class FailedToCreateCompanyException extends Exception
{
	public FailedToCreateCompanyException()
	{
		super("Failed to create new Company");
	}
}
