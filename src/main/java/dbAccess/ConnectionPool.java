package dbAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;

import exceptions.ConnectionCloseException;
import exceptions.DatabaseAccessError;
import exceptions.GetConnectionWaitInteruptedException;

public class ConnectionPool  {
	// Singleton object
	private static ConnectionPool pool;
	// Connection parameters
	
	String username = "admin";
	String password = "admin";
	// Collections of connections
	private HashSet<Connection> newConnections = null;
	private HashSet<Connection> usedConnections = null;
	// Constructor
	private ConnectionPool() throws DatabaseAccessError {
		// Initialize connection collections
		newConnections = new HashSet<Connection>();
		usedConnections = new HashSet<Connection>();
		String driver="org.apache.derby.jdbc.ClientDriver";
		String url = "jdbc:derby://localhost:1527/CouponDB";
		// Fill the newConnections with appropriate number of connections
		for (int i = 0; i < 5; i++) 
		{
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try
			{
				newConnections.add(DriverManager.getConnection(url));
			}catch(SQLException e)
			{
				throw new DatabaseAccessError();
			}
		}
	}
	// Methods
	public static ConnectionPool getInstance() throws DatabaseAccessError  {
		if  (pool == null) {
			pool = new ConnectionPool();
		}
		return pool;
	}
	// Returns a connection, as soon as there is at least one available
	public  synchronized Connection getConnection() throws GetConnectionWaitInteruptedException {
		// While there are no available connections - wait
		while (newConnections.iterator().hasNext()==false) {
			try {
				wait();
			}catch (InterruptedException e) {
				throw new GetConnectionWaitInteruptedException();
			}
		}
		// If there is a connection available return it
		Connection conn = newConnections.iterator().next();
		newConnections.remove(conn);
		usedConnections.add(conn);
		return conn;
	}
	// Return connection to the pool
	public synchronized void returnConnection(Connection connection) {
		newConnections.add(connection);
		usedConnections.remove(connection);
		notifyAll();	
	}
	// Closes and returns all connection to the pool
	public void closeAllConnections() throws ConnectionCloseException {
		// Close all free connections
		while(newConnections.iterator().hasNext()){
			try
			{
				newConnections.iterator().next().close();
			}catch(SQLException e)
			{
				throw new ConnectionCloseException();
			}
		}
		// Close all used connections
		while(usedConnections.iterator().hasNext()){
			try
			{
				usedConnections.iterator().next().close();
			}catch(SQLException e)
			{
				throw new ConnectionCloseException();
			}
		}
	}
}