package com.rahul.seriousx.buyhatkeassignment.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by root on 4/21/17.
 */

public interface GeneralAPI {

    @GET("/PickCoupon/FreshCoupon/getCoupons.php?pos=1")
    Call<String> getBuyHatkeCoupons();

    @GET("/PickCoupon/FreshCoupon/getCoupons.php?pos=2")
    Call<String> getMyntraCoupons();

}

