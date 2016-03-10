package income;

import java.util.Collection;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;



@Stateless(name="incomeService")
public class IncomeServiceBean implements IncomeService {

	@PersistenceContext(unitName="couponSystem") 
	private EntityManager em;
	
    public IncomeServiceBean() {	}
    
    
    public void storeIncome(Income income){
    	em.persist(income);
    }
    
    public Collection<Income> viewAllIncome() {
    	Query query = em.createQuery("SELECT i FROM Income AS i");
		return (Collection<Income>)query.getResultList();
    }
    
    public Collection<Income> viewIncomeByCustomer(long customerId)
    {
    	Query query = em.createQuery("SELECT i FROM Income AS i WHERE i.id = :custId");
    	query.setParameter("custId", customerId);
		return (Collection<Income>)query.getResultList();
    }
    
    public Collection<Income> viewIncomeByCompany(long companyId)
    {
    	return viewIncomeByCustomer(companyId);
    }
}
