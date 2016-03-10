package dbAccess;


import java.util.Collection;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.WaitingForConnectionInterrupted;
import exceptions.FailedToCreateCustomerException;
import objects.Coupon;
import objects.Customer;

public interface CustomerDAO {

	 void createCustomer(Customer customer) throws WaitingForConnectionInterrupted, FailedToCreateCustomerException, ConnectionCloseException;
	 void removeCustomer(Customer customer) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	 void updateCustomer(Customer customer) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	 Customer getCustomer(long id) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	 Collection<Customer> getAllCustomers() throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	 Collection<Coupon> getCoupons(Customer customer) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	 public Customer getCustomer(String name) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
	 boolean login(String custName,String password) throws WaitingForConnectionInterrupted, ClosedConnectionStatementCreationException, ConnectionCloseException;
}
