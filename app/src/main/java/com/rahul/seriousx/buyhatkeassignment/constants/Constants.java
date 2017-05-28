package com.rahul.seriousx.buyhatkeassignment.constants;

/**
 * Created by root on 5/27/17.
 */

public class Constants {
    public static class JabongConstants {
        public static final String GET_DISCOUNT_PRICE_JS = "document.querySelector('.total-amt').getElementsByClassName('rupee')[0].innerText";
        public static final String GET_ORIGINAL_PRICE_JS = "document.querySelector('.summary-content').getElementsByClassName('standard-price')[0].innerText";
        public static final String HAVE_COUPON_JS = "document.querySelector('.have-a-coupon').click();";
        public static final String REMOVE_COUPON_JS = "document.querySelector('.remove-coupon').click();";
        public static final String APPLY_COUPON_JS = "document.querySelector('.jbApplyCoupon').click();";
        public static final String FILL_COUPON_JS = "document.getElementById('applyCoupon').value = '%s';";
        public static final CharSequence COUPON_PAGE_IDENTIFIER = "jabong.com/cart/coupon";
        public static final CharSequence CART_PAGE_IDENTIFIER = "jabong.com/cart";
        public static final CharSequence REMOVE_COUPON_IDENTIFIER = "jabong.com/cart/removecoupon";
        public static final CharSequence COUPON_APPLIED_RESPONSE_IDENTIFIER = "analytics.jabong.com/klickstorm";
        public static final CharSequence COUPON_APPLIED_IDENTIFIER = "jabong.com/cart/applycoupon/";
        public static final String EMPTY_CART_CHECK_JS = "var element = document.querySelector('.empty-cart');JSInterface.isApplyCouponExists(element);";
        public static final java.lang.String ERROR_MESSAGE_JS = "var element = document.querySelector('.error-msg');JSInterface.checkForErrorCoupon(element);";
    }
}
