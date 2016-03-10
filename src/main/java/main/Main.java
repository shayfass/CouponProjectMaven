package main;

import objects.*;
import facades.*;

// This is the Test class
public class Main
{
	public static void main(String[] args) throws Exception
	{	
		// Prepare facades
		CustomerFacade custFacade = new CustomerFacade();
		CompanyFacade compFacade = new CompanyFacade();
		AdminFacade admin = new AdminFacade();
		
		// Prepare Company and Customer
		Company company = new Company("FirstCompany");
		company.setPassword("aaa");
		company.setEmail("thecompany@gmail.com");
		Customer customer = new Customer("shay");
		customer.setPassword("1987");
		
		// Prepare Coupon
		Coupon coupon = new Coupon();
		coupon.setEndDate(java.sql.Date.valueOf("2015-12-01"));
		coupon.setAmount(4);
		coupon.setMessage("this is a coupon");
		coupon.setPrice(45.0);
		coupon.setStartDate(java.sql.Date.valueOf("2015-09-13"));
		coupon.setTitle("g");
		coupon.setImage("jkggjghkj");
		coupon.setType(CouponType.PETS);
		
		// Login into Admin Facade - on fail prints message, stops program
		//if((admin = (AdminFacade) admin.login("admin", "1234")) != null)
		//{
			// Create Company and Customer - add them to DB
			//admin.createCompany(company);
			//admin.createCustomer(customer);
		//	System.out.println("Company and Customer created");
		//}
		//else
		//{
		//	System.out.println("Admin Login Failed");
		//	return;
		//}
		
		// Login into Company Facade - on fail prints message, stops program
	if((compFacade = (CompanyFacade) compFacade.login(company.getCompName(), company.getPassword())) != null)
		{
			// Create coupon - add to DB
			//compFacade.createCoupon(coupon);
			System.out.println("Coupon created");
		}
		else
		{
			System.out.println("Company Login Failed");
			return;
		}
		
		// Login into Customer - on fail prints message, stops program
		if((custFacade = (CustomerFacade) custFacade.login(customer.getCustName(), customer.getPassword())) != null)
		{
			// Purchase coupon
			custFacade.purchaseCoupon(9);
System.out.println(custFacade.getAllPurchasedCoupons());			;
			System.out.println("Coupon purchased by the customer");
		}
		else
		{
			System.out.println("Customer Login Failed");
			return;
		}
				
		// Update Coupon
	//	coupon.setEndDate(java.sql.Date.valueOf("2016-01-01"));
		//compFacade.updateCoupon(coupon);
		
	}
}