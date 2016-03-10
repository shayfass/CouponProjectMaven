package dbAccess;

import java.util.Collection;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.WaitingForConnectionInterrupted;
import exceptions.FailedToCreateCouponException;
import objects.Coupon;
import objects.CouponType;

public interface CouponDAO 
{
	void createCoupon(Coupon Coupon) throws WaitingForConnectionInterrupted, ConnectionCloseException, FailedToCreateCouponException;
	void removeCoupon(Coupon Coupon) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException  ;
	void updateCoupon(Coupon Coupon) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException  ;
	Coupon getCoupon(long id) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException  ;
	Collection<Coupon> getAllCoupons() throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException  ;
	Collection<Coupon> getCouponByType(CouponType couponType) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException ;
	Collection<Coupon> getCouponByPrice(double price) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException  ;
	Collection<Coupon> getCouponTillDate(String date) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException ;
	Coupon getCoupon(String title) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	long getCouponid() throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException,
			ConnectionCloseException;
}
