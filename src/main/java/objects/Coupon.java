package objects;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Coupon {
	// Attributes
	// id is assigned by DB on insert
	private long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private double price;
	private String image;

	// Constructors
	public Coupon() {
	}
	public Coupon(String title) {
		this.setTitle(title);
	}

	// Methods
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if(title != null)
			this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public CouponType getType() {
		return type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Coupon [" 
				+ "id=" + id 
				+ ", title=" + title 
				+ ", startDate=" + startDate 
				+ ", endDate=" + endDate
				+ ", amount=" + amount 
				+ ", type=" + type 
				+ ", message=" + message 
				+ ", price=" + price 
				+ ", image=" + image 
				+ "]";
	}
	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Coupon) {
			return (this.getId() == ((Coupon) object).getId());
		}
		return false;
	}

}
