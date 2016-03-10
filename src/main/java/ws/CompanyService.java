package ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import client.BusinessDelegate;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.FailedToCreateCouponException;
import facades.CompanyFacade;
import income.Income;
import income.IncomeType;
import objects.ClientType;
import objects.Coupon;
import objects.CouponType;
import system.CouponSystem;

@Path("company")
public class CompanyService {
	
	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse responce;

	@POST
	@Path("login/{user}/{pass}")
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@PathParam("user") String user, @PathParam("pass") String pass) {
		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}
		String str = null;
		CompanyFacade facade;
		try {
			CouponSystem sys = CouponSystem.getInstance();
			facade = (CompanyFacade) sys.login(user, pass, ClientType.company);
			if (facade != null) {
				str = "success";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("facade", facade);
		session.setAttribute("user", user);
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
		CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
		try {
			coupons = facade.getAllCoupons();

		} catch (Exception e) {
			return null;
		}
		return coupons;
	}

	@POST
	@Path("createCoupon/{amount}/{startD}/{startM}/{startY}/{endD}/{endM}/{endY}/{message}/{title}/{type}/{price}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createCoupon(@PathParam("amount") int amount, @PathParam("startD") int startD,
			@PathParam("startM") int startM, @PathParam("startY") int startY, @PathParam("endD") int endD,
			@PathParam("endM") int endM, @PathParam("endY") int endY, @PathParam("message") String message,
			@PathParam("title") String title, @PathParam("type") CouponType type, @PathParam("price") double price)
					throws ConnectionCloseException, ClosedConnectionStatementCreationException,
					FailedToCreateCouponException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
		Coupon coupon = new Coupon();

		String startDate = startY + "-" + startM + "-" + startD;
		String endDate = endY + "-" + endM + "-" + endD;
		coupon.setAmount(amount);
		coupon.setEndDate(java.sql.Date.valueOf(endDate));
		coupon.setMessage(message);
		coupon.setStartDate(java.sql.Date.valueOf(startDate));
		coupon.setPrice(price);
		coupon.setType(type);
		coupon.setTitle(title);

		facade.createCoupon(coupon);

		//persist income
		Income income = new Income();
		income.setName("New coupon added");
		income.setDescription(IncomeType.COMPANY_NEW_COUPON);
		income.setAmount(100);
		income.setDate(Calendar.getInstance().getTime());
		BusinessDelegate bd = new BusinessDelegate();
		bd.storeIncome(income);
		
		return "created";
	}

	@PUT
	@Path("updateCoupon/{id}/{amount}/{endD}/{endM}/{endY}/{message}/{price}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateCoupon(@PathParam("id") long id, @PathParam("amount") int amount, @PathParam("endD") int endD,
			@PathParam("endM") int endM, @PathParam("endY") int endY, @PathParam("message") String message,
			@PathParam("price") double price) {
		HttpSession session = request.getSession(false);

		if (session == null) {

			request.getSession(false).invalidate();

		}

		try {
			CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
			Coupon coupon = new Coupon();
			coupon = facade.getCoupon((int) id);
			String endDate = endY + "-" + endM + "-" + endD;
			coupon.setAmount(amount);
			coupon.setEndDate(java.sql.Date.valueOf(endDate));
			coupon.setMessage(message);
			coupon.setPrice(price);
			facade.updateCoupon(coupon);
		} catch (Exception e) {
			return "update Failed " + e.getMessage();
		}
		
		//persist income
		Income income = new Income();
		income.setName("A coupon was updated");
		income.setDescription(IncomeType.COMPANY_UPDATE_COUPON);
		income.setAmount(10);
		income.setDate(Calendar.getInstance().getTime());
		BusinessDelegate bd = new BusinessDelegate();
		bd.storeIncome(income);
		
		return "Coupon was updated successfuly.";
	}

	@DELETE
	@Path("removeCoupon/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String removeCoupon(@PathParam("id") long id) {
		HttpSession session = request.getSession(false);

		if (session == null) {

			request.getSession(false).invalidate();

		}

		try {
			CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
			Coupon coupon = null;
			coupon = facade.getCoupon((int) id);
			facade.removeCoupon(coupon);
		} catch (Exception e) {
			return "remove Failed " + e.getMessage();
		}
		return "Coupon was removed successfuly.";
	}

	@GET
	@Path("getTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StringWrapper> getCouponTypes() {

		List<StringWrapper> types = new ArrayList<>();
		CouponType[] ctArray = CouponType.values();

		for (int i = 0; i < ctArray.length; i++) {

			StringWrapper type = new StringWrapper();
			type.setValue(ctArray[i].name());
			types.add(type);
		}
		return types;
	}

	@POST
	@Path("ByType/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCouponsByType(@PathParam("type") CouponType type) {

		Collection<Coupon> coupons;
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.getSession(false).invalidate();
		}

		CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
		try {
			coupons = facade.getCouponByType(type);
		} catch (Exception e) {
			return null;
		}
		return coupons;
	}

	@GET
	@Path("ById/{id}") // JSon Coupon
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon getCouponsById(@PathParam("id") long id) {

		Coupon coupon;
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.getSession(false).invalidate();
		}

		CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
		try {
			coupon = facade.getCoupon((int) id);
		} catch (Exception e) {
			return null;
		}
		return coupon;
	}

	@GET
	@Path("Bydate/{year}/{month}/{day}") // JSon Coupon
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCouponsByDate(@PathParam("year") String year, @PathParam("month") String month,
			@PathParam("day") String day) {
		Collection<Coupon> coupons;
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.getSession(false).invalidate();
		}

		CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
		try {
			String date = year + "-" + month + "-" + day;
			coupons = facade.getCouponTillDate(date);
			
		} catch (Exception e) {
			return null;
		}
		return coupons;
	}

	// image upload service
	@POST
	@Path("imageUpload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImg(@FormDataParam("file") InputStream input,
			@FormDataParam("file") FormDataContentDisposition fileDetails, @FormDataParam("title") String title) {

		CompanyFacade facade = (CompanyFacade) request.getSession(false).getAttribute("facade");

		// Save File
		
		//check this i might be wring -- roey code
		String location= request.getSession().getServletContext().getRealPath("") + "/images/"
				+ fileDetails.getFileName();
		
		// check check up up 
		boolean result = saveFile(input, location);

		// Update coupon's images column
		// Free of charge :)
		Coupon coupon = new Coupon();
		try {
			long id=facade.getCouponid();
			coupon = facade.getCoupon((int) id);
			coupon.setImage("images/" + fileDetails.getFileName());
			facade.updateCoupon(coupon);
		} catch (Exception e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
		if (!result) {
			return Response.status(500).entity("File System Problem").build();
		}
		//return Response.status(200).build();
		return null;
	}

	// save file to server
	private boolean saveFile(InputStream input, String loc) {
		try {
			FileOutputStream out = new FileOutputStream(new File(loc));
			int read = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(loc));
			while ((read = input.read(bytes)) > -1) {
				out.write(bytes, 0, read);
			}
			input.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	// TODO Add web service
	// view generated income
	@GET
	@Path("viewIncome")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewIncome(){
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.getSession(false).invalidate();
		}
		CompanyFacade facade = (CompanyFacade) session.getAttribute("facade");
		BusinessDelegate bd = new BusinessDelegate();
		return bd.viewIncomeByCompany(facade.getCompanyId());
	}
}
