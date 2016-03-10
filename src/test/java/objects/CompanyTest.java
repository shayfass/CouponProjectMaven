package objects;

import exceptions.DatabaseAccessError;
import facades.AdminFacade;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by shay on 11/03/2016.
 */
public class CompanyTest {

    @Test
    public void verifyGettersAndSetters() {
        Company company = new Company();

        company.setCompName("shay");
        Assert.assertEquals("shay", company.getCompName());
    }


    @Test
    public void verifyCompanyExist() throws DatabaseAccessError {

        Company company = new Company("FirstCompany");
        company.setPassword("aaa");
        company.setEmail("thecompany@gmail.com");

        AdminFacade admin = new AdminFacade();

        admin.createCompany(company);

        admin.getAllCompanies().size();

        Assert.assertTrue(admin.getAllCompanies().size() > 0);
    }
}