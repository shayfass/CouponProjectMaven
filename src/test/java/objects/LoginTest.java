package objects;

import exceptions.DatabaseAccessError;
import facades.AdminFacade;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shay on 11/03/2016.
 */
public class LoginTest {

    @Test
    public void loginCheck() throws DatabaseAccessError {
        AdminFacade admin = new AdminFacade();
        admin = (AdminFacade) admin.login("admin", "1234");

        Assert.assertNotNull(admin);
    }

    @Test
    public void loginNegCheck() throws DatabaseAccessError {
        AdminFacade admin = new AdminFacade();
        admin = (AdminFacade) admin.login("ad", "1234");

        Assert.assertNull(admin);
    }
}
