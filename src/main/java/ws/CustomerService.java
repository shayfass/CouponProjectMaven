package ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import client.BusinessDelegate;
import facades.CustomerFacade;
import income.Income;
import income.IncomeType;
import objects.ClientType;
import objects.Coupon;
import objects.CouponType;
import system.CouponSystem;

@Path("/customer")
public class CustomerService {
	
	@Context
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	@POST
	@Path("login/{user}/{pass}")
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@PathParam("user") String user, @PathParam("pass") String pass) {

		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}
String str=null;
		CustomerFacade facade;
		try {
			CouponSystem sys = CouponSystem.getInstance();
			facade = (CustomerFacade) sys.login(user, pass, ClientType.customer);
			if(facade!=null){
				str="success";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		session = request.getSession(true);
		session.setAttribute("facade", facade);
		session.setAttribute("user", user);
		session.setAttribute("pass", pass);
		return str;
	}

	@GET
	@Path("getAllCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllCoupons() {

		Collection<Coupon> coupons;
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		CustomerFacade facade = (CustomerFacade) session.getAttribute("facade");
		try {
			coupons = facade.getAllPurchasedCoupons();
			
		} catch (Exception e) {
			return null;
		}
		return coupons;
	}
	@GET
	@Path("gettotalCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> gettotalCoupons() {

		Collection<Coupon> coupons;
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		CustomerFacade facade = (CustomerFacade) session.getAttribute("facade");
		try {
			coupons = facade.gettotalCoupons();
			
		} catch (Exception e) {
			return null;
		}
		return coupons;
	}

	@POST
	@Path("ByPrice/{price}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllPurchasedCouponsByPrice(@PathParam("price") double price) {

		Collection<Coupon> coupons;
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		CustomerFacade facade = (CustomerFacade) session.getAttribute("facade");
		try {
			coupons = facade.getAllPurchasedCouponsByPrice(price);
		} catch (Exception e) {
			return null;
		}
		return coupons;
	}

	@GET
	@Path("getTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StringWrapper> getCouponTypes() {
		
		List<StringWrapper> types = new ArrayList<>();
		CouponType[] ctArray = CouponType.values(); 
		
		for (int i=0;i<ctArray.length;i++) {
			
			StringWrapper type =  new StringWrapper();			
			type.setValue(ctArray[i].name());
			types.add(type);
		}		
		return types;
	}
	
	@POST
	@Path("ByType/{type}") // JSon Coupon
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllPurchasedCouponsByType(@PathParam("type") CouponType type) {

		Collection<Coupon> coupons;
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.getSession(false).invalidate();
		}

		CustomerFacade facade = (CustomerFacade) session.getAttribute("facade");
		try {
			coupons = facade.getAllPurchasedCouponsByType(type);
		} catch (Exception e) {
			return null;
		}
		return coupons;
	}

	@PUT
	@Path("purchaseCoupon/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purchaseCoupon(@PathParam("id")long id) {
		HttpSession session = request.getSession(false);
		CustomerFacade facade;
		if (session == null) {
			request.getSession(false).invalidate();
		}

		try {
			facade = (CustomerFacade) session.getAttribute("facade");
			facade.purchaseCoupon(id);
		} catch (Exception e) {
			return "Purchase Failed " + e.getMessage();
		}
		//persist the income
		ArrayList<Coupon> coupons = (ArrayList<Coupon>)facade.getAllPurchasedCoupons();
		Income income;
		for(Coupon c:coupons)
		{
			if(c.getId() == id)
			{
				income = new Income();
				income.setName("Customer purchase");
				income.setDescription(IncomeType.CUSTOMER_PURCHASE);
				income.setAmount(c.getPrice());
				income.setDate(Calendar.getInstance().getTime());
				BusinessDelegate bd = new BusinessDelegate();
				bd.storeIncome(income);
				break;
			}
		}
		return "Coupon was purchased successfuly.";
	}
}
