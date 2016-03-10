package system;

import java.sql.SQLException;

import dbAccess.ConnectionPool;
import exceptions.ConnectionCloseException;
import exceptions.FailedToCloseAllConnectionsException;
import exceptions.FailedToJoinThreadInterruptedException;
import threads.DailyCouponExpirationTask;
import facades.*;
import objects.ClientType;

public class CouponSystem {
	// Singleton
	private static CouponSystem system;
	private static ConnectionPool pool;
	private CustomerFacade cust = new CustomerFacade();
	private CompanyFacade comp = new CompanyFacade();
	private AdminFacade admin = new AdminFacade();
	 DailyCouponExpirationTask thread=new  DailyCouponExpirationTask();
	Thread task=new Thread(thread);
	// method getINstance
	public static CouponSystem getInstance() throws SQLException {
		if (system == null) {
			system = new CouponSystem();
		}
		return system;
	}

	private CouponSystem() throws SQLException {

	}

	public ClientFacade login(String name, String password, ClientType ClientType) throws SQLException 
	{
		if (ClientType ==ClientType.customer) {
			return cust.login(name, password);
		} else if (ClientType ==ClientType.company) {
			return comp.login(name, password);
		} else
			return admin.login(name, password);
	}
	public void shutDown() throws FailedToCloseAllConnectionsException, FailedToJoinThreadInterruptedException
	{
		// close all open DB connections
		try	{
			pool.closeAllConnections();
		}catch(ConnectionCloseException e)	{
			throw new FailedToCloseAllConnectionsException();
		}
		// wait for task thread to finish
		try	{
			task.join();
		}catch(InterruptedException e)	{
			throw new FailedToJoinThreadInterruptedException();
		}
		thread.stopTask();
	}
}
