package ws;

import income.Income;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IncomeWrapper {
	
	private String id;
	private String name;
	private String date;
	private String type;
	private String amount;
	
	public IncomeWrapper() {}
	
	public IncomeWrapper(Income i) {
		this.id = i.getId()+"";
		this.name = i.getName();
		this.date=i.getDate().toString();
		this.type = i.getDescription().getDescription();
		this.amount = i.getAmount() +"";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}	
}
