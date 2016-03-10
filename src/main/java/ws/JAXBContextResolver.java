package ws;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import income.Income;
import objects.Company;
import objects.Coupon;
import objects.Customer;

/**
 * This helps in configuring the JSON output format.
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

    /**
     * The JAXBContext object.
     */
    private JAXBContext context;
    /**
     * The class types.
     */
    private final Class[] types = {Company.class,Coupon.class,Customer.class,Income.class};

    /**
     * Constructs the JSON output format for the specified class types.
     *
     * @throws Exception the exception
     */
    public JAXBContextResolver() throws Exception {
        this.context = new JSONJAXBContext(JSONConfiguration.mapped().arrays("coupon","company","customer","income").build(), types);
    }

    /**
     * Returns the current context for the given class.
     *     
     * @param objectType the JAXBContext for the given class object.
     * @return the JAXBContext.
     */
    @Override
    public JAXBContext getContext(final Class objectType) {
        for (Class type : types) {
            if (type == objectType) {
                return context;
            }
        }
        return null;
    }
}