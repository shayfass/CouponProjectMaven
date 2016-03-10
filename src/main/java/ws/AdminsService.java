package ws;

import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import client.BusinessDelegate;
import facades.AdminFacade;
import income.Income;
import objects.ClientType;
import objects.Company;
import objects.Customer;
import system.CouponSystem;


@Path("admin")
public class AdminsService {

	@Context
	private HttpServletRequest request;

	@POST
	@Path("login/{user}/{pass}")
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@PathParam("user") String user, @PathParam("pass") String pass) {
		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}

		AdminFacade facade=null;
		String str=null;
		try {
			CouponSystem sys = CouponSystem.getInstance();
			facade = (AdminFacade) sys.login(user, pass, ClientType.admin);
			if(facade!=null){
				str="success";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("facade", facade);
		session.setAttribute("user", user);
		session.setAttribute("pass", pass);
		return str;
	}

	@GET
	@Path("getAllCompanies")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Company> getAllCompanies() throws SQLException {
		Collection<Company> companies;
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		AdminFacade facade = (AdminFacade) session.getAttribute("facade");
		try {
			companies = facade.getAllCompanies();
			
		} catch (Exception e) {
			return null;
		}
		return companies;

	}

	@POST
	@Path("AddCompany/{compName}/{mail}/{pass}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createCompany(@PathParam("compName") String compName, @PathParam("mail") String mail,
			@PathParam("pass") String pass) {

		HttpSession session = request.getSession(false);

		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			Company comp = new Company();
			comp.setCompName(compName);
			comp.setEmail(mail);
			comp.setPassword(pass);
			facade.createCompany(comp);

		} catch (Exception e) {
			return "Adding company failed. " + e.getMessage();
		}
		return "Adding company was succeessful";
	}

	@DELETE
	@Path("DeleteCompany/{id}")
	public String removeCompany(@PathParam("id") long id) {

		HttpSession session = request.getSession(false);

		Company comp;
		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			comp = facade.getCompany((int) id);
			facade.removeCompany(comp);
			

		} catch (Exception e) {
			return "company was not removed";
		}
		return "company was removed";
	}

	@PUT
	@Path("UpdateCompany/{id}/{pass}/{mail}")
	@Produces(MediaType.TEXT_PLAIN)
	public String updateCompany(@PathParam("id") long id, @PathParam("pass") String pass,
			@PathParam("mail") String mail) {

		HttpSession session = request.getSession(false);
		Company comp;

		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			comp = facade.getCompany((int) id);
			comp.setPassword(pass);
			comp.setEmail(mail);
			facade.updateCompany(comp);

		} catch (Exception e) {
			return "couldnt update company";
		}

		return "update complited";
	}

	@SuppressWarnings("null")
	@GET
	@Path("GetCompany/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Company> getCompany(@PathParam("id") long id) {
		Collection<Company> companies=null;
		Company company=new Company();
		HttpSession session = request.getSession(false);

		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			company=facade.getCompany((int) id);
		} catch (Exception e) {
			return null;
		}
		companies.add(company);
		return companies;
	}

	@GET
	@Path("getAllCustomers")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Customer> getAllCustomers() throws SQLException {
		Collection<Customer> customers;
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		AdminFacade facade = (AdminFacade) session.getAttribute("facade");
		try {
			customers = facade.getAllCustomers();
		} catch (Exception e) {
			return null;
		}
		return customers;
	}

	@POST
	@Path("AddCustomer/{CustName}/{pass}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createCustomer(@PathParam("CustName") String CustName, @PathParam("pass") String pass) {

		HttpSession session = request.getSession(false);

		
		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			Customer cust = new Customer();
			cust.setCustName(CustName);
			cust.setPassword(pass);
			facade.createCustomer(cust);

		} catch (Exception e) {
			return "Adding Customer failed. " + e.getMessage();
		}
		return "Adding Customer was succeessful";
	}

	@DELETE
	@Path("DeleteCustomer/{id}")
	public String removeCustomer(@PathParam("id") long id) {

		HttpSession session = request.getSession(false);

		
		Customer cust;
		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			cust = facade.getCustomer((int) id);
			facade.removeCustomer(cust);
			

		} catch (Exception e) {
			return null;
		}
		return "Customer was removed";
	}

	@PUT
	@Path("UpdateCustomer/{id}/{pass}")
	@Produces(MediaType.TEXT_PLAIN)
	public String updateCustomer(@PathParam("id") long id, @PathParam("pass") String pass) {

		HttpSession session = request.getSession(false);
		Customer cust;
		
		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			cust = facade.getCustomer((int) id);
			cust.setPassword(pass);
			facade.updateCustomer(cust);

		} catch (Exception e) {
			return "couldnt update Customer";
		}

		return "update complited";
	}

	@GET
	@Path("GetCustomer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer(@PathParam("id") long id) {

		HttpSession session = request.getSession(false);

		
		try {
			AdminFacade facade = (AdminFacade) session.getAttribute("facade");
			return facade.getCustomer((int) id);

		} catch (Exception e) {
			return null;
		}
	}

	@GET
	@Path("viewAllIncome")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewAllIncome(){
		BusinessDelegate bd = new BusinessDelegate();
		return bd.viewAllIncome();
	}
	
	@GET
	@Path("viewCompanyIncome/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewCompanyIncome(@PathParam("id") long id) {
		BusinessDelegate bd = new BusinessDelegate();
		return bd.viewIncomeByCompany(id);
	}
	
	@GET
	@Path("viewCustomerIncome/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewCustomerIncome(@PathParam("id") long id) {
		BusinessDelegate bd = new BusinessDelegate();
		return bd.viewIncomeByCustomer(id);
	}
}
