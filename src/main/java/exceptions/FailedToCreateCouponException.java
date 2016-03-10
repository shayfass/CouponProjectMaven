package exceptions;

public class FailedToCreateCouponException extends Exception
{
	public FailedToCreateCouponException()
	{
		super("Failed to create coupon");
	}
}
