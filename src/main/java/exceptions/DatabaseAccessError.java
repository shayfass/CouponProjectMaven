package exceptions;

import java.sql.SQLException;

public class DatabaseAccessError extends SQLException
{
	

	public DatabaseAccessError()
	{
		super("Failed to establish connection to DB");
	}
}
