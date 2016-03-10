package objects;


import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Company {
	// Attributes
	// id is assigned by DB on insert
	private long id;
	private String compName;
	private String password;
	private String email;
	private ArrayList<Coupon> coupons;

	// Constructors
	public Company() {
	}
	public Company(String compName)
	{
		setCompName(compName);
	}
	// Methods
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCompName() {
		return compName;
	}
	public void setCompName(String compName) {
		if (compName != null) {
			this.compName = compName;
		}
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		if (password != null) {
			this.password = password;
		}
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		if (email != null) {
			this.email = email;
		}
	}
	public Collection<Coupon> getCoupons()   {
		return coupons;
	}
	public void setCoupons(ArrayList<Coupon> coupons) {
		this.coupons = coupons;
	}
	@Override
	public String toString() {
		return "Company ["
				+ "id=" + id 
				+ ", compName=" + compName 
				+ ", password=" + password 
				+ ", email=" + email 
				+ ", coupons=" + coupons 
				+ "]";
	}
	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Company) {
			return (this.getId() == ((Company)object).getId());
		}
		return false;
	}
}
