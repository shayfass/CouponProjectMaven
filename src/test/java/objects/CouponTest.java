package objects;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shay on 11/03/2016.
 */
public class CouponTest {

    @Test
    public void verifyCoupon() {
        Coupon coupon = new Coupon();
        coupon.setEndDate(java.sql.Date.valueOf("2015-12-01"));
        coupon.setAmount(4);
        coupon.setMessage("this is a coupon");
        coupon.setPrice(45.0);
        coupon.setStartDate(java.sql.Date.valueOf("2015-09-13"));
        coupon.setTitle("g");
        coupon.setImage("jkggjghkj");
        coupon.setType(CouponType.PETS);

        Assert.assertEquals(4,coupon.getAmount());
        Assert.assertEquals(45.0,coupon.getPrice(),0);
        Assert.assertEquals(CouponType.PETS,coupon.getType());

    }
}
