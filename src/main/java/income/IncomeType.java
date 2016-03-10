package income;

public enum IncomeType {
	CUSTOMER_PURCHASE("Customer bought a coupon"), 
	COMPANY_NEW_COUPON("Company added new coupon"), 
	COMPANY_UPDATE_COUPON("Company updated a coupon");
	
	private String description;

    private IncomeType(String descrition) {
    	this.description = description;
    }
	
    public String getDescription()
    {
    	return this.description;
    }
}
