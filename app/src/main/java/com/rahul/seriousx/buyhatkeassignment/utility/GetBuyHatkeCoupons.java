package com.rahul.seriousx.buyhatkeassignment.utility;

import com.rahul.seriousx.buyhatkeassignment.model.ContentModel;
import com.rahul.seriousx.buyhatkeassignment.retrofit.BuildRetrofit;
import com.rahul.seriousx.buyhatkeassignment.retrofit.GeneralAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 5/28/17.
 */

public class GetBuyHatkeCoupons {
    private BuyHatkeAPI api;

    public interface BuyHatkeAPI {
        void onSuccess(List<ContentModel> couponList);
        void onFailure(Throwable t);
    }

    public GetBuyHatkeCoupons(BuyHatkeAPI api) {
        this.api = api;
    }

    public void fetchList() {
        GeneralAPI generalAPI = BuildRetrofit.getInstance().create(GeneralAPI.class);
        Call<String> call = generalAPI.getBuyHatkeCoupons();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                List<ContentModel> couponList = new ArrayList<ContentModel>();
                for (String coupon: response.body().split("~")) {
                    ContentModel model = new ContentModel();
                    model.setInProgress(true);
                    model.setCoupon(coupon);
                    model.setDescription("Description for coupon " + coupon);
                    couponList.add(model);
                }
                api.onSuccess(couponList);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                api.onFailure(t);
            }
        });
    }
}
