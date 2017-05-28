package com.rahul.seriousx.buyhatkeassignment.model;

/**
 * Created by root on 5/26/17.
 */

public class ContentModel {
    private float originalPrice;
    private float discountedPrice;
    private String coupon;
    private String description;
    private boolean isValid;
    private boolean inProgress;

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setDiscountedPrice(float discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public float getDiscountedPrice() {
        return discountedPrice;
    }

    public String getCoupon() {
        return coupon;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
