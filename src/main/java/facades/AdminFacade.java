package facades;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import dbAccess.*;
import objects.*;
import exceptions.*;

public class AdminFacade implements ClientFacade
{
	
	// Create Data Base connections:
	// CompanyDBDAO to manipulate companies
	private CompanyDBDAO compDBDAO;
	// CustomerDBDAO to manipulate customers
	private CustomerDBDAO custDBDAO;
	
	// Constructor
	public AdminFacade() throws DatabaseAccessError
	{
		// Instantiate db connections
		try
		{
			compDBDAO = new CompanyDBDAO();
			custDBDAO = new CustomerDBDAO();
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	/// Methods
	// Login method, on successful login returns ClientFacade object
	@Override
	public ClientFacade login(String name, String password) throws DatabaseAccessError
	{
		// If username/password wrong - throw exception
		if(name.equals("admin")  && password.equals("1234"))
		{
			return this;
		}
		System.out.println("Wrong user/passowrd combination, try again");
		// else - create and return new Facade Object
		return null;
	}
	// Create new company
	public void createCompany(Company newCompany)
	{
		try	{
			compDBDAO.createCompany(newCompany);
		}catch(WaitingForConnectionInterrupted | FailedToCreateCompanyException e)	{
			System.out.print(e.getMessage() + ", company wasn't created");
		}
	}
	// Removes company and all its coupons, if company exists
	public void removeCompany(Company company)
	{
		try	{
			compDBDAO.removeCompany(company);
		}catch(WaitingForConnectionInterrupted | ConnectionCloseException 
				| ClosedConnectionStatementCreationException e)	{
			System.out.print(e.getMessage() + ", company wasn't removed");
		}
	}
	// Update existing company
	public void updateCompany(Company company)
	{
		try	{
			compDBDAO.updateCompany(company);
		}catch(NothingToUpdateException | WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException | UpdateDidNotExecuteException e)	{
			System.out.print(e.getMessage() + ", company wasn't updated");
		}
	}
	// Find Company by id
	public Company getCompany(int id)
	{
		Company company = null;
		try	{
			company = compDBDAO.getCompany(id);
		}catch(WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException | ConnectionCloseException e)	{
			System.out.print(e.getMessage() + ", failed to get company");
		}
		return company;
	}
	// Returns Collection<Company> of all existing companies
	public Collection<Company> getAllCompanies()
	{
		Collection<Company> allCompanies = null;
		try	{
			allCompanies = compDBDAO.getAllCompanies();
		}catch(WaitingForConnectionInterrupted 
				| ClosedConnectionStatementCreationException | ConnectionCloseException e)	{
			System.out.print(e.getMessage() + ", failed to get companies collection");
		}
		return allCompanies;
	}
	// Create new Customer
	public void createCustomer(Customer newCustomer)
	{
		try	{
			custDBDAO.createCustomer(newCustomer);
		}catch(WaitingForConnectionInterrupted
				| FailedToCreateCustomerException | ConnectionCloseException e)	{
			System.out.print(e.getMessage() + ", failed to create customer");
		}

	}
	// Removes customer and all his coupons, if exists
	public void removeCustomer(Customer customer)
	{
		try	{
			custDBDAO.removeCustomer(customer);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to remove customer");
		}
	}
	// Update existing customer
	public void updateCustomer(Customer customer)
	{
		try	{
			custDBDAO.updateCustomer(customer);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + ", failed to update customer");
		}
	}
	// Find Customer by id
	public Customer getCustomer(int id)
	{
		Customer customer = null;
		try	{
			customer = custDBDAO.getCustomer(id);
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)	{
			System.out.println(e.getMessage() + "failed to get customer");
		}
		return customer;
	}
	// Returns Collection<Customer> of all existing customers
	public Collection<Customer> getAllCustomers()
	{
		ArrayList<Customer> allCustomers = null;
		try
		{
			allCustomers = (ArrayList<Customer>) custDBDAO.getAllCustomers();
		}catch(WaitingForConnectionInterrupted
				| ClosedConnectionStatementCreationException
				| ConnectionCloseException e)
		{
			System.out.println(e.getMessage() + "failed to get customers collection");
		}
		return allCustomers;
	}
}
