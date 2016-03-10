package exceptions;

public class CouponSoldOutException extends Exception
{
	public CouponSoldOutException()
	{
		super("This coupon is sold out");
	}
}
