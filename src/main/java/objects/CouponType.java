package objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum CouponType {
	RESTURANTS, 
	ELECTRICITY, 
	FOOD, 
	HEALTH, 
	SPORTS, 
	CAMPING, 
	TRAVELLING, 
	PETS;
}
