package facades;

import java.util.ArrayList;
import java.util.Collection;
import objects.*;
import dbAccess.*;
import exceptions.*;

public class CompanyFacade implements ClientFacade 
{
	// Prepare company to store for a session
	private Company currentCompany;
	// CouponDBDAO to manipulate coupons
	private CouponDBDAO coupDBDAO;
	// CompanyDBDAO to add to COMPANY_COUPON
	private CompanyDBDAO compDBDAO;

	// Constructor
	public CompanyFacade() throws DatabaseAccessError 
	{
		// Instantiate DB connections
		try
		{
			coupDBDAO = new CouponDBDAO();
			compDBDAO = new CompanyDBDAO();
		}catch(DatabaseAccessError e)
		{
			System.out.println(e.getMessage() + ", failed to connect");
		}
	}
	// Methods
	// Login method, on successful login returns ClientFacade object
	// or throws an exception
	@Override
	public ClientFacade login(String name, String password) throws DatabaseAccessError 
	{
		try
		{
			// sets currentCompany data and returns the facade on success
			if(compDBDAO.login(name, password))
			{
				this.currentCompany = compDBDAO.getCompany(name);
				return this;
			}
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e1)
		{
			System.out.println(e1.getMessage() + ", login failed");
		}
		// on invalid login
		return null;
	}
	// Create new coupon
	public void createCoupon(Coupon newCoupon) throws ConnectionCloseException, 
		ClosedConnectionStatementCreationException, FailedToCreateCouponException
	{
		Coupon coupon;
		try	{
			// Create coupon
			coupDBDAO.createCoupon(newCoupon);
			// Provide coupon ID from DB
			coupon = coupDBDAO.getCoupon(newCoupon.getTitle());
			// Add coupon to Company's coupons list
			compDBDAO.addCoupon(currentCompany, coupon);
		}catch(WaitingForConnectionInterrupted e)	{
			System.out.println(e.getMessage() + ", failed to create coupon");
		}
	}
	// Removes coupon, if it exists
	public void removeCoupon(Coupon coupon)
	{
		try	{
			coupDBDAO.removeCoupon(coupon);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to remove coupon");
		}
	}
	// Update existing coupon
	public void updateCoupon(Coupon coupon)
	{
		try	{
			Coupon couponTemp =new Coupon();
			couponTemp=coupDBDAO.getCoupon(coupon.getTitle());
			coupon.setId(couponTemp.getId());
			coupDBDAO.updateCoupon(coupon);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to update coupon");
		}
	}
	// Find Coupon by id, in company's coupons
	public Coupon getCoupon(int id) 
	{
		Coupon coupon = null;
		try	{
			coupon = coupDBDAO.getCoupon(id);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)		{
			System.out.println(e.getMessage() + ", failed to update coupon");
		}
		return coupon;
	}
	public long getCouponid() 
	{
		long id = 0;
		try	{
			id = coupDBDAO.getCouponid();
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)		{
			System.out.println(e.getMessage() + ", failed to update coupon");
		}
		return id;
	}
	// Returns Collection<Coupon> of all existing coupons of the company
	public Collection<Coupon> getAllCoupons() 
	{
		ArrayList<Coupon> allCoupons = null;
		try	{
			currentCompany = compDBDAO.getCompany(currentCompany.getCompName());
			allCoupons = (ArrayList<Coupon>) compDBDAO.getCoupons(currentCompany);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to get coupons");
		}
		return allCoupons;
	}
	public Coupon getCoupon(String title) 
	{
		Coupon coupon = null;
		try	{
			coupon = coupDBDAO.getCoupon(title);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)		{
			System.out.println(e.getMessage() + ", failed to update coupon");
		}
		return coupon;
	}
	public long getCompanyId(){
		return currentCompany.getId();
	}
	
	// Returns Collection<Coupon> of all existing coupons of the company of a certain type
	public Collection<Coupon> getCouponByType(CouponType type) 
	{
		ArrayList<Coupon> CouponsByType = null;
		try	{
			currentCompany = compDBDAO.getCompany(currentCompany.getCompName());
			ArrayList<Coupon> AllCouponsByType = (ArrayList<Coupon>) coupDBDAO.getCouponByType(type);
			ArrayList<Coupon> companyCoupons = (ArrayList<Coupon>) currentCompany.getCoupons();
			CouponsByType = new ArrayList<Coupon>();
			for (Coupon coupon : companyCoupons) {
				if (AllCouponsByType.contains(coupon)) {
					CouponsByType.add(coupon);
				}
			}
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to get coupons collection");
		}
		return CouponsByType;
	}
	// Returns Collection<Coupon> of all existing coupons of the company valid until date
	public ArrayList<Coupon> getCouponTillDate(String date) 
	{
		ArrayList<Coupon> CouponsByDate = null;
		try
		{
			currentCompany = compDBDAO.getCompany(currentCompany.getCompName());
			ArrayList<Coupon> AllCouponsByDate = (ArrayList<Coupon>) coupDBDAO.getCouponTillDate(date);
			ArrayList<Coupon> companyCoupons = (ArrayList<Coupon>) currentCompany.getCoupons();
			CouponsByDate = new ArrayList<Coupon>();
			for (Coupon coupon : companyCoupons) 
			{
				if (AllCouponsByDate.contains(coupon)) 
				{
					CouponsByDate.add(coupon);
				}
			}
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to get coupons");
		}
		return CouponsByDate;
	}
}
