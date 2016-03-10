package dbAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import exceptions.ClosedConnectionStatementCreationException;
import exceptions.ConnectionCloseException;
import exceptions.DatabaseAccessError;
import exceptions.WaitingForConnectionInterrupted;
import exceptions.FailedToCreateCouponException;
import exceptions.GetConnectionWaitInteruptedException;
import objects.Coupon;
import objects.CouponType;

public class CouponDBDAO implements CouponDAO {

	// Connection attributes
	ConnectionPool pool;

	// Constructor, throws Exception, on failed connection attempt
	public CouponDBDAO() throws DatabaseAccessError {
		pool = ConnectionPool.getInstance();
	}
	// Adds coupon to a coupons list
	@Override
	public void createCoupon(Coupon coupon) throws FailedToCreateCouponException, WaitingForConnectionInterrupted  {
		// Get connection
		Connection connection;
		try
		{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e1)
		{
			throw new WaitingForConnectionInterrupted();
		}

		// Prepare SQL message to insert new company
		String insertSQL = "INSERT INTO APP.COUPON (IMAGE,PRICE,MESSAGE,COUPON_TYPE,AMOUNT,END_DATE,START_DATE,TITLE) VALUES" + "(?,?,?,?,?,?,?,?)";
		PreparedStatement preparedStatement;
		try
		{
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setInt(5, coupon.getAmount());
			preparedStatement.setString(3, coupon.getMessage());
			preparedStatement.setDouble(2, coupon.getPrice());
			preparedStatement.setString(8, coupon.getTitle());
			preparedStatement.setDate(6, (java.sql.Date) coupon.getEndDate());
			preparedStatement.setDate(7, (java.sql.Date) coupon.getStartDate());
			preparedStatement.setString(1, coupon.getImage());
			preparedStatement.setString(4, coupon.getType().name());
			// Execute prepared Statement
			preparedStatement.executeUpdate();
			// Close statement connection
			preparedStatement.close();
		}catch(SQLException e)
		{
			throw new FailedToCreateCouponException();
		}finally
		{
			// close connection
			pool.returnConnection(connection);
		}
		System.out.println(coupon.toString() + " was added to the table");
	}
	// Removes relevant rows from CUSTOMER_COUPON, COMPANY_COUPON as well as coupon itself
	@Override
	public void removeCoupon(Coupon coupon) throws WaitingForConnectionInterrupted,
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Get coupon ID from DB
		Statement statement;
		ResultSet idFound;
		try	{
			statement = connection.createStatement();
			String sqlRequest = "SELECT ID FROM APP.COUPON WHERE TITLE='"+ coupon.getTitle() + "'";
			idFound = statement.executeQuery(sqlRequest);
			idFound.next();
			// coupon.setId(idFound.getLong("ID"));
			// Prepare message to remove from purchase history
			String removeSQL = "DELETE FROM APP.CUSTOMER_COUPON WHERE COUPON_ID ="
					+ coupon.getId();
			// Remove coupon from purchase history
			statement.execute(removeSQL);
			// Prepare message to remove from company's coupons
			removeSQL = "DELETE FROM APP.COMPANY_COUPON WHERE COUPON_ID="
					+ coupon.getId();
			// Remove coupon from company
			statement.execute(removeSQL);
			// Prepare SQL message to remove the Coupon
			removeSQL = "DELETE FROM APP.COUPON WHERE ID=" + coupon.getId();
			// Remove the Coupon himself
			statement.execute(removeSQL);
			System.out.println(coupon.toString() + " was deleted");
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
	// Update existing coupon
	@Override
	public void updateCoupon(Coupon coupon) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		// Establish connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		PreparedStatement preparedStatement;
		// Prepare and execute the update
		try	{
			// Prepare SQL message to remove the Coupon
			String updateSQL = "UPDATE APP.COUPON SET "
					+ "AMOUNT=?,MESSAGE=?,PRICE=?,END_DATE=?,IMAGE=? "
					+ "WHERE ID=?";
			// Prepare statement
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setInt(1, coupon.getAmount());
			preparedStatement.setString(2, coupon.getMessage());
			preparedStatement.setDouble(3, coupon.getPrice());
			preparedStatement.setDate(4, (java.sql.Date) coupon.getEndDate());
			preparedStatement.setString(5, coupon.getImage());
			preparedStatement.setLong(6, coupon.getId());
			// update the Coupon
			preparedStatement.execute();
			// Log
			System.out.println(coupon.toString() + " was updated");
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close Connections
		try	{
			preparedStatement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
	}
	// Returns coupon by ID, or NULL if such coupon does not exist
	@Override
	public Coupon getCoupon(long id) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		Connection connection;
		try
		{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)
		{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute coupon
		PreparedStatement statement;
		ResultSet rs;
		String sql;
		Coupon coupon = null;
		try	{
			
			// Prepare SQL message to get the Coupon by the id
			sql = "SELECT * FROM APP.COUPON WHERE ID=?";
			statement = connection.prepareStatement(sql);
			
			statement.setLong(1, id);
			// getting the values into a result set
			rs = statement.executeQuery();
			coupon = new Coupon();
			if(rs.next())
			{
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setId(rs.getLong("ID"));
				coupon.setImage(rs.getString("IMAGE"));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setType(CouponType.valueOf(rs.getString("COUPON_TYPE")));
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			rs.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		return coupon;
	}
	@Override
	public long getCouponid( ) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		Connection connection;
		try
		{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)
		{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute coupon
		PreparedStatement statement;
		ResultSet rs;
		String sql;
		ArrayList<Long> listid=new ArrayList<Long>();
		try	{
			
			// Prepare SQL message to get the Coupon by the id
			sql = "SELECT ID FROM APP.COUPON ";
			statement = connection.prepareStatement(sql);
			
			
			// getting the values into a result set
			rs = statement.executeQuery();
			while(rs.next())
			{
				listid.add(rs.getLong("ID"));
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			rs.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		long max=0;
		for(Long id:listid){
			if(id>max){
				max=id;
			}
		}
		pool.returnConnection(connection);
		return max;
	}
	// Returns coupon by Title
	@Override
	public Coupon getCoupon(String title) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		Connection connection;
		try
		{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)
		{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute coupon
		Statement statement;
		ResultSet rs;
		String sql;
		Coupon coupon = null;;
		try	{
			statement = connection.createStatement();
			// Prepare SQL message to get the Coupon by the id
			sql = "SELECT * FROM APP.COUPON WHERE TITLE='"+title + "'";
			// getting the values into a result set
			rs = statement.executeQuery(sql);
			coupon = new Coupon();
			if(rs.next())
			{
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setId(rs.getLong("ID"));
				coupon.setImage(rs.getString("IMAGE"));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setType(CouponType.valueOf(rs.getString("COUPON_TYPE")));
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			rs.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		return coupon;
	}
	// Returns collection of all existing coupons
	@Override
	public Collection<Coupon> getAllCoupons() throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		// Establish db connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare and execute SELECT
		Statement statement;
		ArrayList<Coupon> coupons;
		ResultSet rs;
		try	{
			statement = connection.createStatement();
			coupons = new ArrayList<Coupon>();
			String sql = "SELECT * FROM APP.COUPON  ";
			rs = statement.executeQuery(sql);
			while (rs.next()) 
			{
				Coupon coupon = new Coupon();
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("COUPON_TYPE")));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setId(rs.getLong("ID"));
				coupon.setImage(rs.getString("IMAGE"));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupons.add(coupon);
				
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try
		{
			rs.close();
			statement.close();
		}catch(SQLException e)
		{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		return coupons;
	}
	// Returns all existing coupons of a certain type
	@Override
	public Collection<Coupon> getCouponByType(CouponType couponType) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		// Establish connection
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare ArrayList to return
		ArrayList<Coupon> allCouponsFound = null;
		// Prepare and execute statement
		PreparedStatement statement = null;
		ResultSet couponsFound = null;
		// Prepare sql request
		String sqlRequest;
		try	{
			sqlRequest = "SELECT * FROM APP.COUPON WHERE COUPON_TYPE='"+ couponType + "'";
			statement = connection.prepareStatement(sqlRequest);
			// Get all coupons in a ResultSet
			couponsFound = statement.executeQuery();
			// Prepare Collection
			allCouponsFound = new ArrayList<Coupon>();
			// Move all coupons from ResultSet to an ArrayList
			while (couponsFound.next()) 
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
				tempCoupon.setPrice(couponsFound.getDouble("PRICE"));
				// Add coupon to the Collection
				allCouponsFound.add(tempCoupon);
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			couponsFound.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		// returns NULL, when no coupons found
		return allCouponsFound;
	}
	// Returns all existing coupons of a certain price
	@Override
	public Collection<Coupon> getCouponByPrice(double price) throws WaitingForConnectionInterrupted, 
		ClosedConnectionStatementCreationException, ConnectionCloseException 
	{
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare sql request
		PreparedStatement statement;
		// Prepare ArrayList to return
		ArrayList<Coupon> allCouponsFound;
		// Prepare resultSet
		ResultSet couponsFound;
		try	{
			String sqlRequest = "SELECT * FROM APP.COUPON WHERE PRICE=?";
			statement = connection.prepareStatement(sqlRequest);
			allCouponsFound = new ArrayList<Coupon>();
			statement.setDouble(1, price);
			// Get all coupons in a ResultSet
			couponsFound = statement.executeQuery();
			// Move all coupons from ResultSet to an ArrayList
			while (couponsFound.next()) {
				// Prepare temp coupon
				Coupon tempCoupon = new Coupon();
				tempCoupon.setId(couponsFound.getLong("ID"));
				tempCoupon.setTitle(couponsFound.getString("TITLE"));
				tempCoupon.setStartDate(couponsFound.getDate("START_DATE"));
				tempCoupon.setEndDate(couponsFound.getDate("END_DATE"));
				tempCoupon.setAmount(couponsFound.getInt("AMOUNT"));
				tempCoupon.setType(CouponType.valueOf(couponsFound
						.getString("COUPON_TYPE")));
				tempCoupon.setMessage(couponsFound.getString("MESSAGE"));
				tempCoupon.setPrice(couponsFound.getDouble("PRICE"));
				// Add it to the Collection
				allCouponsFound.add(tempCoupon);
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try
		{
			couponsFound.close();
			statement.close();
		}catch(SQLException e)
		{
			throw new ConnectionCloseException();
		}
			pool.returnConnection(connection);
		// returns NULL, when no coupons found
		return allCouponsFound;
	}
	// Returns all existing coupons of that their price is smaller than certain
	// price
	@Override
	public ArrayList<Coupon> getCouponTillDate(String date) throws WaitingForConnectionInterrupted,
		ClosedConnectionStatementCreationException, ConnectionCloseException
	{
		Connection connection;
		try	{
			connection = pool.getConnection();
		}catch(GetConnectionWaitInteruptedException e)	{
			throw new WaitingForConnectionInterrupted();
		}
		// Prepare ArrayList to return
		ArrayList<Coupon> allCouponsFound = new ArrayList<Coupon>();
		// Prepare and execute sql request
		String sqlRequest = "SELECT * FROM APP.COUPON WHERE END_DATE<=?";
		PreparedStatement statement = null;
		ResultSet couponsFound = null;
		try	{
			statement = connection.prepareStatement(sqlRequest);
			statement.setDate(1, java.sql.Date.valueOf(date));
			// Get all coupons in a ResultSet
			couponsFound = statement.executeQuery();
			// Move all coupons from ResultSet to an ArrayList
			while (couponsFound.next()) {
				// Prepare temp coupon
				Coupon tempCoupon = new Coupon();
				tempCoupon.setId(couponsFound.getLong("ID"));
				tempCoupon.setTitle(couponsFound.getString("TITLE"));
				tempCoupon.setStartDate(couponsFound.getDate("START_DATE"));
				tempCoupon.setEndDate(couponsFound.getDate("END_DATE"));
				tempCoupon.setAmount(couponsFound.getInt("AMOUNT"));
				tempCoupon.setType(CouponType.valueOf(couponsFound
						.getString("COUPON_TYPE")));
				tempCoupon.setMessage(couponsFound.getString("MESSAGE"));
				tempCoupon.setPrice(couponsFound.getDouble("PRICE"));
				// Add it to the Collection
				allCouponsFound.add(tempCoupon);
			}
		}catch(SQLException e)	{
			throw new ClosedConnectionStatementCreationException();
		}
		// Close connections
		try	{
			couponsFound.close();
			statement.close();
		}catch(SQLException e)	{
			throw new ConnectionCloseException();
		}
		pool.returnConnection(connection);
		// returns NULL, when no coupons found
		return allCouponsFound;
	}

}
