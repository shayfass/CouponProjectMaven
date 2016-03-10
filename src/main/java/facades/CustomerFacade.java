package facades;

import java.util.ArrayList;
import java.util.Collection;

import objects.CouponType;
import objects.Customer;
import objects.Coupon;
import dbAccess.CouponDBDAO;
import dbAccess.CustomerDBDAO;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.DatabaseAccessError;
import exceptions.WaitingForConnectionInterrupted;

public class CustomerFacade implements ClientFacade {
	// Save Customer Data
	private Customer currentCustomer;
	// Create Data Base connections:
	// CustomerDBDAO is only used to read customers
	private CustomerDBDAO custDBDAO;
	// CouponDBDAO is only used to read coupons
	private CouponDBDAO coupDBDAO;

	// Constructor
	public CustomerFacade() 
	{
		// Instantiate db connections
		try {
			custDBDAO = new CustomerDBDAO();
			coupDBDAO = new CouponDBDAO();
		} catch (DatabaseAccessError e) {
			System.out.println(e.getMessage() + ", connection attempt failed");
		}
	}
	// Methods
	// Login
	@Override
	public ClientFacade login(String name, String password) {
		// Validate login
		try {
			// On successful login set currentCustomer data, return facade
			if (custDBDAO.login(name, password)) {
				this.currentCustomer = custDBDAO.getCustomer(name);
				return this;
			}
		} catch (WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e) {
			System.out.println(e.getMessage() + ", login failed");
		}
		// On invalid login
		return null;
	}

	// Purchase coupon
	public void purchaseCoupon(long id) {
		try {
			// Ensure coupon has an ID
			Coupon coupon = coupDBDAO.getCoupon(id);
			// Purchase
			custDBDAO.purchaseCoupon(currentCustomer, coupon);
		} catch (Exception e) {
			System.out.println(e.getMessage() + ", purchase failed");
		}
	}
	public Collection<Coupon> gettotalCoupons() 
	{
		Collection<Coupon> allCoupons = null;
		try	{
			allCoupons = coupDBDAO.getAllCoupons();
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to get coupons");
		}
		return allCoupons;
	}
	// Returns all coupons owned by the Customer
	public Collection<Coupon> getAllPurchasedCoupons() {
		// save coupons here
		Collection<Coupon> coupons = null;
		// check if there are coupons on the customer db and prints the correct
		// massage
		try {
			currentCustomer = custDBDAO.getCustomer(currentCustomer.getCustName());
			coupons = custDBDAO.getCoupons(currentCustomer);
			if (coupons.isEmpty()) {
				System.out.println("You didn't buy any coupons yet");
				return null;
			}
			System.out.println("You bought those coupons:");
			
		} catch (WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e) {
			System.out.println(e.getMessage() + ", failed to get purchased coupons");
		}
		return coupons;
	}
	// Returns all coupons of a certain type, purchased by the customer
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) {
		// get the list for all coupons from this type
		ArrayList<Coupon> CouponsByType = null;
		try {
			ArrayList<Coupon> AllCouponsByType = (ArrayList<Coupon>) coupDBDAO.getCouponByType(type);
			// get the list for all coupons for this customer
			currentCustomer = custDBDAO.getCustomer(currentCustomer.getCustName());
			ArrayList<Coupon> customerCoupons = (ArrayList<Coupon>) custDBDAO.getCoupons(currentCustomer);
			// new list for for all coupons from the same type for this customer
			CouponsByType = new ArrayList<Coupon>();
			for (Coupon coupon : customerCoupons) {

				if (AllCouponsByType.contains(coupon)) {
					CouponsByType.add(coupon);
				}
			}
		} catch (WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e) {
			System.out.println(e.getMessage() + ", failed to get coupons");
		}
		System.out.println(CouponsByType.toString());
		return CouponsByType;
	}
	// Returns Collection of coupons purchased by customer
	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) {
		ArrayList<Coupon> CouponsByPrice = null;
		// get the list for all coupons from this price
		ArrayList<Coupon> AllCouponsByPrice;
		try {
			AllCouponsByPrice = (ArrayList<Coupon>) coupDBDAO.getCouponByPrice(price);
			// get the list for all coupons for this customer
			currentCustomer = custDBDAO.getCustomer(currentCustomer.getCustName());
			ArrayList<Coupon> customerCoupons = (ArrayList<Coupon>) custDBDAO.getCoupons(currentCustomer);
			// new list for for all coupons from the same price for this
			// customer
			CouponsByPrice = new ArrayList<Coupon>();
			for (Coupon coupon : customerCoupons) {
				if (AllCouponsByPrice.contains(coupon)) {
					CouponsByPrice.add(coupon);
				}
			}
		} catch (WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e) {
			System.out.println(e.getMessage() + ", failed to get coupons");
		}
		System.out.println(CouponsByPrice.toString());
		return CouponsByPrice;
	}
}
