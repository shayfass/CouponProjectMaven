package dbAccess;

import java.util.Collection;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.WaitingForConnectionInterrupted;
import exceptions.FailedToCreateCompanyException;
import exceptions.NothingToUpdateException;
import exceptions.UpdateDidNotExecuteException;
import objects.Company;
import objects.Coupon;
import objects.*;

public interface CompanyDAO {
	
	void createCompany(Company company) throws WaitingForConnectionInterrupted, FailedToCreateCompanyException;
	void removeCompany(Company company) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	void updateCompany(Company company) throws NothingToUpdateException, WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, UpdateDidNotExecuteException;
	Company getCompany(long id) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	Collection<Company> getAllCompanies() throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	Collection<Coupon> getCoupons(Company company) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	boolean login(String compName,String password) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	Company getCompany(String name) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;

}