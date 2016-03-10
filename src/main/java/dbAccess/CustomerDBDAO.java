package dbAccess;

import objects.*;
import exceptions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class CustomerDBDAO implements CustomerDAO
{
	// Connection attributes
	ConnectionPool pool;
	// Constructor, throws SQLException, on failed connection attempt
	public CustomerDBDAO() throws DatabaseAccessError
	{
		pool = ConnectionPool.getInstance();
	}
	// Creates new Customer from the passed object Customer
	@Override
	public void createCustomer(Customer customer) throws WaitingForConnectionInterrupted, 
		FailedToCreateCustomerException, ConnectionCloseException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare SQL message to insert new company
		String insertSQL = "INSERT INTO APP.CUSTOMER " + "(PASSWORD, CUST_NAME) VALUES" + "(?,?)";
		PreparedStatement preparedStatement;
		// Prepare statement and update 
		try	{
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(2, customer.getCustName());
			preparedStatement.setString(1, customer.getPassword());
			// Execute prepared Statement
			preparedStatement.executeUpdate();
			// LOG
			System.out.println(customer.toString() + " was added to the table");
		}catch(SQLException e)	{
			throw new FailedToCreateCustomerException();
		}
		// Close connections
		try	{
			preparedStatement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}
	// Remove Customer from the table
	@Override
	public void removeCustomer(Customer customer) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Get customer ID from DB
		String sqlRequest;
		Statement statement;
		ResultSet idFound;
		try	{
			sqlRequest = "SELECT ID FROM APP.CUSTOMER WHERE CUST_NAME='" + customer.getCustName() + "'";
			statement = connection.createStatement();
			idFound = statement.executeQuery(sqlRequest);
			idFound.next();
			customer.setId(idFound.getLong("ID"));
			// Prepare message to remove purchase history
			sqlRequest = "DELETE FROM APP.CUSTOMER_COUPON WHERE CUST_ID =" + customer.getId();
			// Remove all customer purchase history
			statement.execute(sqlRequest);
			// Prepare SQL message to remove the customer
			sqlRequest = "DELETE FROM APP.CUSTOMER WHERE ID=" + customer.getId();
			// Remove the customer
			statement.execute(sqlRequest);
			// LOG
			System.out.println("Customer " + customer.toString() + " was deleted");
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			idFound.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}
	// Update existing customer info BY ID
	@Override
	public void updateCustomer(Customer customer) throws WaitingForConnectionInterrupted,
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute statement
		Statement statement;
		// Prepare SQL message to remove the customer
		String updateSQL;
		// Update the customer
		try	{
			statement = connection.createStatement();
			updateSQL = "UPDATE APP.CUSTOMER SET " 
					+ "CUST_NAME='" + customer.getCustName() 
					+ "' ,PASSWORD='" + customer.getPassword() 
					+ "' WHERE ID=" + customer.getId();
			statement.execute(updateSQL);
			// LOG
			System.out.println("Customer " + customer.toString() + " was updated");
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}
	// Purchase coupon
	public void purchaseCoupon(Customer customer, Coupon coupon) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException, CouponSoldOutException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		String sqlMessage;
		PreparedStatement preparedStatement;
		Statement statement;
		ResultSet resultSet = null;
		int amountLeft = 0;
		// Check coupon availability
		try
		{
			sqlMessage = "SELECT AMOUNT FROM APP.COUPON WHERE ID = " + coupon.getId();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlMessage);
			resultSet.next();
			amountLeft = resultSet.getInt(1);
		}catch(SQLException e1)
		{
			throw new ClosedConnectionStatementCreationException();
		}
		// if there are coupons available - purchase
		if(amountLeft > 1)
		{
			amountLeft--;
			try	
			{
				// Prepare and execute SQL message to insert new company
				sqlMessage = "INSERT INTO APP.CUSTOMER_COUPON " + "(CUST_ID, COUPON_ID) VALUES" + "(?,?)";
				preparedStatement = connection.prepareStatement(sqlMessage);
				preparedStatement.setLong(1, customer.getId());
				preparedStatement.setLong(2, coupon.getId());
				// Execute prepared Statement
				preparedStatement.executeUpdate();
				// Decrease AMOUNT of available coupons by 1
				sqlMessage = "UPDATE APP.COUPON SET AMOUNT=" + amountLeft + " WHERE ID=" + coupon.getId();
				statement.executeUpdate(sqlMessage);
			}catch(SQLException e)	{
				throw new ClosedConnectionStatementCreationException();
			}
		}
		else  // throw exception
			{
				throw new CouponSoldOutException();
			}
		// Close connections
		try	{
			statement.close();
			resultSet.close();
			preparedStatement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}
	// Returns customer by ID
	@Override
	public Customer getCustomer(long id) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// Establish db connection
				Connection connection;
				try {
					connection = pool.getConnection();
				} catch (GetConnectionWaitInteruptedException e) {
					throw new WaitingForConnectionInterrupted();
				}
				// Prepare SQL message to get the company by the id
				String sql = "SELECT * FROM APP.CUSTOMER WHERE ID = ?";
				PreparedStatement preparedStatement;
				ResultSet rs;
				Customer customer;
				// Prepare and execute SELECT
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setLong(1, id);
					// getting the values into a result set
					rs = preparedStatement.executeQuery();
					customer = new Customer();
					rs.next();
					customer.setId(rs.getLong("ID"));
					customer.setCustName(rs.getString("CUST_NAME"));
					customer.setPassword(rs.getString("PASSWORD"));
				} catch (SQLException e) {
					throw new ClosedConnectionStatementCreationException();
				}
				// Close Connections
				try {
					rs.close();
					preparedStatement.close();
				} catch (SQLException e) {
					throw new ConnectionCloseException();
				}
				pool.returnConnection(connection);

				return customer;
	}
	// Returns Customer by Name
	public Customer getCustomer(String name) throws WaitingForConnectionInterrupted, 
	ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute statement
		Statement statement;
		// Create new customer to store what will be found
		Customer customerFound;
		ResultSet customerSetFound;
		try	{
			statement = connection.createStatement();
			customerFound = new Customer();
			// Find customer with ID
			customerSetFound = statement.executeQuery("SELECT * FROM APP.CUSTOMER WHERE CUST_NAME= '" + name+"'");
			// Store customer
			customerSetFound.next();
			//customerFound = (Customer)customerSetFound;
			customerFound.setId(customerSetFound.getLong("ID"));
			customerFound.setCustName(name);
			customerFound.setPassword(customerSetFound.getString("PASSWORD"));
			customerFound.setCoupons((ArrayList<Coupon>) getCoupons(customerFound));
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			customerSetFound.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		// Return it
		return customerFound;
	}
	// Return all existing customers
	@Override
	public Collection<Customer> getAllCustomers() throws WaitingForConnectionInterrupted,
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		Statement statement;
		ResultSet customersFound;
		ArrayList<Customer> allCustomers;
		try	{
			statement = connection.createStatement();
			// Get all customers from DB
			customersFound = statement.executeQuery("SELECT ID, CUST_NAME, PASSWORD FROM APP.CUSTOMER");
			// Prepare ArrayList to put all customers in
			allCustomers = new ArrayList<Customer>(); 
			// Put all customers from ResultSet into ArrayList
			while(customersFound.next())
			{
				// Prepare temp Customer
				Customer tempCustomer = new Customer();
				tempCustomer.setId(customersFound.getLong("ID"));
				tempCustomer.setCustName(customersFound.getString("CUST_NAME"));
				tempCustomer.setPassword(customersFound.getString("PASSWORD"));
				// Add it to the collection
				allCustomers.add(tempCustomer);
			}

		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			statement.close();
			customersFound.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		// Return collection of all customers
		return allCustomers;		
	}
	// Returns collection of all customer's coupons
	@Override
	public Collection<Coupon> getCoupons(Customer customer) throws WaitingForConnectionInterrupted,
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		Statement statement;
		ResultSet couponsFound;
		ArrayList<Coupon> allCoupons;
		try	{
			statement = connection.createStatement();
			// If customer was received without ID - set ID
			if(customer.getId() == 0)
			{
				// Get customer ID
				ResultSet customerSetFound = statement.executeQuery(
						"SELECT ID, CUST_NAME, PASSWORD FROM APP.CUSTOMER WHERE CUST_NAME='" 
						+ customer.getCustName() + "'");
				customerSetFound.next();
				customer.setId(customerSetFound.getLong("ID"));
				customerSetFound.close();
			}
			// Get all customer's coupons from JOIN table
			couponsFound = statement.executeQuery(
					"SELECT * FROM (APP.CUSTOMER_COUPON inner join APP.COUPON on APP.COUPON.ID = APP.CUSTOMER_COUPON.COUPON_ID) "
					+ "WHERE CUST_ID=" + customer.getId());
			// Prepare ArrayList to put coupons in
			allCoupons = new ArrayList<Coupon>();
			// Put all found coupons into Collection
			while(couponsFound.next())
			{
				// Prepare temp coupon
				Coupon tempCoupon = new Coupon();
				tempCoupon.setId(couponsFound.getLong("ID"));
				tempCoupon.setTitle(couponsFound.getString("TITLE"));
				tempCoupon.setStartDate(couponsFound.getDate("START_DATE"));
				tempCoupon.setEndDate(couponsFound.getDate("END_DATE"));
				tempCoupon.setAmount(couponsFound.getInt("AMOUNT"));
				tempCoupon.setType(CouponType.valueOf(couponsFound.getString("COUPON_TYPE")));
				tempCoupon.setMessage(couponsFound.getString("MESSAGE"));
				tempCoupon.setImage(couponsFound.getString("IMAGE"));
				tempCoupon.setPrice(couponsFound.getDouble("PRICE"));
				// Add it to the Collection
				allCoupons.add(tempCoupon);
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			statement.close();
			couponsFound.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		// Return Collection of all coupons
		return allCoupons;
	}
	// Login - return true on success, false on fail
	@Override
	public boolean login(String custName, String password) throws WaitingForConnectionInterrupted,
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		// Flag
		boolean loginSuccess = false;
		// DB Connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		Statement statement;
		ResultSet customerFound;
		try	{
			statement = connection.createStatement();
			// Find customer by NAME in DATABASE
			customerFound = statement.executeQuery("SELECT CUST_NAME, PASSWORD "
					+ "FROM APP.CUSTOMER WHERE CUST_NAME='" + custName + "'");
			// If customer wasn't found - next() will throw EOFException
			customerFound.next();
			// Check the password, return true on success
			if(customerFound.getString("PASSWORD").equals(password))
			{
				loginSuccess = true;
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			statement.close();
			customerFound.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		// 
		return loginSuccess;
	}
}
