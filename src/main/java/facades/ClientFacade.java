package facades;

import exceptions.DatabaseAccessError;

public interface ClientFacade
{
	// Abstract method login
	// On successful login should return ClientFacade object
	public ClientFacade login(String name, String password) throws DatabaseAccessError;
}
