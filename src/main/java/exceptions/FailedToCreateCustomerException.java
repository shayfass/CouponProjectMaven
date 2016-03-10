package exceptions;

public class FailedToCreateCustomerException extends Exception
{
	public FailedToCreateCustomerException()
	{
		super("Failed to create new customer");
	}
}
