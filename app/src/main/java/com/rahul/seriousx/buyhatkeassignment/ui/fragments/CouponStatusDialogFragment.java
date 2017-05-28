package com.rahul.seriousx.buyhatkeassignment.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rahul.seriousx.buyhatkeassignment.R;
import com.rahul.seriousx.buyhatkeassignment.adapters.CouponStatusRecyclerAdapter;
import com.rahul.seriousx.buyhatkeassignment.model.ContentModel;
import com.rahul.seriousx.buyhatkeassignment.ui.activities.CustomJabongWebView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 5/27/17.
 */

public class CouponStatusDialogFragment extends DialogFragment implements CustomJabongWebView.CouponStatusInterface{

    private RecyclerView recyclerView;
    private List<ContentModel> contentModelList;
    private TextView totalCoupon, triedCoupon, gotOffCouponName, gotOffText,
            couponDiscount, successCoupon;
    private View successView, statusView, failureView;
    private ProgressBar couponProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentModelList = EventBus.getDefault().getStickyEvent(ArrayList.class);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coupon_status_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView closeFragment = (ImageView) view.findViewById(R.id.close_fragment_IV);
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                EventBus.getDefault().post(true);
            }
        });
        bindViews(view);
        setData();
    }

    private void bindViews(View view) {
        totalCoupon = (TextView) view.findViewById(R.id.total_coupon_TV);
        triedCoupon = (TextView) view.findViewById(R.id.coupon_tried_TV);
        gotOffText = (TextView) view.findViewById(R.id.got_off_TV);
        gotOffCouponName = (TextView) view.findViewById(R.id.applied_coupon_TV);
        couponProgress = (ProgressBar) view.findViewById(R.id.coupon_progress_progressbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        couponDiscount = (TextView) view.findViewById(R.id.coupon_discount_TV);
        successCoupon = (TextView) view.findViewById(R.id.coupon_applied_success_TV);
        successView = view.findViewById(R.id.success_view);
        failureView = view.findViewById(R.id.failure_view);
        statusView = view.findViewById(R.id.status_view);
    }

    private void setData() {
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CouponStatusRecyclerAdapter adapter = new CouponStatusRecyclerAdapter(getContext(), contentModelList);
        recyclerView.setAdapter(adapter);
        totalCoupon.setText("/"+contentModelList.size());
        couponProgress.setMax(contentModelList.size());
        gotOffText.setText(getString(R.string.got_off_string, 0));
        gotOffCouponName.setText(getString(R.string.applied_coupon, "---"));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onStatusChange(int position) {
        couponProgress.setProgress(position + 1);
        triedCoupon.setText(""+(position + 1));
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void couponApplied(String coupon, float offAmount) {
        gotOffText.setText(getString(R.string.got_off_string, (int)offAmount));
        gotOffCouponName.setText(getString(R.string.applied_coupon, coupon));
    }

    @Override
    public void onFinish(ContentModel coupon) {
        if (coupon != null) {
            showSuccessView(coupon.getCoupon(), (int) (coupon.getOriginalPrice()-coupon.getDiscountedPrice()));
        } else {
            showFailureView();
        }
    }

    private void showFailureView() {
        failureView.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }

    private void showSuccessView(String bestCoupon, int discountPrice) {
        String text = getString(R.string.we_applied_coupon_amount, discountPrice);
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), 0, text.indexOf("OFF"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        couponDiscount.setText(spannable);
        successCoupon.setText(getString(R.string.applied_coupon, bestCoupon));
        successView.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }
}
