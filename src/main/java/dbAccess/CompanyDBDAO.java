package dbAccess;

import objects.Company;
import objects.Coupon;
import objects.CouponType;
import exceptions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class CompanyDBDAO implements CompanyDAO {
	// Connection attributes
	ConnectionPool pool;

	// Constructor, throws SQLException, on failed connection attempt
	public CompanyDBDAO() throws DatabaseAccessError {
		pool = ConnectionPool.getInstance();
	}

	// Creates new company, with unique name
	@Override
	public void createCompany(Company company) throws WaitingForConnectionInterrupted, FailedToCreateCompanyException {
		// Get connection
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e1) {
			throw new WaitingForConnectionInterrupted();
		}

		// Prepare SQL message to insert new company
		String insertSQL = "INSERT INTO APP.Company " + "(EMAIL, PASSWORD, COMP_NAME) VALUES" + "(?,?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, company.getEmail());
			preparedStatement.setString(2, company.getPassword());
			preparedStatement.setString(3, company.getCompName());
			// Execute prepared Statement
			preparedStatement.executeUpdate();
			// Close statement connection
			preparedStatement.close();
		} catch (SQLException e) {
			throw new FailedToCreateCompanyException();
		} finally {
			// close connection
			pool.returnConnection(connection);
		}
		System.out.println(company.toString() + " was added to the table");
	}

	// Remove existing company
	@Override
	public void removeCompany(Company company) throws WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, ConnectionCloseException {
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		// Get company ID from DB
		String sqlRequest;
		Statement statement;
		ResultSet idFound;
		try {
			sqlRequest = "SELECT ID FROM APP.COMPANY WHERE COMP_NAME='" + company.getCompName() + "'";
			statement = connection.createStatement();
			idFound = statement.executeQuery(sqlRequest);
			idFound.next();
			company.setId(idFound.getLong("ID"));
			// Prepare message to remove purchase history
			sqlRequest = "DELETE FROM APP.COMPANY_COUPON WHERE COMP_ID = " + company.getId();
			// Remove all customer purchase history
			statement.execute(sqlRequest);
			// Prepare SQL message to remove the company
			sqlRequest = "DELETE FROM APP.COMPANY WHERE ID=" + company.getId();
			// Remove the company himself
			statement.execute(sqlRequest);
			System.out.println(company.toString() + " was deleted");
		} catch (SQLException e) {
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try {
			idFound.close();
			statement.close();
		} catch (SQLException e) {
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}

	// Update existing company
	@Override
	public void updateCompany(Company company) throws NothingToUpdateException, WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, UpdateDidNotExecuteException {
		Connection connection;
		Statement statement;
		try {
			connection = pool.getConnection();
			statement = connection.createStatement();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		} catch (SQLException e) {
			throw new ClosedConnectionStatementCreationException();
		}
		try {
			// Check that COMPANY with that name exists
			statement.execute("SELECT COMP_NAME FROM APP.COMPANY WHERE COMP_NAME = '" + company.getCompName() + "'");
		} catch (SQLException e) {
			// If such company does not exist - throw exception
			throw new NothingToUpdateException();
		}

		// Prepare SQL message to remove the company
		String updateSQL = "UPDATE APP.COMPANY SET" + " COMP_NAME='" + company.getCompName() + "', PASSWORD='"
				+ company.getPassword() + "', EMAIL='" + company.getEmail() + "' WHERE ID=" + company.getId();
		try {
			// Remove the company
			statement.execute(updateSQL);
			System.out.println(company.toString() + " was updated");
			// Close statement
			statement.close();
		} catch (SQLException e) {
			throw new UpdateDidNotExecuteException();
		}

		pool.returnConnection(connection);
	}

	// Returns Company by id
	@Override
	public Company getCompany(long id) throws WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, ConnectionCloseException {
		// Establish db connection
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare SQL message to get the company by the id
		String sql = "SELECT * FROM APP.COMPANY WHERE ID = ?";
		PreparedStatement preparedStatement;
		ResultSet rs;
		Company company;
		// Prepare and execute SELECT
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			// getting the values into a result set
			rs = preparedStatement.executeQuery();
			company = new Company();
			rs.next();
			company.setId(rs.getLong("ID"));
			company.setCompName(rs.getString("COMP_NAME"));
			company.setPassword(rs.getString("PASSWORD"));
			company.setEmail(rs.getString("EMAIL"));
			company.setCoupons((ArrayList<Coupon>) getCoupons(company));
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

		return company;
	}
	// Returns Company by Name
	@Override
	public Company getCompany(String name) throws WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, ConnectionCloseException {
		// Establish db connection
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare SQL message to get the company by the id
		String sql = "SELECT * FROM APP.COMPANY WHERE COMP_NAME= ?";
		PreparedStatement preparedStatement;
		ResultSet rs;
		Company company;
		// Prepare and execute SELECT
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, name);
			// getting the values into a result set
			rs = preparedStatement.executeQuery();
			company = new Company();
			while (rs.next()) {
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
				company.setCoupons((ArrayList<Coupon>) getCoupons(company));
			}
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

		return company;
	}

	// Returns all existing companies
	@Override
	public Collection<Company> getAllCompanies() throws WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, ConnectionCloseException {
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		// Find all companies IN DATABASE
		String sql = "SELECT * FROM APP.COMPANY ";
		PreparedStatement statement;
		ResultSet rs;
		ArrayList<Company> companies;
		// prepare and execute SELECT
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			companies = new ArrayList<Company>();
			while (rs.next()) {
				Company company = new Company();
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
				companies.add(company);
				//stem.out.println(company.toString());
			}
		} catch (SQLException e) {
			throw new ClosedConnectionStatementCreationException();
		}

		try {
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new ConnectionCloseException();
		}

		pool.returnConnection(connection);
		return companies;
	}

	// Return all coupons of a certain company
	@Override
	public Collection<Coupon> getCoupons(Company company) throws WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, ConnectionCloseException {
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		Statement statement;
		ArrayList<Coupon> coupons;
		String sql;
		ResultSet rs;
		try {
			statement = connection.createStatement();
			coupons = new ArrayList<Coupon>();
			// If company was received without ID - set ID
			if (company.getId() == 0) {
				sql = "SELECT ID, COMP_NAME, PASSWORD FROM APP.COMPANY WHERE COMP_NAME='" + company.getCompName() + "'";
				// Get company ID ResultSet
				ResultSet companySetFound = statement.executeQuery(sql);
				companySetFound.next();
				company.setId(companySetFound.getLong("ID"));
				companySetFound.close();
			}
			// Find company by ID in JOIN TABLE IN DATABASE
			sql = "SELECT * FROM (APP.COMPANY_COUPON inner join APP.COUPON on APP.COUPON.ID = APP.COMPANY_COUPON.COUPON_ID) "
					+ "WHERE COMP_ID=" + company.getId();
			rs = statement.executeQuery(sql);
			// Fill up the coupons Collection
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setId(rs.getLong("ID"));
				coupon.setImage(rs.getString("IMAGE"));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setType(CouponType.valueOf(rs.getString("COUPON_TYPE")));
				coupons.add(coupon);
				
			}
		} catch (SQLException e) {
			throw new ClosedConnectionStatementCreationException();
		}
		try {
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		return coupons;
	}

	// Returns true on success, false on fail to Log In
	@Override
	public boolean login(String compName, String password) throws WaitingForConnectionInterrupted,
			ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		Connection connection;
		boolean loginSuccess = false;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		Statement statement;
		String sqlStatement;
		ResultSet companyFound;
		try {
			statement = connection.createStatement();
			// Find company by NAME in DATABASE
			sqlStatement = "SELECT COMP_NAME,PASSWORD FROM APP.COMPANY WHERE COMP_NAME='" + compName + "'";
			companyFound = statement.executeQuery(sqlStatement);
			// If company wasn't found - next() will throw EOFException
			companyFound.next();
			// Check the password, return true on success
			if (companyFound.getString("PASSWORD").equals(password)) 
			{
				// login details are valid
				loginSuccess = true;
			}
		} catch (SQLException e) {
			throw new ClosedConnectionStatementCreationException();
		}
		// on invalid Login information - return false
		try {
			companyFound.close();
			statement.close();
		} catch (SQLException e) {
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		return loginSuccess;
	}

	public void addCoupon(Company company, Coupon newCoupon) throws ClosedConnectionStatementCreationException,
			WaitingForConnectionInterrupted, ConnectionCloseException 
	{
		// DB Connection
		Connection connection;
		try {
			connection = pool.getConnection();
		} catch (GetConnectionWaitInteruptedException e) {
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute SQL message to insert new company
		String insertSQL = "INSERT INTO APP.COMPANY_COUPON " + "(COMP_ID, COUPON_ID) VALUES" + "(?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setLong(1, company.getId());
			preparedStatement.setLong(2, newCoupon.getId());
			// Execute prepared Statement
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try {
			preparedStatement.close();
		} catch (SQLException e) {
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}
}
