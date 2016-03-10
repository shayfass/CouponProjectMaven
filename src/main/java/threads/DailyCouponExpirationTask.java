package threads;

import dbAccess.CouponDBDAO;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.DatabaseAccessError;
import exceptions.WaitingForConnectionInterrupted;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Thread;
import java.sql.SQLException;

import objects.Coupon;

public class DailyCouponExpirationTask implements Runnable {
	// Attributes
	private long millis = 60 * 60 * 24 * 1000;
	private CouponDBDAO coupDBDAO;
	// Constructor
	public DailyCouponExpirationTask() throws DatabaseAccessError
	{
		try	{
			coupDBDAO = new CouponDBDAO();
		}catch(SQLException e)	{
			System.out.println(e.getMessage() + ", failed to establish connection to DB");
		}
	}
	// Runs in separate thread
	@Override
	public void run() {
			dailyCouponsExpirationTask();
		System.out.println("going to sleep");
		try	{
			Thread.sleep(millis);
		}catch(InterruptedException e)	{
			System.out.println(e.getMessage());
		}
		System.out.println("wake up");
	}
	// Removes irrelevant coupons from the system
	private void dailyCouponsExpirationTask() 
	{
		ArrayList<Coupon> allCouponsFound = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(sdf.format(date));
		try	{
			allCouponsFound = coupDBDAO.getCouponTillDate(sdf.format(date));
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to access DB");
		}
		System.out.println(allCouponsFound.toString());
		for (Coupon coupon : allCouponsFound) {
			try	{
				coupDBDAO.removeCoupon(coupon);
			}catch(WaitingForConnectionInterrupted
					| ClosedConnectionStatementCreationException
					| ConnectionCloseException e)	{
				System.out.println(e.getMessage() + ", failed to remove old coupon because of system exception");
			}

		}
	}
	// Stops thread
	public void stopTask() {
		Thread.currentThread().interrupt();
	}

}
